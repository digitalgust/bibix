package com.egls.client.extgui;

import org.mini.gui.GLabel;
import org.mini.gui.GList;
import org.mini.gui.GObject;
import org.mini.gui.event.GActionListener;
import org.mini.layout.XContainer;
import org.mini.layout.XList;

public class XSessionList extends XList {
    static public final String XML_NAME = "com.egls.client.extgui.XSessionList";


    GList sessionList;

    public XSessionList() {
        super(null);
    }

    public XSessionList(XContainer xc) {
        super(xc);
    }

    @Override
    protected String getXmlTag() {
        return XML_NAME;
    }

    @Override
    protected <T extends GObject> T createGuiImpl() {
        return (T) new GSessionList(getAssist().getForm(), x, y, width, height);
    }

    @Override
    public GObject getGui() {
        return sessionList;
    }

    @Override
    protected void createAndSetGui() {
        if (sessionList == null) {
            sessionList = createGuiImpl();
            sessionList.setAttachment(this);
            sessionList.setName(name);
            sessionList.setShowMode(multiLine ? GList.MODE_MULTI_SHOW : GList.MODE_SINGLE_SHOW);
            sessionList.setLocation(x, y);
            sessionList.setSize(width, height);
        } else {
            sessionList.setLocation(x, y);
            sessionList.setSize(width, height);
        }
    }


}
