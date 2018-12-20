/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.client.extgui;

import com.egls.client.BbApplication;
import com.egls.client.BbMain;
import com.egls.client.chat.bean.SessionInfo;
import org.mini.gui.GListItem;
import static org.mini.gui.GObject.TYPE_UNKNOW;
import org.mini.gui.GToolkit;

/**
 *
 * @author Gust
 */
public class GSessionItem extends GListItem {

    public SessionInfo groupInfo;
    int msgNewCount;
    BbMain app;

    final static float NOTIFY_R = 12.f;

    public GSessionItem(SessionInfo gi, BbMain app) {
        super(null, gi.toString());
        this.app = app;
        groupInfo = gi;
    }

    @Override
    public int getType() {
        return TYPE_UNKNOW;
    }

    public boolean update(long vg) {
        if (groupInfo.isGroup()) {
            setImg(app.getGroupHead());
        } else {
            setImg(app.getHead(groupInfo.getRoleId()));
        }

        super.update(vg);
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
