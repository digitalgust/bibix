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
 * @author gust
 */
public class MsgItem {

    public static final int TYPE_TEXT = 0;
    public static final int TYPE_IMAGE = 1;
    public static final String STRING_IMAGE = ".jpeg";
    public static final int TYPE_VOICE = 2;
    public static final String STRING_VOICE = ".wav";
    public static final int TYPE_VIDEO = 3;
    public static final String STRING_VIDEO = ".mp4";
    public static final int TYPE_FILE = 4;

    static final String MEDIA_SCHEMA = "MEDIA://";//   CONTENTFILE://aa.wav


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

    public MsgItem(String extName, byte[] data) {
        msg = MEDIA_SCHEMA + extName + "@";
        thumb = data;
    }

    public boolean isMediaMsg() {
        return msg.startsWith(MEDIA_SCHEMA);
    }

    public int getMediaType() {
        if (msg.startsWith(MEDIA_SCHEMA)) {
            if (msg.startsWith(MEDIA_SCHEMA + STRING_IMAGE)) {
                return TYPE_IMAGE;
            } else if (msg.startsWith(MEDIA_SCHEMA + STRING_VOICE)) {
                return TYPE_VOICE;
            } else if (msg.startsWith(MEDIA_SCHEMA + STRING_VIDEO)) {
                return TYPE_VIDEO;
            } else {
                return TYPE_FILE;
            }
        }
        return TYPE_TEXT;
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
        int msgType = getMediaType();
        if (msgType == TYPE_IMAGE || msgType == TYPE_VOICE) {
            thumb = base.getMedia(getMediaId());
        }
    }
}
