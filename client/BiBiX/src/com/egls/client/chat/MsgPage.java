/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.client.chat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Gust
 */
public class MsgPage {

    int pageId;
    boolean dirty = false;

    long sessionid;
    long groupid;
    long firstAt;
    long endAt;

    int blockSize;
    List<MsgItem> items = new ArrayList();

    void load(byte[] b) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(b);
            DataInputStream dis = new DataInputStream(bais);
            sessionid = dis.readLong();
            groupid = dis.readLong();
            firstAt = dis.readLong();
            endAt = dis.readLong();
            blockSize = dis.readShort();

            //
            int itemSize = dis.readShort();
            for (int i = 0; i < itemSize; i++) {
                MsgItem mi = new MsgItem();
                mi.sessionRoleId = sessionid;
                mi.groupid = groupid;

                mi.time = dis.readLong();
                mi.fromid = dis.readLong();
                mi.toid = dis.readLong();
                mi.msg = dis.readUTF();
                items.add(mi);
            }
            //System.out.println("load data{" + pageId + "," + toid + "," + groupid + ",size=" + items.size() + "}");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    byte[] save() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.writeLong(sessionid);
            dos.writeLong(groupid);
            dos.writeLong(firstAt);
            dos.writeLong(endAt);
            dos.writeShort(0);
            dos.writeShort(items.size());
            for (MsgItem mi : items) {
                dos.writeLong(mi.time);
                dos.writeLong(mi.fromid);
                dos.writeLong(mi.toid);
                dos.writeUTF(mi.msg == null ? "" : mi.msg);
            }
            byte[] b = baos.toByteArray();
            setLength(b.length, b);
            System.out.println("save data{" + pageId + "," + sessionid + "," + groupid + ",size=" + items.size() + "}");
            return b;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    void setLength(int len, byte[] b) {
        b[32] = (byte) (len >> 8);
        b[33] = (byte) (len);
    }

    public boolean isInRange(long time) {
        return firstAt <= time && time <= endAt;
    }

    public List<MsgItem> getItems() {
        return items;
    }

    public void addItem(MsgItem mi) {
        items.add(mi);
        blockSize += mi.getSerialSize();
        endAt = mi.time;
    }

    public int getPageId() {
        return pageId;
    }

    public void clear(MsgDatabase base) {
        for(MsgItem mi:items){
            mi.clear(base);
        }
        sessionid = 0;
        groupid = 0;
        items.clear();
        firstAt = endAt = 0;
        dirty = true;
        blockSize = 0;
    }
    
    public void loadMedia(MsgDatabase base) {
        for(MsgItem mi:items){
            mi.loadMedia(base);
        }
    }
}
