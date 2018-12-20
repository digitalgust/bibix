/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.client.chat;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gust
 */
public class MsgItem {

    public static final int TYPE_UNKNOW = 0;
    public static final int TYPE_IMAGE = 1;
    public static final int TYPE_VOICE = 2;

    static final String MEDIA_SCHEMA = "MEDIA://";//
    static final String MEDIA_SCHEMA_IMAGE = "MEDIA://IMAGE@";//example:      MEDIA://IMAGE@8E29384092A73B321.jpeg 
    static final String MEDIA_SCHEMA_VOICE = "MEDIA://VOICE@";//example:      MEDIA://VOICE@8E29384092A73B321.wav 


    static final char fieldSplitor = '|';
    static final char lineSplitor = '\n';
    
    //
    public long time;
    public long fromid;
    public long toid;
    public long sessionRoleId;//put in someone's session
    public long groupid;
    public String msg;
    public byte[] thumb;

    public MsgItem() {

    }

    public MsgItem(int type, byte[] data) {
        switch (type) {
            case TYPE_IMAGE: {
                msg = MEDIA_SCHEMA_IMAGE;
                break;
            }
            case TYPE_VOICE: {
                msg = MEDIA_SCHEMA_VOICE;
                break;
            }
        }
        thumb = data;
    }

    public boolean isMediaMsg() {
        return msg.startsWith(MEDIA_SCHEMA);
    }

    public int getMediaType() {
        if (msg.startsWith(MEDIA_SCHEMA_IMAGE)) {
            return TYPE_IMAGE;
        } else if (msg.startsWith(MEDIA_SCHEMA_VOICE)) {
            return TYPE_VOICE;
        }

        return TYPE_UNKNOW;
    }

    public String getMediaId() {
        if (msg.indexOf('@') < 0) {
            return null;
        }
        return msg.substring(msg.indexOf('@') + 1);
    }

    public static List<String> split(String s, char splitor) {
        List<String> list = new ArrayList();
        if (s != null) {
            int index = 0, lastIndex = 0;
            while ((index = s.indexOf(splitor, index)) != -1) {
                String t = s.substring(lastIndex, index);
                list.add(t);
                index++;
                lastIndex = index;
            }
        }
        return list;
    }

    public int getSerialSize() {
        int size = 28;
        try {
            if (msg != null) {
                size += msg.getBytes("utf-8").length;
            }
        } catch (UnsupportedEncodingException ex) {
        }
        return size;
    }

    public void clear(MsgDatabase base) {
        if (isMediaMsg()) {
            base.removeMedia(getMediaId());
        }
    }

    public void loadMedia(MsgDatabase base) {
        if (isMediaMsg()) {
            thumb = base.getMedia(getMediaId());
        }
    }
}
