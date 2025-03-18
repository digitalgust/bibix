/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.client.extgui;

import org.mini.gui.GForm;
import org.mini.gui.GList;
import org.mini.gui.GListItem;
import org.mini.gui.GObject;

import java.util.List;

/**
 *
 * @author GustXList
 */
public class GSessionList extends GList {

    public GSessionList(GForm form, float left, float top, float width, float height) {
        super(form,left, top, width, height);
    }

    public GSessionItem findSessionItem(long friendid, long sessionid) {
        for (GListItem go : getItems()) {
            GSessionItem gsi = (GSessionItem) go;
            if (gsi.groupInfo.getRoleId() == friendid && gsi.groupInfo.getGroupId() == sessionid) {
                return gsi;
            }
        }
        return null;
    }

    public void reSort() {
        sort((GObject o1, GObject o2) -> {
            GSessionItem gsi1 = (GSessionItem) o1;
            GSessionItem gsi2 = (GSessionItem) o2;
            //System.out.println(gsi1.getLabel() + " : " + gsi1.groupInfo.lastMsgAt + "      " + gsi2.getLabel() + " : " + gsi2.groupInfo.lastMsgAt);
            long v = gsi1.groupInfo.lastMsgAt - gsi2.groupInfo.lastMsgAt;
            if (v > 0) {
                return -1;
            } else if (v == 0) {
                return 0;
            } else {
                return 1;
            }
        });
        reAlignItems();
    }

}
