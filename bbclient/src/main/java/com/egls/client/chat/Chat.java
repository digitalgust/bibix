/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.client.chat;

import com.egls.client.BbMain;
import com.egls.client.BbClient;
import com.egls.client.chat.bean.ChatGroupInfo;
import com.egls.client.chat.bean.MemberInfo;
import com.egls.client.game.Const;
import com.egls.client.netmgr.CmdPkg;
import org.mini.crypt.XorCrypt;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @author Gust
 */
public class Chat {

    Map<Long, ChatGroupInfo> groups = Collections.synchronizedMap(new HashMap());
    Map<Long, MemberInfo> friends = Collections.synchronizedMap(new LinkedHashMap());
    Map<Long, MemberInfo> requests = Collections.synchronizedMap(new LinkedHashMap());

    ChatStateListener listener;

    BbClient bbClient;
    MsgDatabase msgdb;
    ImageDatabase imagedb;

    List<Long> groupNew;

    static final int MAX_SPEAK_LEN = 500;

    boolean deviceTokenSent = false;

    public Chat(BbClient bb) {
        bbClient = bb;

        msgdb = new MsgDatabase(BbMain.getInstance().getSaveRoot(), bbClient.getRoleid());
        imagedb = new ImageDatabase(BbMain.getInstance().getSaveRoot(), "");


    }

    public Map<Long, ChatGroupInfo> getSessions() {
        return groups;
    }

    public ChatGroupInfo getGroup(long sessionid) {
        ChatGroupInfo csi = groups.get(sessionid);
        if (csi == null) {
            csi = new ChatGroupInfo();
            csi.groupid = sessionid;
            groups.put(sessionid, csi);
        }
        return csi;
    }

    public MemberInfo getFriend(long roleid) {
        MemberInfo mi = friends.get(roleid);
        if (mi == null) {
            mi = new MemberInfo();
            mi.roleid = roleid;
            mi.name = "";
            friends.put(roleid, mi);
        }
        return mi;
    }

    public String genQrImage() {
        String s = "http://bb.egls.cn/qr?cmd=add&bbid=" + bbClient.getRoleid();
        return s;
    }

    public void parseQrString(String s) {
        //System.out.println("qrcode :" + s);
        if (s != null) {
            int idx = s.indexOf("bbid=");
            if (idx > 0) {
                String bbid = s.substring(idx + 5);
                if (bbid.indexOf("&") > 0) {
                    bbid = bbid.substring(0, bbid.indexOf("&"));
                }
                try {
                    long id = Long.parseLong(bbid);
                    sendFriendRequest(id);
                } catch (Exception e) {
                }
            }
        }
    }

    public Map<Long, MemberInfo> getFriends() {
        return friends;
    }

    public Map<Long, MemberInfo> getRequests() {
        return requests;
    }

    public MsgDatabase getMsgDatabase() {
        return msgdb;
    }

    public void setChatStateListener(ChatStateListener listener) {
        this.listener = listener;
    }

    public ImageDatabase getImageDatabase() {
        return imagedb;
    }

    public void initFriendList() {
        sendFriendListGet(bbClient.getRoleid());
        sendGroupListGet(bbClient.getRoleid());

        Collection<MetaItem> metas = getMsgDatabase().getMetas();
        for (MetaItem mi : metas) {
            if (mi.groupid == 0) {
                if (mi.roleid != 0) {
                    MemberInfo info = getFriend(mi.roleid);
                    listener.onFriendAdd(info, mi.lastMsgAt);
                }
            } else {
                ChatGroupInfo csi = getGroup(mi.groupid);
                listener.onGroupAdd(csi, mi.lastMsgAt);
            }
        }

        byte[] b = imagedb.getMyBbidQr();
        if (b == null) {
            sendQrEncode(genQrImage(), (CmdPkg cmd) -> {
                int size = cmd.readInt();
                byte[] b1 = cmd.readByteArray(size);
                imagedb.setMyBbidQr(b1);
            });
        }
    }

    byte[] toUtf8(String s) {
        try {
            byte[] b = s.getBytes("utf-8");
            return b;
        } catch (UnsupportedEncodingException ex) {
        }
        return null;
    }

    String utf8ToStr(byte[] b) {
        try {
            String s = new String(b, "utf-8");
            return s;
        } catch (UnsupportedEncodingException ex) {
        }
        return null;
    }

    public void processCmd(CmdPkg cmd) {
        while (listener == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
        }
        int cat = cmd.getCatId();
        switch (cmd.getCommandID()) {
            case Const.S_CHATSESSION_SPEAK: {
                receiveSpeak(cmd);
                break;
            }
            case Const.S_CHATSESSION_FRIENDLIST_GET: {
                receiveFriendListGet(cmd);
                break;
            }
            case Const.S_CHATSESSION_QR_DECODE: {
                receiveQrDecode(cmd);
                break;
            }
            case Const.S_CHATSESSION_QR_ENCODE: {
                receiveQrEncode(cmd);
                break;
            }
            case Const.S_CHATSESSION_FRIEND_ADD: {
                receiveFriendAdd(cmd);
                break;
            }
            case Const.S_CHATSESSION_FRIEND_REMOVE: {
                receiveFriendRemove(cmd);
                break;
            }
            case Const.S_CHATSESSION_FRIEND_UPDATE: {
                receiveFriendUpdate(cmd);
                break;
            }
            case Const.S_CHATSESSION_FRIEND_REQUEST: {
                receiveFriendRequest(cmd);
                break;
            }
            case Const.S_CHATSESSION_SESSION_GET: {
                receiveGroupGet(cmd);
                break;
            }
            case Const.S_CHATSESSION_SESSION_ADD: {
                receiveGroupAdd(cmd);
                break;
            }
            case Const.S_CHATSESSION_SESSION_REMOVE: {
                receiveGroupRemove(cmd);
                break;
            }
            case Const.S_CHATSESSION_SESSION_UPDATE: {
                receiveGroupUpdate(cmd);
                break;
            }
            case Const.S_CHATSESSION_SESSION_MEMBER_ADD: {
                receiveGroupMemberAdd(cmd);
                break;
            }
            case Const.S_CHATSESSION_SESSION_MEMBER_REMOVE: {
                receiveGroupMemberRemove(cmd);
                break;
            }
            case Const.S_CHATSESSION_SESSION_MEMBER_UPDATE: {
                receiveGroupMemberUpdate(cmd);
                break;
            }
            case Const.S_CHATSESSION_SESSION_LIST_GET: {
                receiveGroupListGet(cmd);
                break;
            }
            case Const.S_CHATSESSION_HEAD_GET: {
                receiveHeaderGet(cmd);
                break;
            }
        }
    }

    public void sendGroupGet(long sessionId) {
        CmdPkg cmd = new CmdPkg(Const.C_CHATSESSION_SESSION_GET);
        cmd.writeLong(sessionId);
        //System.out.println("sendGroupGet :" + ownerid + "," + sessionId);
        bbClient.send(cmd);
    }

    public void receiveGroupGet(CmdPkg pkg) {
        long ownerid = pkg.readLong();
        long sessionid = pkg.readLong();
        ChatGroupInfo csd = getGroup(sessionid);
        csd.ownerid = ownerid;
        csd.detailReceived = true;
        csd.groupid = sessionid;
        csd.name = pkg.readUTF();

        int size = pkg.readInt();
        for (int i = 0; i < size; i++) {
            MemberInfo mi = new MemberInfo();
            mi.roleid = pkg.readLong();
            mi.name = pkg.readUTF();
            csd.members.put(mi.roleid, mi);
        }
        //System.out.println("receiveGroupGet " + ownerid + "-" + groupid);
        listener.onGroupAdd(csd, 0);
    }

    public void sendGroupAdd(List<Long> members) {
        CmdPkg cmd = new CmdPkg(Const.C_CHATSESSION_SESSION_ADD);
        cmd.writeLong(0);
        cmd.writeUTF("");
        bbClient.send(cmd);

        groupNew = members;
    }

    public void receiveGroupAdd(CmdPkg pkg) {
        long sessionid = pkg.readLong();
        ChatGroupInfo csi = getGroup(sessionid);
        if (groupNew != null) {
            sendGroupMemberAdd(csi.groupid, groupNew);
            groupNew = null;
        }
        //System.out.println("receiveGroupAdd " + ownerid + "-" + groupid);
        listener.onGroupAdd(csi, 0);
    }

    public void sendGroupRemove(long sessionid) {
        CmdPkg cmd = new CmdPkg(Const.C_CHATSESSION_SESSION_REMOVE);
        cmd.writeLong(sessionid);
        cmd.writeUTF("");
        bbClient.send(cmd);
    }

    public void receiveGroupRemove(CmdPkg pkg) {
        ChatGroupInfo csi = new ChatGroupInfo();
        csi.groupid = pkg.readLong();
        groups.remove(csi.groupid);
        listener.onGroupRemove(csi);
    }

    public void sendGroupUpdate(long sessionid, String groupname) {
        CmdPkg cmd = new CmdPkg(Const.C_CHATSESSION_SESSION_UPDATE);
        cmd.writeLong(sessionid);
        if (groupname == null) {
            groupname = "group -" + sessionid;
        }
        cmd.writeUTF(groupname);
        bbClient.send(cmd);
    }

    public void receiveGroupUpdate(CmdPkg pkg) {
        long ownerid = pkg.readLong();
        long sessionid = pkg.readLong();
        String name = pkg.readUTF();
        ChatGroupInfo csi = getGroup(sessionid);
        csi.ownerid = ownerid;
        csi.name = name;
        listener.onGroupUpdate(csi);
    }

    public void sendGroupMemberAdd(long sessionid, List<Long> member) {
        CmdPkg cmd = new CmdPkg(Const.C_CHATSESSION_SESSION_MEMBER_ADD);
        cmd.writeLong(sessionid);
        cmd.writeInt(member.size());
        for (int i = 0, imax = member.size(); i < imax; i++) {
            cmd.writeLong(member.get(i));
            cmd.writeUTF("");
        }
        bbClient.send(cmd);
    }

    public void receiveGroupMemberAdd(CmdPkg pkg) {
        long roleid = pkg.readLong();
        long sessionid = pkg.readLong();
        ChatGroupInfo info = groups.get(sessionid);
        if (info != null) {
            int size = pkg.readInt();
            for (int i = 0; i < size; i++) {
                MemberInfo mi = new MemberInfo();
                mi.roleid = pkg.readLong();
                mi.name = pkg.readUTF();
                info.members.put(mi.roleid, mi);
            }
        }
    }

    public void sendGroupMemberRemove(long friendid, long sessionid, List<Long> delids) {
        CmdPkg cmd = new CmdPkg(Const.C_CHATSESSION_SESSION_MEMBER_REMOVE);
        cmd.writeLong(friendid);
        cmd.writeLong(sessionid);
        cmd.writeInt(delids.size());
        for (int i = 0, imax = delids.size(); i < imax; i++) {
            cmd.writeLong(delids.get(i));
        }
        bbClient.send(cmd);
    }

    public void receiveGroupMemberRemove(CmdPkg pkg) {
        long roleid = pkg.readLong();
        long sessionid = pkg.readLong();
        ChatGroupInfo info = groups.get(sessionid);
        if (info != null) {
            int size = pkg.readInt();
            for (int i = 0; i < size; i++) {
                long removeid = pkg.readLong();
                info.members.remove(removeid);
            }
        }
    }

    public void sendGroupMemberUpdate(long sessionid, MemberInfo mi) {
        CmdPkg cmd = new CmdPkg(Const.C_CHATSESSION_SESSION_MEMBER_UPDATE);
        cmd.writeLong(sessionid);
        cmd.writeLong(mi.roleid);
        cmd.writeUTF(mi.name);
        bbClient.send(cmd);
    }

    public void receiveGroupMemberUpdate(CmdPkg pkg) {
        long roleid = pkg.readLong();
        long sessionid = pkg.readLong();
        long chgRoleid = pkg.readLong();
        String nick = pkg.readUTF();
        ChatGroupInfo info = groups.get(sessionid);
        if (info != null) {
            MemberInfo mi = info.members.get(chgRoleid);
            if (mi != null) {
                mi.name = nick;
            }
        }
    }

    public void sendFriendListGet(long roleid) {
        CmdPkg cmd = new CmdPkg(Const.C_CHATSESSION_FRIENDLIST_GET);
        cmd.writeLong(roleid);
        bbClient.send(cmd);
    }

    public void receiveFriendListGet(CmdPkg pkg) {
        //friends
        int size = pkg.readInt();
        for (int i = 0; i < size; i++) {
            long roleid = pkg.readLong();
            MemberInfo fd = getFriend(roleid);
            fd.name = pkg.readUTF();
            listener.onFriendAdd(fd, 0);
            //get head
            byte[] b = imagedb.getHeader(fd.roleid);
            sendHeadGet(fd.roleid, b == null ? 0 : b.length);
        }

    }

    public void sendGroupListGet(long roleid) {
        CmdPkg cmd = new CmdPkg(Const.C_CHATSESSION_SESSION_LIST_GET);
        cmd.writeLong(roleid);
        bbClient.send(cmd);
    }

    public void receiveGroupListGet(CmdPkg pkg) {
        int size = pkg.readInt();
        for (int i = 0; i < size; i++) {
            long sessionid = pkg.readLong();

            //System.out.println("receiveGroupListGet " + ownerid + "-" + groupid);
            ChatGroupInfo csd = getGroup(sessionid);

            csd.name = pkg.readUTF();
            listener.onGroupAdd(csd, 0);
        }

    }

    public void sendAttachmentGet(String resourceid) {
        CmdPkg cmd = new CmdPkg(Const.C_CHATSESSION_ATTACHMENT_GET);
        cmd.writeUTF(resourceid);
        bbClient.send(cmd);
    }

    public void receiveAttachment(CmdPkg pkg) {
        int size = pkg.readInt();
        if (size == 0) {
            String msg = pkg.readUTF();
            listener.onReceiveAttachment(msg, null);
        } else {
            byte[] b = pkg.readByteArray(size);
            String resourceid = pkg.readUTF();
            listener.onReceiveAttachment(resourceid, b);
        }
    }

    public void sendTextMsg(long toid, long groupid, String s) {
        MsgItem mi = new MsgItem();
        mi.time = System.currentTimeMillis();
        mi.toid = toid;
        mi.toid = toid;
        mi.fromid = bbClient.getRoleid();
        mi.groupid = groupid;
        mi.msg = s;

        sendSpeak(mi);
    }

    public void sendFileMsg(long toid, long groupid, String fileName, byte[] image) {
        MsgItem mi = new MsgItem(fileName, image);
        mi.fromid = bbClient.getRoleid();
        mi.toid = toid;
        mi.groupid = groupid;
        sendSpeak(mi);
    }


    public void sendSpeak(MsgItem mi) {
        //msg too long
        //split msg 
        if (mi.msg.length() > MAX_SPEAK_LEN) {
            for (int i = 0, imax = mi.msg.length() / MAX_SPEAK_LEN + 1; i < imax; i++) {
                MsgItem tmp = new MsgItem();
                tmp.toid = mi.toid;
                tmp.fromid = mi.fromid;
                tmp.groupid = mi.groupid;
                int start = i * MAX_SPEAK_LEN;
                int end = (i + 1) * MAX_SPEAK_LEN;
                if (end > mi.msg.length()) {
                    end = mi.msg.length();
                }
                tmp.msg = mi.msg.substring(start, end);
                sendSpeakImpl(tmp);
            }
        } else {
            sendSpeakImpl(mi);
        }

    }

    private void sendSpeakImpl(MsgItem mi) {
        if (mi.msg == null || mi.msg.length() == 0) {
            return;
        }
        CmdPkg cmd = new CmdPkg(Const.C_CHATSESSION_SPEAK);
        cmd.writeLong(mi.fromid);
        cmd.writeLong(mi.toid);
        cmd.writeLong(mi.groupid);

        byte[] utf8Msg = toUtf8(mi.msg);
        utf8Msg = XorCrypt.xor_encrypt(utf8Msg, bbClient.getXorkey());
        cmd.writeInt(utf8Msg.length);
        cmd.writeByteArray(utf8Msg);
        if (mi.thumb == null) {
            cmd.writeInt(0);
        } else {
            cmd.writeInt(mi.thumb.length);
            cmd.writeByteArray(XorCrypt.xor_encrypt(mi.thumb, bbClient.getXorkey()));
        }
        bbClient.send(cmd);
    }

    public void receiveSpeak(CmdPkg pkg) {
        int serialNo = pkg.readInt();
        long fromid = pkg.readLong();
        long toid = pkg.readLong();
        long groupid = pkg.readLong();
        long time = pkg.readLong();
        int len = pkg.readInt();
        byte[] mb = pkg.readByteArray(len);
        String msg = utf8ToStr(XorCrypt.xor_decrypt(mb, bbClient.getXorkey()));
        int attachmentSize = pkg.readInt();
        byte[] b = null;
        if (attachmentSize > 0) {
            b = XorCrypt.xor_decrypt(pkg.readByteArray(attachmentSize), bbClient.getXorkey());
        }
        if (groupid > 0) {
            ChatGroupInfo csi = getGroup(groupid);
            if (csi.members.isEmpty()) {
                sendGroupGet(groupid);
            }
        }
        MsgItem item = new MsgItem();

        item.fromid = fromid;
        item.time = time;
        item.toid = toid;
        item.groupid = groupid;
        item.msg = msg;
        item.thumb = b;
        //
        if (groupid == 0) {
            item.sessionRoleId = fromid == bbClient.getRoleid() ? toid : fromid;
        }

        getMsgDatabase().putMsg(item);
        listener.onReceiveMsg(item);
        sendSpeakRcvSuccess(serialNo);
    }

    void sendSpeakRcvSuccess(int serialNo) {
        CmdPkg cmd = new CmdPkg(Const.C_CHATSESSION_SPEAK_OK);
        cmd.writeInt(serialNo);
        bbClient.send(cmd);
    }

    public void receiveFriendAdd(CmdPkg pkg) {
        long roleid = pkg.readLong();
        MemberInfo mi = getFriend(roleid);
        listener.onFriendAdd(mi, System.currentTimeMillis());
    }

    public void sendFriendAdd(long roleid, boolean ok) {
        CmdPkg cmd = new CmdPkg(Const.C_CHATSESSION_FRIEND_ADD);
        cmd.writeBoolean(ok);
        cmd.writeLong(roleid);
        bbClient.send(cmd);
    }

    public void sendFriendRequest(long friendid) {
        CmdPkg cmd = new CmdPkg(Const.C_CHATSESSION_FRIEND_REQUEST);
        cmd.writeLong(friendid);
        bbClient.send(cmd);
    }

    public void receiveFriendRequest(CmdPkg pkg) {
        long roleid = pkg.readLong();
        String nick = pkg.readUTF();
        listener.onFriendRequest(roleid, nick);
    }

    public void sendFriendRemove(long friendid) {
        CmdPkg cmd = new CmdPkg(Const.C_CHATSESSION_FRIEND_REMOVE);
        cmd.writeLong(friendid);
        bbClient.send(cmd);
    }

    public void receiveFriendRemove(CmdPkg pkg) {
        long roleid = pkg.readLong();
        MemberInfo mi = friends.remove(roleid);
        if (mi != null) {
            listener.onFriendRemove(mi);
        }
    }

    public void sendFriendUpdate(long roleid, String name) {
        CmdPkg cmd = new CmdPkg(Const.C_CHATSESSION_FRIEND_UPDATE);
        cmd.writeLong(roleid);
        cmd.writeUTF(name);
        bbClient.send(cmd);
    }

    public void receiveFriendUpdate(CmdPkg pkg) {
        long roleid = pkg.readLong();
        String name = pkg.readUTF();
        MemberInfo mi = getFriend(roleid);
        if (mi != null) {
            mi.name = name;
        }
        listener.onFriendUpdated(mi);
    }

    //    public void sendReconnect() {
//        CmdPkg cmd = new CmdPkg(Const.C_CHATSESSION_RECONNECT);
//        cmd.writeLong(bbClient.getRoleid());
//        bbClient.send(cmd);
//    }
    Map<Long, CmdCallback> qrstack = new HashMap();

    public void sendQrDecode(byte[] imgBytes, CmdCallback callback) {
        if (imgBytes == null) {
            return;
        }
        CmdPkg cmd = new CmdPkg(Const.C_CHATSESSION_QR_DECODE);
        long requestCode = System.currentTimeMillis();
        cmd.writeLong(requestCode);
        cmd.writeInt(imgBytes.length);
        cmd.writeByteArray(imgBytes);
        bbClient.send(cmd);
        qrstack.put(requestCode, callback);
    }

    public void receiveQrDecode(CmdPkg pkg) {
        long requestCode = pkg.readLong();
        CmdCallback callback = qrstack.remove(requestCode);
        callback.onBack(pkg);
    }

    public void sendQrEncode(String s, CmdCallback callback) {
        if (s == null) {
            return;
        }
        CmdPkg cmd = new CmdPkg(Const.C_CHATSESSION_QR_ENCODE);
        long requestCode = System.currentTimeMillis();
        cmd.writeLong(requestCode);
        cmd.writeUTF(s);
        bbClient.send(cmd);
        qrstack.put(requestCode, callback);
    }

    public void receiveQrEncode(CmdPkg pkg) {
        long requestCode = pkg.readLong();
        CmdCallback callback = qrstack.remove(requestCode);
        callback.onBack(pkg);
    }

    public void sendClientActive(boolean active) {

        CmdPkg cmd = new CmdPkg(Const.C_CHATSESSION_ACTIVE);
        cmd.writeBoolean(active);
        bbClient.send(cmd);
        //System.out.println("client active:" + active);

        byte[] b = imagedb.getHeader(bbClient.getRoleid());
        sendHeadGet(bbClient.getRoleid(), b == null ? 0 : b.length);

    }

    public void sendDeviceToken() {
        if (!deviceTokenSent) {
            String token = System.getProperty("glfm.device.token");
            String uuid = System.getProperty("glfm.uuid");
            if (token != null && token.length() > 0 && uuid != null && uuid.length() > 0) {
                CmdPkg cmd = new CmdPkg(Const.C_CHATSESSION_DEVICE_TOKEN);
                cmd.writeUTF(uuid);
                cmd.writeUTF(token);
                deviceTokenSent = true;
                bbClient.send(cmd);
                System.out.println("send device token:" + token + " uuid:" + uuid);
            }
        }
    }

    public void clearAll() {
        getImageDatabase().clearAll();
        getMsgDatabase().clearAll();
        bbClient.logout();
    }

    public void sendHeadGet(long roleid, int fileSize) {
        CmdPkg cmd = new CmdPkg(Const.C_CHATSESSION_HEAD_GET);
        cmd.writeLong(roleid);
        cmd.writeInt(fileSize);
        bbClient.send(cmd);
    }

    private void receiveHeaderGet(CmdPkg cmd) {
        long roleid = cmd.readLong();
        int size = cmd.readInt();
        byte[] b = cmd.readByteArray(size);
        System.out.println("received head image " + roleid + " size:" + size);
        getImageDatabase().putHeader(roleid, b);
    }

    public void setMyHead(byte[] data) {
        if (data == null) {
            return;
        }
        getImageDatabase().putHeader(bbClient.getRoleid(), data);

        CmdPkg cmd = new CmdPkg(Const.C_CHATSESSION_HEAD_SET);
        cmd.writeInt(data.length);
        cmd.writeByteArray(data);
        bbClient.send(cmd);
    }

}
