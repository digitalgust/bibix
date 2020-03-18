/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.client.chat.bean;

import java.util.Collection;

/**
 *
 * @author Gust
 */
public class MemberInfo extends SessionInfo {

    static final String splitor = "\n";
    public long roleid;
    public String name;

    public String toString() {
        return name + "(" + roleid + ")";
    }

    @Override
    public int hashCode() {
        return (int) (roleid ^ (roleid >>> 32));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MemberInfo) {
            return roleid == ((MemberInfo) obj).roleid;
        }
        return false;
    }

    static public void parseMembers(String ms, Collection<MemberInfo> members) {
        String[] strs = ms.split(splitor);
        for (int i = 0, imax = strs.length / 2; i < imax; i += 2) {
            try {
                MemberInfo fi = new MemberInfo();
                fi.roleid = Long.parseLong(strs[i]);
                fi.name = strs[i + 1];
                members.add(fi);
            } catch (Exception e) {
            }
        }
    }

    static public String parkMembers(Collection<MemberInfo> members) {
        StringBuilder sb = new StringBuilder();
        for (MemberInfo mi : members) {
            sb.append(mi.roleid).append(splitor);
            sb.append(mi.name).append(splitor);
        }
        return sb.toString();
    }

    @Override
    public long getId() {
        return roleid;
    }

    @Override
    public String getName() {
        return name;
    }

    public boolean isGroup() {
        return false;
    }
}
