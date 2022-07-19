/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.client;

import com.egls.client.audio.AudioLoader;
import com.egls.client.chat.*;
import com.egls.client.chat.bean.ChatGroupInfo;
import com.egls.client.chat.bean.MemberInfo;
import com.egls.client.extgui.*;
import com.egls.client.netmgr.CmdPkg;
import org.mini.apploader.AppManager;
import org.mini.glfm.Glfm;
import org.mini.gui.*;
import org.mini.gui.event.GActionListener;
import org.mini.gui.event.GFocusChangeListener;
import org.mini.gui.event.GKeyboardShowListener;
import org.mini.gui.event.GSizeChangeListener;
import org.mini.layout.UITemplate;
import org.mini.layout.XContainer;
import org.mini.layout.XEventHandler;
import org.mini.layout.XmlExtAssist;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gust
 */
public class BbChatUI implements ChatStateListener {

    BbClient bbClient;

    Map<Long, GImage> headCache = new LinkedHashMap() {
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            if (headCache.size() > 100) {
                return true;
            }
            return false;
        }
    };

    GForm form;

    GMenu menu;
    GPanel chatRoot;

    GViewSlot chatSlots;
    GViewPort gameView;
    GViewPort myView;

    ChatEventHandler eventHandler;
    XContainer menuContainer;

    GTextField nameField;

    GTextBox msgBox;

    GTextField search;
    //
    GSessionList sessionList;
    GContentView contentView;
    int curPreMsgPageId = -1, curNextMsgPageId = -1;
    GSessionItem curSelectedItem;

    //GFrame addFrame;
    GButton moreBtn;
    GButton mediaBtn;
    //
    GList moreMenu;

    static final int PICK_PHOTO = 101, PICK_CAMERA = 102, PICK_QR = 103, PICK_HEAD = 104;
    static float menuH = 60, pad = 2;


    static final String REQUEST_LIST_NAME = "LIST_REQUEST";

    XmlExtAssist assist;

    /**
     * @param pform
     * @param pclient
     */

    public BbChatUI(GForm pform, BbClient pclient) {
        form = pform;
        bbClient = pclient;

        assist = new XmlExtAssist(form);
        assist.registerGUI("com.egls.client.extgui.XSessionList");
        assist.registerGUI("com.egls.client.extgui.XContentView");
    }

    public void close() {
//        if (chatRoot != null) {
//            form.remove(chatRoot);
//        }
//        if (menu != null) {
//            form.remove(menu);
//        }
        form.clear();
        form.setSizeChangeListener(null);
        form.setNotifyListener(null);
        form.setActiveListener(null);
        form.setPickListener(null);
    }

    public GContainer getUI() {
        if (chatRoot != null) {
            return chatRoot;
        }

        GForm.hideKeyboard(form);

        GContentItem.defaultItemW = form.getDeviceWidth() * .80f;


        String xmlStr = GToolkit.readFileFromJarAsString("/res/ui/ChatRoot.xml", "utf-8");
        XContainer xc = (XContainer) XContainer.parseXml(xmlStr, new XmlExtAssist(form));
        eventHandler = new ChatEventHandler();
        xc.build((int) form.getDeviceWidth(), (int) (form.getDeviceHeight() - menuH), eventHandler);

        chatRoot = (GPanel) xc.getGui();
        form.setSizeChangeListener(new GSizeChangeListener() {
            @Override
            public void onSizeChange(int width, int height) {
                xc.reSize(width, height);
                menuContainer.reSize(width, height);
                menu.setLocation(0, form.getDeviceHeight() - menuH);
                GContentItem.defaultItemW = form.getDeviceWidth() * .80f;
            }
        });


        createMainMenu();
        getCanvasPanel();
        getMyPanel();
        setCurrent(getChatSlots());

        menu.setFixed(true);
        menu.setLocation(0, form.getDeviceHeight() - menuH);
        form.add(menu);

        form.setKeyshowListener(new GKeyboardShowListener() {
            @Override
            public void keyboardShow(boolean show, float x, float y, float w, float h) {
                //form.onSizeChange(form.getDeviceWidth(), (int) (form.getDeviceHeight() - h));
                GPanel contentPanel = (GPanel) chatSlots.findByName("PAN_CONTENT");
                if (contentPanel != null) {
                    XContainer xc = (XContainer) contentPanel.getLayout();
                    if (show) {
                        xc.reSize(form.getDeviceWidth(), (int) (form.getDeviceHeight() - h));
                    } else {
                        xc.reSize(form.getDeviceWidth(), (int) (form.getDeviceHeight() - menuH));
                    }
                    GForm.flush();
                }
            }
        });

        form.setNotifyListener((String key, String val) -> {
            onNotify(key, val);
        });

        form.setActiveListener((boolean active) -> {
            getChat().sendClientActive(active);
        });

        form.setPickListener((int uid, String url, byte[] data) -> {
            if (data == null && url != null) {
                File f = new File(url);
                if (f.exists()) {
                    try {
                        FileInputStream fis = new FileInputStream(f);
                        data = new byte[(int) f.length()];
                        fis.read(data);
                        fis.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            switch (uid) {

                case PICK_PHOTO:
                case PICK_CAMERA: {

                    if (data != null) {
                        GSessionItem gsi = curSelectedItem;
                        if (gsi != null) {
                            String fn = MsgItem.STRING_IMAGE;
                            if (url.toLowerCase().endsWith(MsgItem.STRING_VIDEO)) {
                                fn = MsgItem.STRING_VIDEO;
                            }
                            MsgItem mi = new MsgItem(fn, data);
                            mi.fromid = bbClient.getRoleid();
                            mi.toid = gsi.groupInfo.getRoleId();
                            mi.groupid = gsi.groupInfo.getGroupId();
                            getChat().sendSpeak(mi);
                        }
                    }
                    break;
                }
                case PICK_QR: {
                    getChat().sendQrDecode(data, (CmdPkg cmd) -> {
                        String s = cmd.readUTF();
                        getChat().parseQrString(s);
                    });
                    break;
                }
                case PICK_HEAD: {
                    if (data != null) {
                        getChat().setMyHead(data);
                        removeImgFromCache(bbClient.getRoleid());
                        setCurrent(getMyPanel());
                        GForm.flush();
                    }
                    break;
                }
            }
        });


        return chatRoot;
    }


    void setCurrent(GContainer cur) {
        chatRoot.remove(chatSlots);
        chatRoot.remove(gameView);
        chatRoot.remove(myView);
        chatRoot.add(cur);
        chatRoot.getLayout().reSize((int) form.getDeviceWidth(), (int) (form.getDeviceHeight() - menuH));
    }

    void createMainMenu() {
        String xmlStr = GToolkit.readFileFromJarAsString("/res/ui/MainMenu.xml", "utf-8");

        UITemplate uit = new UITemplate(xmlStr);
        for (String key : uit.getVariable()) {
            uit.setVar(key, BbStrings.getString(key));
        }

        XContainer xc = (XContainer) XContainer.parseXml(uit.parse(), new XmlExtAssist(form));
        xc.build((int) form.getDeviceWidth(), form.getDeviceHeight(), eventHandler);
        menu = (GMenu) ((GContainer) xc.getGui()).findByName("MENU_MAIN");
        //System.out.println("menu:" + menu);
        menuContainer = xc;
    }

    public GContainer getChatSlots() {
        if (chatSlots == null) {
            String xmlStr = GToolkit.readFileFromJarAsString("/res/ui/ChatSlot.xml", "utf-8");

            UITemplate uit = new UITemplate(xmlStr);
            for (String key : uit.getVariable()) {
                uit.setVar(key, BbStrings.getString(key));
            }


            XContainer xc = (XContainer) XContainer.parseXml(uit.parse(), assist);
            xc.build((int) chatRoot.getW(), (int) (chatRoot.getH()), eventHandler);
            chatSlots = (GViewSlot) xc.getGui();


            search = (GTextField) chatSlots.findByName("INPUT_SEARCH");
            msgBox = (GTextBox) chatSlots.findByName("INPUT_CHATMSG");
            sessionList = (GSessionList) chatSlots.findByName("LIST_SESSION");
            contentView = (GContentView) chatSlots.findByName("VP_CONTENT");
            mediaBtn = (GButton) chatSlots.findByName("BT_MULTIMEDIA");
            moreBtn = (GButton) chatSlots.findByName("BT_MORE");
            contentView.setChatUI(this);
            //System.out.println("sessionList:" + sessionList);
        }
        return chatSlots;
    }

    public GContainer getCanvasPanel() {
        if (gameView == null) {
            String xmlStr = GToolkit.readFileFromJarAsString("/res/ui/GameCanvas.xml", "utf-8");

            UITemplate uit = new UITemplate(xmlStr);
            for (String key : uit.getVariable()) {
                uit.setVar(key, BbStrings.getString(key));
            }


            XContainer xc = (XContainer) XContainer.parseXml(uit.parse(), new XmlExtAssist(form));
            xc.build((int) chatRoot.getW(), (int) (chatRoot.getH()), eventHandler);
            gameView = (GViewPort) xc.getGui();
            bbClient.initGui(gameView);
        }
        return gameView;
    }


    public GContainer getMyPanel() {
        if (myView == null) {
            String xmlStr = GToolkit.readFileFromJarAsString("/res/ui/MyPanel.xml", "utf-8");

            UITemplate uit = new UITemplate(xmlStr);
            for (String key : uit.getVariable()) {
                uit.setVar(key, BbStrings.getString(key));
            }


            XContainer xc = (XContainer) XContainer.parseXml(uit.parse(), new XmlExtAssist(form));
            xc.build((int) chatRoot.getW(), (int) (chatRoot.getH()), eventHandler);
            myView = (GViewPort) xc.getGui();

            GLabel accountLab = (GLabel) myView.findByName("LAB_MYACCOUNT");
            accountLab.setText(accountLab.getText() + bbClient.passport);
            GLabel bbidLab = (GLabel) myView.findByName("LAB_MYBBID");
            bbidLab.setText(bbidLab.getText() + bbClient.roleid);
            GImageItem imgItem = (GImageItem) myView.findByName("IMG_MYHEAD");
            imgItem.setImg(getHead(bbClient.getRoleid()));

            //System.out.println("myView:" + myView);
        }
        return myView;
    }


    void chatPanelShowLeft() {
        GSessionItem gsi = curSelectedItem;
        if (gsi != null) {
            gsi.clearMsgNewCount();
            sessionList.setSelectedIndex(-1);
            curSelectedItem = null;
        }
        chatSlots.moveTo(0, 200);
        System.out.println("show left");
    }

    void chatPanelShowRight() {
        chatSlots.moveTo(1, 200);
        System.out.println("show right");
    }

    void showAddNewFrame() {

        GFrame frame = GToolkit.getInputFrame(form, BbStrings.getString("Add Friend"),
                BbStrings.getString("Input the bbid:"),
                null,
                "Input BBID",
                BbStrings.getString("Add"),
                (GObject gobj) -> {
                    String fstr = ((GTextField) gobj.getFrame().findByName("input")).getText();
                    long fid = -1;
                    try {
                        fid = Long.parseLong(fstr);
                    } catch (Exception e) {
                    }
                    if (fid != -1) {
                        bbClient.getChat().sendFriendRequest(fid);
                        gobj.getFrame().close();
                    } else {
                        ((GLabel) gobj.getFrame().findByName("state")).setText(BbStrings.getString("bbid not illegal"));
                    }
                },
                BbStrings.getString("Camera Scan"),
                (GObject gobj) -> {
                    Glfm.glfmPickPhotoCamera(form.getWinContext(), PICK_QR, 0);
                    gobj.getFrame().close();
                }
        );

        form.add(frame);
        frame.align(GGraphics.HCENTER | GGraphics.VCENTER);
        form.setFocus(frame);
    }

    @Override
    public void onGroupAdd(ChatGroupInfo sd, long lastMsgAt) {
        GSessionItem gsi = sessionList.findSessionItem(0, sd.groupid);
        if (gsi == null) {
            gsi = new GSessionItem(form, sd, this);
            gsi.groupInfo = sd;
            sessionList.add(0, gsi);
            addSessionItemAction(gsi);
        }
        gsi.setLabel(sd.toString());
        if (lastMsgAt > 0) {
            gsi.groupInfo.lastMsgAt = lastMsgAt;
        }
        sessionList.reSort();
        GForm.flush();
    }

    @Override
    public void onGroupRemove(ChatGroupInfo sd) {
        GObject go = sessionList.findSessionItem(0, sd.groupid);
        if (go == null) {
            sessionList.remove(go);
            GForm.flush();
        }
    }

    @Override
    public void onGroupUpdate(ChatGroupInfo sd) {
        GSessionItem gsi = sessionList.findSessionItem(0, sd.groupid);
        if (gsi != null) {
            gsi.setLabel(sd.toString());
            GForm.flush();
        }
    }

    @Override
    public void onGroupDetail(ChatGroupInfo sd) {

    }

    @Override
    public void onFriendAdd(MemberInfo mi, long lastMsgAt) {
        GSessionItem gsi = (GSessionItem) sessionList.findSessionItem(mi.roleid, 0);
        if (gsi == null) {
            gsi = new GSessionItem(form, mi, this);
            gsi.groupInfo = mi;
            sessionList.add(0, gsi);
            addSessionItemAction(gsi);
        }
        if (lastMsgAt > 0) {
            gsi.groupInfo.lastMsgAt = lastMsgAt;
        }
        gsi.setLabel(mi.toString());
        sessionList.reSort();
        GForm.flush();
    }

    @Override
    public void onFriendRemove(MemberInfo mi) {
        GObject go = sessionList.findSessionItem(mi.roleid, 0);
        if (go != null) {
            GSessionItem gsi = (GSessionItem) (go);
            sessionList.remove(gsi);
            GForm.flush();
        }
    }

    @Override
    public void onFriendUpdated(MemberInfo mi) {
        if (mi.roleid == bbClient.getRoleid()) {
            GTextField nameField = (GTextField) myView.findByName("INPUT_MYNICK");
            nameField.setText(mi.name);
        } else {
            GSessionItem gsi = (GSessionItem) sessionList.findSessionItem(mi.roleid, 0);
            if (gsi != null) {
                gsi.setLabel(mi.toString());
                GForm.flush();
            }
        }
    }

    @Override
    public void onReceiveMsg(MsgItem mi) {
        GSessionItem gsi = curSelectedItem;
        if (gsi != null && gsi.groupInfo.match(mi.sessionRoleId, mi.groupid)) {
            GContentItem ci = new GContentItem(form, mi, this);
            float left = pad;
            if (mi.fromid == bbClient.getRoleid()) {
                left = contentView.getW() - ci.getW();
                ci.left = false;
            }
            ci.setLocation(left, contentView.getAfterLastItem());
            contentView.addItem(ci);

            gsi.groupInfo.lastMsgAt = mi.time;
            sessionList.reSort();
            sessionList.setSelectedIndex(sessionList.getItemIndex(gsi));
        } else {
            GSessionItem got = sessionList.findSessionItem(mi.sessionRoleId, mi.groupid);
            if (got != null) {
                got.addNewMsgCount(1);
                //
                AudioLoader.play(AudioLoader.BIBI);
            }
        }
        GForm.flush();
    }

    public void OnReceiveHead(long roleid) {
        removeImgFromCache(roleid);
    }

    @Override
    public void onReceiveAttachment(String resourceid, byte[] attachment) {
    }

    @Override
    public void onFriendRequest(long friendid, String nick) {
        GList list = (GList) myView.findByName(REQUEST_LIST_NAME);
        if (list != null) {
            GListItem gli = list.addItem(null, BbStrings.getString("Request be friend") + " (" + friendid + ")" + nick);
            gli.setAttachment(friendid);
            gli.setActionListener((GObject gobj) -> {
                showConfirmAdd(gli);
            });

            //
            GMenuItem mi = (GMenuItem) menu.findByName("MI_MY");
            if (mi != null) {
                mi.incMsgNew(1);
            }
        }
        GForm.flush();
    }

    void showConfirmAdd(GListItem gli) {
        GFrame frame = GToolkit.getConfirmFrame(form, BbStrings.getString("Request list"),
                BbStrings.getString("bbid:") + gli.getAttachment(),
                BbStrings.getString("Decline"),
                //
                (GObject gobj) -> {
                    bbClient.getChat().sendFriendAdd((Long) gli.getAttachment(), false);
                    form.remove(gobj.getFrame());
                },
                BbStrings.getString("Add"),
                //
                (GObject gobj) -> {
                    bbClient.getChat().sendFriendAdd((Long) gli.getAttachment(), true);
                    form.remove(gobj.getFrame());
                });

        form.add(frame);
        frame.align(GGraphics.VCENTER | GGraphics.HCENTER);
        form.setFocus(frame);

    }

    void showMoreMenu() {
        if (moreMenu == null) {
            moreMenu = new GList(form, form.getDeviceWidth() - 200 - pad, moreBtn.getY() + moreBtn.getH() + pad, 200, 160);
            moreMenu.setShowMode(GList.MODE_MULTI_SHOW);
            moreMenu.setBgColor(GToolkit.getStyle().getFrameBackground());
            moreMenu.setFocusListener(new GFocusChangeListener() {
                @Override
                public void focusGot(GObject oldgo) {
                }

                @Override
                public void focusLost(GObject newgo) {
                    if (form != null) {
                        form.getForm().remove(moreMenu);
                    }
                }
            });
            GListItem itemInfo = moreMenu.addItem(null, BbStrings.getString("Infomation"));
            itemInfo.setActionListener((GObject gobj) -> {
                showInfoFrame();
            });
            GListItem itemClear = moreMenu.addItem(null, BbStrings.getString("Clear Message"));
            itemClear.setActionListener((GObject gobj) -> {
                form.remove(moreMenu);

                GSessionItem gsi = curSelectedItem;
                if (gsi == null) {
                    return;
                }
                GFrame frame = GToolkit.getConfirmFrame(form, BbStrings.getString("Clear Message"),
                        BbStrings.getString("Clear Message") + " : " + gsi.groupInfo.getId() + "," + gsi.getLabel() + " " + BbStrings.getString("Are you sure?"),
                        BbStrings.getString("Delete"),
                        //
                        (GObject gobj1) -> {
                            form.remove(gobj1.getFrame());
                            GSessionItem sitem = curSelectedItem;
                            if (sitem == null) {
                                return;
                            }
                            getMsgDatabase().removeSession(sitem.groupInfo.getRoleId(), sitem.groupInfo.getGroupId());
                            contentView.clear();
                        },
                        BbStrings.getString("Cancel"),
                        //
                        (GObject gobj1) -> {
                            form.remove(gobj1.getFrame());
                        });

                form.add(frame);
                frame.align(GGraphics.VCENTER | GGraphics.HCENTER);
                form.setFocus(frame);
            });
            GListItem itemDelete = moreMenu.addItem(null, BbStrings.getString("Remove"));
            itemDelete.setActionListener((GObject gobj) -> {
                form.remove(moreMenu);

                GSessionItem gsi = curSelectedItem;
                if (gsi == null) {
                    return;
                }
                if (gsi.groupInfo.isGroup()) {
                    showRemoveMember(gsi);
                } else {
                    showDeleteFriend(gsi);
                }
            });

            GListItem itemAddMember = moreMenu.addItem(null, BbStrings.getString("Add Friend"));
            itemAddMember.setActionListener((GObject gobj) -> {
                showAddMember();
            });
        }
        form.add(moreMenu);
        form.setFocus(moreMenu);
        moreMenu.setFront(true);
    }

    void showDeleteFriend(GSessionItem gsi) {
        GFrame frame = GToolkit.getConfirmFrame(form, BbStrings.getString("Remove Friend"),
                BbStrings.getString("Delete") + " : " + gsi.groupInfo.getId() + "," + gsi.getLabel() + " " + BbStrings.getString("Are you sure?"),
                BbStrings.getString("Delete"),
                //
                (GObject gobj1) -> {
                    form.remove(gobj1.getFrame());
                    if (gsi.groupInfo.isGroup()) {
                        bbClient.getChat().sendGroupRemove(gsi.groupInfo.getGroupId());
                    } else {
                        bbClient.getChat().sendFriendRemove(gsi.groupInfo.getRoleId());
                        getMsgDatabase().removeSession(gsi.groupInfo.getRoleId(), 0);
                        contentView.clear();
                    }
                },
                BbStrings.getString("Cancel"),
                //
                (GObject gobj1) -> {
                    form.remove(gobj1.getFrame());
                });

        form.add(frame);
        frame.align(GGraphics.VCENTER | GGraphics.HCENTER);
        form.setFocus(frame);
    }

    void showAddMember() {
        GSessionItem sitem = curSelectedItem;
        if (sitem == null) {
            return;
        }

        ChatGroupInfo info = null;
        if (sitem.groupInfo.isGroup()) {
            info = getChat().getGroup(sitem.groupInfo.getId());
        }
        List<String> strlist = new ArrayList();
        List<GImage> imglist = new ArrayList();
        for (GListItem item : sessionList.getItems()) {
            GSessionItem gsi = (GSessionItem) item;
            if (gsi.groupInfo.isGroup()) {//except group
                continue;
            }
            if (gsi.groupInfo.getId() == sitem.groupInfo.getId()) {//except current friend
                continue;
            }
            if (info != null && info.isMember(gsi.groupInfo.getRoleId())) {
                continue;
            }
            String s = gsi.groupInfo.getRoleId() + "," + gsi.groupInfo.getGroupId();
            strlist.add(s);
            imglist.add(null);
        }

        GFrame addMember = GToolkit.getListFrame(form, BbStrings.getString("Add Member"),
                strlist.toArray(new String[strlist.size()]),
                imglist.toArray(new GImage[imglist.size()]),
                //
                (GObject gobj) -> {
                    GSessionItem sessionItem = curSelectedItem;
                    if (sessionItem == null) {
                        return;
                    }

                    GList list = (GList) gobj.getFrame().findByName("list");
                    if (list == null) {
                        System.out.println("list not found");
                        return;
                    }
                    List<Long> m = new ArrayList();
                    int[] selected = list.getSelectedIndices();
                    for (int i = 0; i < selected.length; i++) {
                        //GListItem gli = list.getItem(selected[i]);
                        String s = (String) strlist.get(selected[i]);
                        String[] strs = s.split(",");
                        try {
                            long friendid = Long.parseLong(strs[0]);
                            long sessionid = Long.parseLong(strs[1]);
                            if (sessionid == 0) {
                                m.add(friendid);
                            } else {
                                ChatGroupInfo csi = bbClient.getChat().getGroup(sessionid);
                                if (csi != null) {
                                    m.addAll(csi.members.keySet());
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                    if (sessionItem.groupInfo.isGroup()) {//group exists
                        bbClient.getChat().sendGroupMemberAdd(sessionItem.groupInfo.getGroupId(), m);
                    } else {// new group
                        m.add(sessionItem.groupInfo.getId());
                        bbClient.getChat().sendGroupAdd(m);
                    }
                    form.remove(gobj.getFrame());
                    chatPanelShowLeft();
                },
                null);

        form.add(addMember);
        addMember.align(GGraphics.VCENTER | GGraphics.HCENTER);
        form.setFocus(addMember);
    }

    void showRemoveMember(GSessionItem gsi) {

        ChatGroupInfo info = getChat().getGroup(gsi.groupInfo.getGroupId());
        List<String> strlist = new ArrayList();
        List<GImage> imglist = new ArrayList();
        for (MemberInfo item : info.members.values()) {
            if (item.roleid == bbClient.getRoleid()) {//except group
                continue;
            }

            String s = item.roleid + "," + item.name;
            strlist.add(s);
            imglist.add(null);
        }

        GFrame removeMember = GToolkit.getListFrame(form, BbStrings.getString("Remove Member"),
                strlist.toArray(new String[strlist.size()]),
                imglist.toArray(new GImage[imglist.size()]),
                //
                (GObject gobj) -> {
                    GList list = (GList) gobj.getFrame().findByName("list");
                    if (list == null) {
                        System.out.println("list not found");
                        return;
                    }
                    List<Long> m = new ArrayList();
                    int[] selected = list.getSelectedIndices();
                    for (int i = 0; i < selected.length; i++) {
                        //GListItem gli = list.getItem(selected[i]);
                        String s = (String) strlist.get(selected[i]);
                        String[] strs = s.split(",");
                        try {
                            long friendid = Long.parseLong(strs[0]);
                            m.add(friendid);
                        } catch (Exception e) {
                        }
                    }
                    bbClient.getChat().sendGroupMemberRemove(gsi.groupInfo.getRoleId(), gsi.groupInfo.getGroupId(), m);
                    form.remove(gobj.getFrame());
                },
                null);

        form.add(removeMember);
        removeMember.align(GGraphics.VCENTER | GGraphics.HCENTER);
        form.setFocus(removeMember);
    }

    public void showForwardFrame(GContentItem ci) {
        GSessionItem sitem = curSelectedItem;
        if (sitem == null) {
            return;
        }

        List<String> strlist = new ArrayList();
        List<GImage> imglist = new ArrayList();
        for (GListItem item : sessionList.getItems()) {
            GSessionItem gsi = (GSessionItem) item;
            String name = "";
            if (gsi.groupInfo.isGroup()) {
                name = getChat().getGroup(gsi.groupInfo.getGroupId()).getName();
            } else {
                name = getChat().getFriend(gsi.groupInfo.getRoleId()).getName();
            }
            String s = gsi.groupInfo.getRoleId() + "," + gsi.groupInfo.getGroupId() + "," + name;
            strlist.add(s);
            GImage img = null;
            if (gsi.groupInfo.isGroup()) {
                img = getGroupHead();
            } else {
                img = getHead(gsi.groupInfo.getRoleId());
            }
            imglist.add(img);
        }

        GFrame selMemberFrame = GToolkit.getListFrame(form, BbStrings.getString("Forward"),
                strlist.toArray(new String[strlist.size()]),
                imglist.toArray(new GImage[imglist.size()]),
                //
                (GObject gobj) -> {
                    GSessionItem sessionItem = curSelectedItem;
                    if (sessionItem == null) {
                        return;
                    }

                    GList list = (GList) gobj.getFrame().findByName("list");
                    if (list == null) {
                        System.out.println("list not found");
                        return;
                    }
                    List<Long> m = new ArrayList();
                    int[] selected = list.getSelectedIndices();
                    for (int i = 0; i < selected.length; i++) {
                        //GListItem gli = list.getItem(selected[i]);
                        String s = (String) strlist.get(selected[i]);
                        String[] strs = s.split(",");
                        try {
                            long friendid = Long.parseLong(strs[0]);
                            long sessionid = Long.parseLong(strs[1]);
                            MsgItem mi = ci.getMsgItem();
                            if (mi.getMediaType() != MsgItem.TYPE_TEXT) {
                                getChat().sendFileMsg(friendid, sessionid, mi.msg, mi.thumb);
                            } else {
                                getChat().sendTextMsg(friendid, sessionid, mi.msg);
                                break;
                            }

                        } catch (Exception e) {
                        }
                    }
                    if (gobj.getFrame() != null) {
                        gobj.getFrame().close();
                    }
                },
                null);

        form.add(selMemberFrame);
        selMemberFrame.align(GGraphics.VCENTER | GGraphics.HCENTER);
        form.setFocus(selMemberFrame);
    }

    /**
     * return a list frame
     *
     * @return
     */
    public void showInfoFrame() {
        GSessionItem sessionItem = curSelectedItem;
        if (sessionItem == null) {
            return;
        }
        float pad = 2, btnW = 80, btnH = 35;
        float y = pad;

        GFrame frame = new GFrame(form, BbStrings.getString("Infomation"), 0, 0, form.getDeviceWidth() * .85f, form.getDeviceHeight() * .7f);

        frame.setFront(true);
        frame.setFocusListener(new GFocusChangeListener() {
            @Override
            public void focusGot(GObject oldgo) {
            }

            @Override
            public void focusLost(GObject newgo) {
                frame.close();
            }
        });
        GViewPort view = frame.getView();

        GImage img = null;
        if (sessionItem.groupInfo.isGroup()) {
            img = getGroupHead();
        } else {
            img = getHead(sessionItem.groupInfo.getId());
        }
        GImageItem imgItem = new GImageItem(form, img);
        imgItem.setSize(80, 80);
        imgItem.setLocation(pad, y);
        view.add(imgItem);
        y += 85;

        String name;
        if (sessionItem.groupInfo.isGroup()) {
            name = getChat().getGroup(sessionItem.groupInfo.getId()).name;
        } else {
            name = getChat().getFriend(sessionItem.groupInfo.getId()).name;
        }
        GTextField nameField = new GTextField(form, name, "Change Name", pad, y, view.getW() - pad * 3 - btnW, btnH);
        //nameField.setName("NameField");
        view.add(nameField);

        GButton btn = new GButton(form, BbStrings.getString("Change"), (view.getW() - btnW - pad), y, btnW, btnH);
        view.add(btn);
        btn.setActionListener((GObject gobj) -> {
            String n = nameField.getText();
            if (sessionItem.groupInfo.isGroup()) {
                getChat().sendGroupUpdate(sessionItem.groupInfo.getGroupId(), n);
            } else {
                getChat().sendFriendUpdate(sessionItem.groupInfo.getRoleId(), n);
            }
        });
        y += btnH + pad;

        //
        form.add(frame);
        frame.align(GGraphics.VCENTER | GGraphics.HCENTER);
        form.setFocus(frame);
    }

    public void showAudioCaptureFrame() {
        GAudioRecoder recoder = new GAudioRecoder(form, false);
        recoder.setRightButton(BbStrings.getString("Send"), (GObject gobj) -> {
            GSessionItem gsi = curSelectedItem;
            if (gsi != null) {
                if (recoder.getCaptureZipData() != null) {
                    MsgItem mi = new MsgItem(MsgItem.STRING_VOICE, recoder.getCaptureZipData());
                    mi.fromid = bbClient.getRoleid();
                    mi.toid = gsi.groupInfo.getRoleId();
                    mi.groupid = gsi.groupInfo.getGroupId();
                    getChat().sendSpeak(mi);
                }
            }
            recoder.close();
        });
        form.add(recoder);
        recoder.align(GGraphics.VCENTER | GGraphics.HCENTER);
        form.setFocus(recoder);

        recoder.startCapture(60);
    }

    public MsgDatabase getMsgDatabase() {
        return bbClient.getChat().getMsgDatabase();
    }

    public ImageDatabase getImageDatabase() {
        return bbClient.getChat().getImageDatabase();
    }

    public Chat getChat() {
        return bbClient.getChat();
    }

    void addSessionItemAction(GSessionItem gsi) {
        gsi.setActionListener((GObject gobj) -> {
            curSelectedItem = (GSessionItem) gobj;
            //clear panel
            contentView.clear();
            curPreMsgPageId = curNextMsgPageId = -1;

            GSessionItem item = (GSessionItem) gobj;
            item.clearMsgNewCount();//clear red point
            //load chat content
            for (int i = 0; i < 2; i++) {
                MsgPage mp = getMsgDatabase().getPrePage(item.groupInfo.getRoleId(), item.groupInfo.getGroupId(), curPreMsgPageId);
                if (mp != null) {
                    for (MsgItem mi : mp.getItems()) {
                        onReceiveMsg(mi);
                    }
                    curPreMsgPageId = mp.getPageId();
                    if (i == 0) {
                        curNextMsgPageId = mp.getPageId();
                    }
                }
            }
            GLabel nameLab = (GLabel) chatSlots.findByName("LAB_UNAME");
            if (nameLab != null) {
                nameLab.setText(gsi.getLabel());
            }
            contentView.setScrollY(1.f);
            //
            chatPanelShowRight();
        });

        //getMemberList
        if (gsi.groupInfo.isGroup()) {
            ChatGroupInfo csi = getChat().getGroup(gsi.groupInfo.getId());
            if (!csi.detailReceived) {
                getChat().sendGroupGet(gsi.groupInfo.getId());
            }
        }
    }

    public void loadPrePage() {
        GSessionItem item = curSelectedItem;
        if (item == null) {
            return;
        }
        MsgPage mp = getMsgDatabase().getPrePage(item.groupInfo.getRoleId(), item.groupInfo.getGroupId(), curPreMsgPageId);
        if (mp != null) {
            for (MsgItem mi : mp.getItems()) {
                onReceiveMsg(mi);
            }
            curPreMsgPageId = mp.getPageId();
        }
    }

    public void loadNextPage() {
        GSessionItem item = curSelectedItem;
        if (item == null) {
            return;
        }
        MsgPage mp = getMsgDatabase().getNextPage(item.groupInfo.getRoleId(), item.groupInfo.getGroupId(), curNextMsgPageId);
        if (mp != null) {
            for (MsgItem mi : mp.getItems()) {
                onReceiveMsg(mi);
            }
            curNextMsgPageId = mp.getPageId();
        }
    }

    public GImage getHead(long friendid) {
        GImage img = headCache.get(friendid);
        if (img == null) {
            byte[] data = getImageDatabase().getHeader(friendid);
            if (data == null) {
                img = headCache.get(0L);
                if (img == null) {
                    data = getChat().getImageDatabase().getDefaultHead();
                    img = GImage.createImage(data);
                    headCache.put(0L, img);
                }
            } else {
                img = GImage.createImage(data);
                headCache.put(friendid, img);
            }
        }
        return img;
    }

    public void removeImgFromCache(long friendid) {
        headCache.remove(friendid);
    }

    public GImage getGroupHead() {
        GImage img = headCache.get(-1L);
        if (img == null) {
            img = GImage.createImage(getImageDatabase().getGroupHead());
            headCache.put(-1L, img);
        }
        return img;
    }

    public GImage getMyBbidQr() {
        GImage img = headCache.get(-2L);
        if (img == null) {
            byte[] b = getImageDatabase().getMyBbidQr();
            if (b != null) {
                img = GImage.createImage(b);
                headCache.put(-2L, img);
            }
        }
        return img;
    }

    void onNotify(String key, String val) {
        System.out.println("notify key:" + key + "  val:" + val);
    }


    class ChatEventHandler extends XEventHandler {


        @Override
        public void action(GObject gobj) {
            String name = gobj.getName();

            if ("BT_ADDFRIEND".equals(name)) {
                showAddNewFrame();
            } else if ("BT_MULTIMEDIA".equals(name)) {
                actionMultiMedia();
            } else if ("BT_SEND".equals(name)) {
                GSessionItem gsi = curSelectedItem;
                if (gsi != null) {
                    getChat().sendTextMsg(gsi.groupInfo.getRoleId(), gsi.groupInfo.getGroupId(), msgBox.getText());
                    msgBox.getParent().setFocus(msgBox);
                    msgBox.setText("");
                }
            } else if ("BT_BACKTOSESSION".equals(name)) {
                chatPanelShowLeft();
            } else if ("IMG_MYHEAD".equals(name)) {
                Glfm.glfmPickPhotoAlbum(form.getWinContext(), PICK_HEAD, Glfm.GLFMPickupTypeImage);
            } else if ("BT_CHANGENICK".equals(name)) {
                String n = nameField.getText();
                getChat().sendFriendUpdate(bbClient.getRoleid(), n);
                GForm.addMessage(BbStrings.getString("Submited change"));
            } else if ("BT_QRCODE".equals(name)) {
                if (getMyBbidQr() == null) {
                    GFrame gf = GToolkit.getConfirmFrame(form, BbStrings.getString("Notify"), BbStrings.getString("Qr Code is generating"), null, null, null, null);
                    form.add(gf);
                    gf.align(GGraphics.HCENTER | GGraphics.VCENTER);
                } else {
                    GViewPort qrView = GToolkit.getImageView(form, getMyBbidQr(), null);
                    form.add(qrView);
                    form.setFocus(qrView);
                }
            } else if ("BT_CLEARALL".equals(name)) {
                getChat().clearAll();
            } else if ("BT_LOGOUT".equals(name)) {
                bbClient.logout();
            } else if ("BT_EXIT".equals(name)) {
                bbClient.close();
                BbMain.getInstance().closeApp();
            } else if ("BT_MORE".equals(name)) {
                showMoreMenu();
            } else if ("MI_SESSION".equals(name)) {
                setCurrent(getChatSlots());
            } else if ("MI_GAME".equals(name)) {
                GForm.addMessage(BbStrings.getString("It's in building, energy wasted"));
                setCurrent(getCanvasPanel());
            } else if ("MI_MY".equals(name)) {
                setCurrent(getMyPanel());
            }
        }


        @Override
        public void onStateChange(GObject gobj) {
            String name = gobj.getName();
            if ("INPUT_SEARCH".equals(name)) {
                String str = search.getText();
                if (sessionList != null) {
                    sessionList.filterLabelWithKey(str);
                }
            }
        }


        private void actionMultiMedia() {
            GList mediaMenu = GToolkit.getListMenu(form, new String[]{
                            BbStrings.getString("Send Photo"),
                            BbStrings.getString("Camera"),
                            BbStrings.getString("Voice"),//
                            BbStrings.getString("VoiceNow"),//
                    },
                    null,
                    new GActionListener[]{
                            //photo
                            new GActionListener() {
                                @Override
                                public void action(GObject gobj) {
                                    Glfm.glfmPickPhotoAlbum(form.getWinContext(), PICK_PHOTO, Glfm.GLFMPickupTypeImage | Glfm.GLFMPickupTypeVideo);
                                    form.setFocus(null);
                                }
                            },//camera
                            new GActionListener() {
                                @Override
                                public void action(GObject gobj) {
                                    Glfm.glfmPickPhotoCamera(form.getWinContext(), PICK_CAMERA, Glfm.GLFMPickupTypeImage | Glfm.GLFMPickupTypeVideo);
                                    form.setFocus(null);
                                }
                            },//voice
                            new GActionListener() {
                                @Override
                                public void action(GObject gobj) {
                                    form.setFocus(null);
                                    showAudioCaptureFrame();
                                }
                            },//voiceNow
                            new GActionListener() {
                                @Override
                                public void action(GObject gobj) {
                                    form.setFocus(null);
                                    GForm.addMessage(BbStrings.getString("Not available"));
                                }
                            },});
            mediaMenu.setSize(mediaMenu.getW(), 160);
            mediaMenu.setInnerSize(mediaMenu.getW(), 160);
            mediaMenu.setLocation(mediaBtn.getX(), mediaBtn.getY() - mediaMenu.getH() - pad);
            form.add(mediaMenu);
            form.setFocus(mediaMenu);
        }
    }
}
