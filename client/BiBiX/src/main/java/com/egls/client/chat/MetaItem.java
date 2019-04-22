/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.client.chat;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Gust
 */
public class MetaItem {

    long roleid;
    long groupid;
    long lastMsgAt;
    String key;

    List<Integer> pageIds = new ArrayList();

    public void parseMeta(String s) {
        String[] strs = s.split(",");
        int pos = 0;
        try {
            roleid = Long.parseLong(strs[pos++]);
            groupid = Long.parseLong(strs[pos++]);
            lastMsgAt = Long.parseLong(strs[pos++]);
            key = roleid + "," + groupid;
            for (int i = pos, imax = strs.length; i < imax; i++) {
                pageIds.add(Integer.parseInt(strs[i]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(roleid).append(",").append(groupid).append(",").append(lastMsgAt).append(",");
        for (Integer in : pageIds) {
            sb.append(in).append(",");
        }
        return sb.toString();
    }

    public int getLastSerialIds() {
        if (pageIds.size() > 0) {
            return pageIds.get(pageIds.size() - 1);
        }
        return -1;
    }

    public int getFirstSerialIds() {
        if (pageIds.size() > 0) {
            return pageIds.get(0);
        }
        return -1;
    }

    public int getAndRemoveLastSerialIds() {
        if (pageIds.size() > 0) {
            return pageIds.remove(pageIds.size() - 1);
        }
        return -1;
    }

    public int getMaxSerialId() {
        int max = 0;
        for (Integer i : pageIds) {
            if (i > max) {
                max = i;
            }
        }
        return max;
    }

}
