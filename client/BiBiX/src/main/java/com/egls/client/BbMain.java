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
import org.mini.gui.event.GStateChangeListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.mini.gui.GObject.*;

/**
 * @author Gust
 */
public class BbMain implements ChatStateListener {

    static final String UI_NAME_MENUITEM_MY = "MenuItem_My";
    static final String UI_NAME_TEXTFIELD_CHANGE_NAME = "TextField_ChangeName";

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

    GViewSlot chatSlots;

    GPanel chatPanel;
    GPanel sessionPanel;
    GViewPort gameView;
    GViewPort myView;
    GImage logoImg;
    //
    GSessionList sessionList;
    GContentView contentView;
    int curPreMsgPageId = -1, curNextMsgPageId = -1;
    GSessionItem curSelectedItem;

    //GFrame addFrame;
    GTextBox msgBox;
    GButton sendBtn;
    GButton mediaBtn;
    //
    GList moreMenu;

    static final int PICK_PHOTO = 101, PICK_CAMERA = 102, PICK_QR = 103, PICK_HEAD = 104;

    static float menuH = 60, pad = 2, inputH = 60, addW = 60, addH = 28, sendW = 60;
    ;
    static float[][] align = {
            {0, -1, -1, menuH}, //menu
            //
            {-1, pad, addW, addH}, //addBtn
            {pad, 37, -1, 30}, //search
            {0, 72, -1, -1}, //sessionList
            //
            {-1, pad, addW, addH}, //moreBtn
            {-1, 37, -1, -1}, //contentView
            {-1, -1, -1, inputH}, //msgBox
            {-1, -1, sendW, inputH}, //sendBtn

            //
            {0, 0, -1, -1}, //chatPan
            {0, 0, -1, -1}, //myPan
            {0, 0, -1, -1}, //mapPan
            //
            {-1, pad, addW, addH}, //back2listBtn
            {-1, 37, 200, 160}, //moreMenu
            {-1, 40, -1, -1}, //addMember
            {-1, 37, -1, -1}, //addMemberList
            {-1, -1, 80, 30}, //addMemberOk

            //
            {-1, -1, sendW, inputH}, //mediaBtn
            {-1, pad, -1, addH}, //nameLab

            //
            {0, 0, -1, -1}, //slots
    };

    static int ATT_MENU = 0;
    static int ATT_ADDBTN = 1;
    static int ATT_SEARCH = 2;
    static int ATT_SESSIONLIST = 3;
    static int ATT_MOREBTN = 4;
    static int ATT_CONTVIEW = 5;
    static int ATT_MSGBOX = 6;
    static int ATT_SENDBTN = 7;
    static int ATT_CHATPAN = 8;
    static int ATT_MYVIEW = 9;
    static int ATT_GAMEVIEW = 10;
    static int ATT_BACK2LISTBTN = 11;
    static int ATT_MOREMENU = 12;
    static int ATT_ADDMEMBER = 13;
    static int ATT_ADDMEMBERLIST = 14;
    static int ATT_ADDMEMBEROK = 15;
    static int ATT_MEDIABTN = 16;
    static int ATT_NAMELAB = 17;
    static int ATT_SLOTS = 18;

    void reAlign(float devW, float devH) {
        align[ATT_MENU][TOP] = devH - menuH;
        align[ATT_MENU][WIDTH] = devW;

        align[ATT_ADDBTN][LEFT] = devW - addW - pad;

        align[ATT_SEARCH][WIDTH] = devW - pad * 2;

        align[ATT_SESSIONLIST][WIDTH] = devW;
        align[ATT_SESSIONLIST][HEIGHT] = devH - menuH - pad - align[ATT_SESSIONLIST][TOP];

        align[ATT_MOREBTN][LEFT] = devW - addW - pad;

        align[ATT_NAMELAB][LEFT] = addW + pad;
        align[ATT_NAMELAB][WIDTH] = devW - addW * 2 - pad * 4;

        align[ATT_BACK2LISTBTN][LEFT] = pad;

        align[ATT_CONTVIEW][LEFT] = 0;
        align[ATT_CONTVIEW][WIDTH] = devW;
        align[ATT_CONTVIEW][HEIGHT] = devH - menuH - align[ATT_CONTVIEW][TOP] - inputH - pad * 2;

        align[ATT_MSGBOX][LEFT] = pad + sendW + pad;
        align[ATT_MSGBOX][TOP] = devH - menuH - pad - inputH;
        align[ATT_MSGBOX][WIDTH] = devW - pad * 4 - sendW * 2;

        align[ATT_SENDBTN][LEFT] = devW - pad - sendW;
        align[ATT_SENDBTN][TOP] = align[ATT_MSGBOX][TOP];

        align[ATT_CHATPAN][WIDTH] = devW;
        align[ATT_CHATPAN][HEIGHT] = devH - menuH;

        align[ATT_MYVIEW][WIDTH] = devW;
        align[ATT_MYVIEW][HEIGHT] = devH - menuH - pad;

        align[ATT_GAMEVIEW][WIDTH] = devW;
        align[ATT_GAMEVIEW][HEIGHT] = devH - menuH - pad;

        align[ATT_MOREMENU][LEFT] = devW - align[ATT_MOREMENU][WIDTH] - pad;

        align[ATT_ADDMEMBER][WIDTH] = devW - 80;
        align[ATT_ADDMEMBER][HEIGHT] = align[ATT_CHATPAN][HEIGHT] - 80;
        align[ATT_ADDMEMBER][LEFT] = 40;

        align[ATT_ADDMEMBERLIST][WIDTH] = align[ATT_ADDMEMBER][WIDTH] - 4;
        align[ATT_ADDMEMBERLIST][HEIGHT] = align[ATT_ADDMEMBER][HEIGHT] - 71 - 30 - pad * 2;
        align[ATT_ADDMEMBERLIST][LEFT] = pad;

        align[ATT_ADDMEMBEROK][LEFT] = (align[ATT_ADDMEMBER][WIDTH] - align[ATT_ADDMEMBEROK][WIDTH]) / 2;
        align[ATT_ADDMEMBEROK][TOP] = align[ATT_ADDMEMBERLIST][TOP] + align[ATT_ADDMEMBERLIST][HEIGHT] + pad;

        align[ATT_MEDIABTN][LEFT] = pad;
        align[ATT_MEDIABTN][TOP] = align[ATT_SENDBTN][TOP];

        align[ATT_SLOTS][WIDTH] = devW;
        align[ATT_SLOTS][HEIGHT] = devH - menuH;
    }

    void reBoundle() {
        if (menu != null) {
            menu.setLocation(align[ATT_MENU][LEFT], align[ATT_MENU][TOP]);
        }

        if (chatPanel != null) {
            chatPanel.setSize(align[ATT_CHATPAN][WIDTH], align[ATT_CHATPAN][HEIGHT]);
        }

        if (myView != null) {
            myView.setSize(align[ATT_MYVIEW][WIDTH], align[ATT_MYVIEW][HEIGHT]);
        }

        if (contentView != null) {
            contentView.setSize(align[ATT_CONTVIEW][WIDTH], align[ATT_CONTVIEW][HEIGHT]);
        }

        if (sessionList != null) {
            sessionList.setSize(align[ATT_SESSIONLIST][WIDTH], align[ATT_SESSIONLIST][HEIGHT]);
        }

        if (msgBox != null) {
            msgBox.setLocation(align[ATT_MSGBOX][LEFT], align[ATT_MSGBOX][TOP]);
        }

        if (sendBtn != null) {
            sendBtn.setLocation(align[ATT_SENDBTN][LEFT], align[ATT_SENDBTN][TOP]);
        }
        if (mediaBtn != null) {
            mediaBtn.setLocation(align[ATT_MEDIABTN][LEFT], align[ATT_MEDIABTN][TOP]);
        }
        if (gameView != null) {
            gameView.setSize(align[ATT_GAMEVIEW][WIDTH], align[ATT_GAMEVIEW][HEIGHT]);
        }
        if (chatSlots != null) {
            chatSlots.setSize(align[ATT_SLOTS][WIDTH], align[ATT_SLOTS][HEIGHT]);
        }

    }

    static final String REQUEST_LIST_NAME = "requestList";

    public BbMain() {
        bbClient = BbApplication.getInstance().client;
    }

    public GForm getMainForm() {
        if (form != null) {
            return form;
        }
        form = new GForm() {

            @Override
            public boolean update(long vg) {
                if (getChat() == null) {
                    return true;
                }
                getChat().sendDeviceToken();
                return super.update(vg);
            }
        };

        GForm.hideKeyboard();

        GContentItem.defaultItemW = form.getDeviceWidth() * .80f;

        reAlign(form.getDeviceWidth(), form.getDeviceHeight());

        logoImg = GImage.createImageFromJar("/res/img/logo128.png");

        createMainMenu();
        getCanvasPanel();
        getMyPanel();
        setCurrent(getChatSlots());

        menu.setFixed(true);
        form.add(menu);

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
        return form;
    }

    void setCurrent(GContainer cur) {
        form.remove(chatSlots);
        form.remove(gameView);
        form.remove(myView);
        form.add(cur);
        form.reSize();
    }

    void createMainMenu() {
        GImage img = GImage.createImageFromJar("/res/img/session.png");
        menu = new GMenu(align[ATT_MENU][LEFT], align[ATT_MENU][TOP], align[ATT_MENU][WIDTH], align[ATT_MENU][HEIGHT]);
        GMenuItem item;
        item = menu.addItem(null, img);//BbStrings.getString("Session")
        item.setActionListener((GObject gobj) -> {
            setCurrent(getChatSlots());
            form.setScrollX(0.f);
        });
        img = GImage.createImageFromJar("/res/img/map.png");
        item = menu.addItem(null, img);//BbStrings.getString("Map")
        item.setActionListener((GObject gobj) -> {
            GForm.addMessage(BbStrings.getString("It's in building, energy wasted"));
            setCurrent(getCanvasPanel());
        });
        img = GImage.createImageFromJar("/res/img/my.png");
        item = menu.addItem(null, img);//BbStrings.getString("My")
        item.setName(UI_NAME_MENUITEM_MY);
        item.setActionListener((GObject gobj) -> {
            setCurrent(getMyPanel());
        });
        //item = menu.addItem("搜索", img);
    }

    public GContainer getChatSlots() {
        if (chatSlots == null) {
            chatSlots = new GViewSlot(align[ATT_SLOTS][WIDTH], align[ATT_SLOTS][HEIGHT], GViewSlot.SCROLL_MODE_HORIZONTAL);
            chatSlots.setLocation(align[ATT_SLOTS][LEFT], align[ATT_SLOTS][TOP]);
            chatSlots.setSize(align[ATT_SLOTS][WIDTH], align[ATT_SLOTS][HEIGHT]);

            createMainMenu();
            getCanvasPanel();
            getMyPanel();
            chatSlots.add(0, getSessionPanel(), GViewSlot.MOVE_FIXED);
            chatSlots.add(1, getChatPanel(), GViewSlot.MOVE_LEFT);
            chatSlots.reSize();
        }
        return chatSlots;
    }

    public GContainer getCanvasPanel() {
        if (gameView == null) {
            gameView = new GViewPort();
//            gameView.setLocation(align[ATT_GAMEVIEW][LEFT], align[ATT_GAMEVIEW][TOP]);
//            gameView.setSize(align[ATT_GAMEVIEW][WIDTH], align[ATT_GAMEVIEW][HEIGHT]);
            gameView.setLocation(0, 0);
            gameView.setSize(form.getDeviceWidth(), form.getDeviceHeight() - menuH - pad);
            bbClient.initGui(gameView);
        }
        return gameView;
    }

    GPanel getSessionPanel() {
        if (sessionPanel == null) {

            sessionPanel = new GPanel();
            sessionPanel.setLocation(align[ATT_CHATPAN][LEFT], align[ATT_CHATPAN][TOP]);
            sessionPanel.setSize(align[ATT_CHATPAN][WIDTH], align[ATT_CHATPAN][HEIGHT]);

            //left
            GButton addbtn = new GButton(BbStrings.getString("+ Add"), align[ATT_ADDBTN][LEFT], align[ATT_ADDBTN][TOP], align[ATT_ADDBTN][WIDTH], align[ATT_ADDBTN][HEIGHT]);
            //addbtn.setBgColor(0, 96, 128, 255);
            sessionPanel.add(addbtn);
            addbtn.setActionListener((GObject gobj) -> {
                showAddNewFrame();
            });

            GTextField search = new GTextField("", "search", align[ATT_SEARCH][LEFT], align[ATT_SEARCH][TOP], align[ATT_SEARCH][WIDTH], align[ATT_SEARCH][HEIGHT]);
            search.setBoxStyle(GTextField.BOX_STYLE_SEARCH);
            search.setStateChangeListener(new GStateChangeListener() {
                @Override
                public void onStateChange(GObject gobj) {
                    String str = search.getText();
                    if (sessionList != null) {
                        sessionList.filterLabelWithKey(str);
                    }
                }
            });
            sessionPanel.add(search);

            sessionList = new GSessionList(align[ATT_SESSIONLIST][LEFT], align[ATT_SESSIONLIST][TOP], align[ATT_SESSIONLIST][WIDTH], align[ATT_SESSIONLIST][HEIGHT]);
            sessionList.setShowMode(GList.MODE_MULTI_SHOW);
            sessionList.setItemHeight(50);
            sessionPanel.add(sessionList);


        }
        return sessionPanel;
    }

    GPanel getChatPanel() {
        if (chatPanel == null) {

            chatPanel = new GPanel();
            chatPanel.setLocation(align[ATT_CHATPAN][LEFT], align[ATT_CHATPAN][TOP]);
            chatPanel.setSize(align[ATT_CHATPAN][WIDTH], align[ATT_CHATPAN][HEIGHT]);


            //right
            GButton moreBtn = new GButton("...", align[ATT_MOREBTN][LEFT], align[ATT_MOREBTN][TOP], align[ATT_MOREBTN][WIDTH], align[ATT_MOREBTN][HEIGHT]);
            //moreBtn.setBgColor(0, 96, 128, 255);
            chatPanel.add(moreBtn);
            moreBtn.setActionListener((GObject gobj) -> {
                showMoreMenu();
            });

            GButton back2listBtn = new GButton("< " + BbStrings.getString("Session"), align[ATT_BACK2LISTBTN][LEFT], align[ATT_BACK2LISTBTN][TOP], align[ATT_BACK2LISTBTN][WIDTH], align[ATT_BACK2LISTBTN][HEIGHT]);
            chatPanel.add(back2listBtn);
            back2listBtn.setActionListener((GObject gobj) -> {
                chatPanelShowLeft();
            });

            GLabel nameLab = new GLabel("", align[ATT_NAMELAB][LEFT], align[ATT_NAMELAB][TOP], align[ATT_NAMELAB][WIDTH], align[ATT_NAMELAB][HEIGHT]);
            nameLab.setAlign(GGraphics.HCENTER | GGraphics.TOP);
            nameLab.setName("nameLab");
            chatPanel.add(nameLab);

            contentView = new GContentView(this);
            contentView.setLocation(align[ATT_CONTVIEW][LEFT], align[ATT_CONTVIEW][TOP]);
            contentView.setSize(align[ATT_CONTVIEW][WIDTH], align[ATT_CONTVIEW][HEIGHT]);
            chatPanel.add(contentView);

            mediaBtn = new GButton(BbStrings.getString("+"), align[ATT_MEDIABTN][LEFT], align[ATT_MEDIABTN][TOP], align[ATT_MEDIABTN][WIDTH], align[ATT_MEDIABTN][HEIGHT]);
            chatPanel.add(mediaBtn);
            mediaBtn.setActionListener((GObject gobj) -> {
                GList mediaMenu = GToolkit.getListMenu(new String[]{
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
                mediaMenu.setLocation(mediaBtn.getLocationLeft(), mediaBtn.getLocationTop() - mediaMenu.getH() - pad);
                form.add(mediaMenu);
                form.setFocus(mediaMenu);

            });

            msgBox = new GTextBox("", "Contents", align[ATT_MSGBOX][LEFT], align[ATT_MSGBOX][TOP], align[ATT_MSGBOX][WIDTH], align[ATT_MSGBOX][HEIGHT]) {
//                @Override
//                public void touchEvent(int phase, int x, int y) {
//                    super.touchEvent(phase, x, y);
//                    if (isInArea(x, y)) {
//                        if (phase == Glfm.GLFMTouchPhaseEnded) {
//                            msgBox.setKeyboardVisible(true);
//                        }
//                    }
//                }

            };
            //msgBox.setKeyboardAutoPop(false);
            chatPanel.add(msgBox);
            form.setKeyshowListener((boolean show, float x, float y, float w, float h) -> {
                //System.out.println("keyboardShow:" + show + "," + x + "," + y + "," + w + "," + h);
                if (show) {
                    reAlign(form.getDeviceWidth(), form.getDeviceHeight() - h);
                    reBoundle();
                } else {
                    reAlign(form.getDeviceWidth(), form.getDeviceHeight());
                    reBoundle();
                }
                GObject.flush();
            });

            sendBtn = new GButton(BbStrings.getString("Send"), align[ATT_SENDBTN][LEFT], align[ATT_SENDBTN][TOP], align[ATT_SENDBTN][WIDTH], align[ATT_SENDBTN][HEIGHT]);
            sendBtn.setBgColor(0, 96, 128, 255);
            chatPanel.add(sendBtn);
            sendBtn.setActionListener((GObject gobj) -> {
                GSessionItem gsi = curSelectedItem;
                if (gsi != null) {
                    getChat().sendTextMsg(gsi.groupInfo.getRoleId(), gsi.groupInfo.getGroupId(), msgBox.getText());
                    msgBox.getParent().setFocus(msgBox);
                    msgBox.setText("");
                }
            });

            msgBox.setUnionObj(sendBtn);
        }
        return chatPanel;
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

        GFrame frame = GToolkit.getInputFrame(BbStrings.getString("Add Friend"),
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

    GContainer getMyPanel() {
        if (myView == null) {
            myView = new GViewPort();
            myView.setLocation(align[ATT_MYVIEW][LEFT], align[ATT_MYVIEW][TOP]);
            myView.setSize(align[ATT_MYVIEW][WIDTH], align[ATT_MYVIEW][HEIGHT]);

            int pad = 5, x = 10, y = 10, w = (int) myView.getW() - 20, th = (int) myView.getH();

            float iwidth = w / 3;
            GImageItem myphotoItem = new GImageItem(getHead(bbClient.getRoleid()));
            myphotoItem.setName("imgItemMyphoto");
            myphotoItem.setActionListener(new GActionListener() {
                @Override
                public void action(GObject gobj) {
                    Glfm.glfmPickPhotoAlbum(form.getWinContext(), PICK_HEAD, Glfm.GLFMPickupTypeImage);
                }
            });
            myphotoItem.setLocation(x, y);
            myphotoItem.setSize(iwidth, iwidth);
            myView.add(myphotoItem);
            y += myphotoItem.getH() + pad * 2;

            GLabel accountLab = new GLabel(BbStrings.getString("Account : ") + bbClient.passport, pad, y, myView.getW() - pad * 2, addH);
            myView.add(accountLab);
            y += addH + pad;

            GLabel bbidLab = new GLabel(BbStrings.getString("My BBID : ") + bbClient.roleid, pad, y, myView.getW() - pad * 2, addH);
            myView.add(bbidLab);
            y += addH + pad;

            GTextField nameField = new GTextField("", "Change Name", pad, y, myView.getW() - pad * 3 - addW, addH);
            nameField.setName(UI_NAME_TEXTFIELD_CHANGE_NAME);
            myView.add(nameField);

            GButton btn = new GButton(BbStrings.getString("Change"), (myView.getW() - addW - pad), y, addW, addH);
            myView.add(btn);
            btn.setActionListener((GObject gobj) -> {
                String n = nameField.getText();
                getChat().sendFriendUpdate(bbClient.getRoleid(), n);
                GForm.addMessage(BbStrings.getString("Submited change"));
            });
            y += addH + pad;

            GLabel lb1 = new GLabel(BbStrings.getString("Request list"), x, y, w, addH);
            myView.add(lb1);
            y += addH + pad;

            GList requestList = new GList(x, y, w, 120);
            requestList.setName(REQUEST_LIST_NAME);
            requestList.setShowMode(GList.MODE_MULTI_SHOW);
            requestList.setScrollBar(true);
            myView.add(requestList);
            y += 120 + pad;

            GButton qrBtn = new GButton(BbStrings.getString("Qr Code"), pad, y, myView.getW() - pad * 2, 35);
            myView.add(qrBtn);
            qrBtn.setActionListener((GObject gobj) -> {
                if (getMyBbidQr() == null) {
                    GFrame gf = GToolkit.getConfirmFrame(BbStrings.getString("Notify"), BbStrings.getString("Qr Code is generating"), null, null, null, null);
                    form.add(gf);
                    gf.align(GGraphics.HCENTER | GGraphics.VCENTER);
                } else {
                    GViewPort qrView = GToolkit.getImageView(form, getMyBbidQr(), null);
                    form.add(qrView);
                    form.setFocus(qrView);
                }
            });
            y += 35 + pad;

            GButton clearBtn = new GButton(BbStrings.getString("Clear All"), pad, y, myView.getW() - pad * 2, 35);
            clearBtn.setBgColor(128, 16, 8, 255);
            myView.add(clearBtn);
            clearBtn.setActionListener((GObject gobj) -> {
                getChat().clearAll();
            });
            y += 35 + pad;

            GButton exitBtn = new GButton(BbStrings.getString("Exit to AppManager"), pad, y, myView.getW() - pad * 2, 35);
            //logoutBtn.setBgColor(128, 16, 8, 255);
            myView.add(exitBtn);
            exitBtn.setActionListener((GObject gobj) -> {
                AppManager.getInstance().active();
            });
            y += 35 + pad;

            GButton logoutBtn = new GButton(BbStrings.getString("Logout"), pad, y, myView.getW() - pad * 2, 35);
            //logoutBtn.setBgColor(128, 16, 8, 255);
            myView.add(logoutBtn);
            logoutBtn.setActionListener((GObject gobj) -> {
                bbClient.logout();
            });
            y += 35 + pad;
        }
        String name = bbClient.getGameRun().getMyPlayer().getName();
        GTextField nameField = (GTextField) myView.findByName(UI_NAME_TEXTFIELD_CHANGE_NAME);
        nameField.setText(name);

        GImageItem myphotoItem = (GImageItem) myView.findByName("imgItemMyphoto");
        //System.out.println("show mypanel " + getHead(bbClient.getRoleid()));
        myphotoItem.setImg(getHead(bbClient.getRoleid()));
        return myView;
    }

    @Override
    public void onGroupAdd(ChatGroupInfo sd, long lastMsgAt) {
        GSessionItem gsi = sessionList.findSessionItem(0, sd.groupid);
        if (gsi == null) {
            gsi = new GSessionItem(sd, this);
            gsi.groupInfo = sd;
            sessionList.addItem(0, gsi);
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
            sessionList.removeItem(go);
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
            gsi = new GSessionItem(mi, this);
            gsi.groupInfo = mi;
            sessionList.addItem(0, gsi);
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
            sessionList.removeItem(gsi);
            GForm.flush();
        }
    }

    @Override
    public void onFriendUpdated(MemberInfo mi) {
        if (mi.roleid == bbClient.getRoleid()) {
            GTextField nameField = (GTextField) myView.findByName(UI_NAME_TEXTFIELD_CHANGE_NAME);
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
            GContentItem ci = new GContentItem(mi, form, this);
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
            GListItem gli = list.addItems(null, BbStrings.getString("Request be friend") + " (" + friendid + ")" + nick);
            gli.setAttachment(friendid);
            gli.setActionListener((GObject gobj) -> {
                showConfirmAdd(gli);
            });

            //
            GMenuItem mi = (GMenuItem) menu.findByName(UI_NAME_MENUITEM_MY);
            if (mi != null) {
                mi.incMsgNew(1);
            }
        }
        GForm.flush();
    }

    void showConfirmAdd(GListItem gli) {
        GFrame frame = GToolkit.getConfirmFrame(BbStrings.getString("Request list"),
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
            moreMenu = new GList(align[ATT_MOREMENU][LEFT], align[ATT_MOREMENU][TOP], align[ATT_MOREMENU][WIDTH], align[ATT_MOREMENU][HEIGHT]);
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
            GListItem itemInfo = moreMenu.addItems(null, BbStrings.getString("Infomation"));
            itemInfo.setActionListener((GObject gobj) -> {
                showInfoFrame();
            });
            GListItem itemClear = moreMenu.addItems(null, BbStrings.getString("Clear Message"));
            itemClear.setActionListener((GObject gobj) -> {
                form.remove(moreMenu);

                GSessionItem gsi = curSelectedItem;
                if (gsi == null) {
                    return;
                }
                GFrame frame = GToolkit.getConfirmFrame(BbStrings.getString("Clear Message"),
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
            GListItem itemDelete = moreMenu.addItems(null, BbStrings.getString("Remove"));
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

            GListItem itemAddMember = moreMenu.addItems(null, BbStrings.getString("Add Friend"));
            itemAddMember.setActionListener((GObject gobj) -> {
                showAddMember();
            });
        }
        form.add(moreMenu);
        form.setFocus(moreMenu);
        moreMenu.setFront(true);
    }

    void showDeleteFriend(GSessionItem gsi) {
        GFrame frame = GToolkit.getConfirmFrame(BbStrings.getString("Remove Friend"),
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

        GFrame addMember = GToolkit.getListFrame(BbStrings.getString("Add Member"),
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

        GFrame removeMember = GToolkit.getListFrame(BbStrings.getString("Remove Member"),
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

        GFrame selMemberFrame = GToolkit.getListFrame(BbStrings.getString("Forward"),
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

        GFrame frame = new GFrame(BbStrings.getString("Infomation"), 0, 0, form.getDeviceWidth() * .85f, form.getDeviceHeight() * .7f);

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
        GImageItem imgItem = new GImageItem(img);
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
        GTextField nameField = new GTextField(name, "Change Name", pad, y, view.getW() - pad * 3 - btnW, btnH);
        //nameField.setName("NameField");
        view.add(nameField);

        GButton btn = new GButton(BbStrings.getString("Change"), (view.getW() - btnW - pad), y, btnW, btnH);
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
            GLabel nameLab = (GLabel) chatPanel.findByName("nameLab");
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
}
