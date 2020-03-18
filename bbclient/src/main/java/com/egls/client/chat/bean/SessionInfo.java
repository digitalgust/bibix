/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.client.chat.bean;

/**
 *
 * @author Gust
 */
public abstract class SessionInfo {

    public long lastMsgAt;

    public abstract boolean isGroup();

    public abstract long getId();

    public abstract String getName();

    public boolean match(long roleid, long groupid) {
        if (isGroup()) {
            if (groupid == getId()) {
                return true;
            } else {
                return false;
            }
        } else if (roleid == getId()) {
            return true;
        } else {
            return false;
        }
    }

    public long getRoleId() {
        if (isGroup()) {
            return 0;
        }
        return getId();
    }

    public long getGroupId() {
        if (isGroup()) {
            return getId();
        }
        return 0;
    }
}
