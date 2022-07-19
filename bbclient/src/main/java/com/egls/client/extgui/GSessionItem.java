/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.client.extgui;

import com.egls.client.BbChatUI;
import com.egls.client.chat.bean.SessionInfo;
import org.mini.gui.GForm;
import org.mini.gui.GListItem;
import org.mini.gui.GToolkit;

/**
 * @author Gust
 */
public class GSessionItem extends GListItem {

    public SessionInfo groupInfo;
    int msgNewCount;
    BbChatUI app;

    final static float NOTIFY_R = 12.f;

    public GSessionItem(GForm form, SessionInfo gi, BbChatUI app) {
        super(form, null, gi.toString());
        this.app = app;
        groupInfo = gi;
    }

    @Override
    public boolean paint(long vg) {
        if (groupInfo.isGroup()) {
            setImg(app.getGroupHead());
        } else {
            setImg(app.getHead(groupInfo.getRoleId()));
        }

        super.paint(vg);
        float x = getX();
        float y = getY();
        float w = getW();
        float h = getH();

        if (msgNewCount > 0) {
            GToolkit.drawRedPoint(vg, msgNewCount > 99 ? "..." : Integer.toString(msgNewCount), x + w - 20, y + h * .5f, NOTIFY_R);
        }

        return true;
    }

    public void addNewMsgCount(int count) {
        msgNewCount += count;
    }

    public void clearMsgNewCount() {
        msgNewCount = 0;
    }
}
