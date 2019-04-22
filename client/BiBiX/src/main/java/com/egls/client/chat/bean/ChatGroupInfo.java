/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.client.chat.bean;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Gust
 */
public class ChatGroupInfo extends SessionInfo {

    public long ownerid;
    public long groupid;
    public String name;
    public final Map<Long, MemberInfo> members = new LinkedHashMap();

    public boolean detailReceived;


    public String toString() {
        return name + "(" + groupid + ")";
    }

    @Override
    public int hashCode() {
        int r = (int) (ownerid ^ (ownerid >>> 32));
        int s = (int) (groupid ^ (groupid >>> 32));
        return r ^ s;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ChatGroupInfo) {
            ChatGroupInfo csi = (ChatGroupInfo) obj;
            return ownerid == csi.ownerid && groupid == csi.groupid;
        }
        return false;
    }

    public boolean isMember(long roleid) {
        return members.get(roleid) != null;
    }

    @Override
    public long getId() {
        return groupid;
    }

    @Override
    public String getName() {
        return name;
    }

    public boolean isGroup() {
        return true;
    }
}
