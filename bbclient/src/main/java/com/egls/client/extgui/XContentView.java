package com.egls.client.extgui;

import org.mini.gui.GForm;
import org.mini.gui.GObject;
import org.mini.gui.event.GActionListener;
import org.mini.layout.XContainer;
import org.mini.layout.XViewPort;

public class XContentView extends XViewPort {
    static public final String XML_NAME = "com.egls.client.extgui.XContentView";


    GContentView contentView;

    public XContentView() {
        super(null);
    }

    public XContentView(XContainer xc) {
        super(xc);
    }

    @Override
    protected String getXmlTag() {
        return XML_NAME;
    }

    @Override
    protected <T extends GObject> T createGuiImpl() {
        return (T) new GContentView(getAssist().getForm());
    }
    public GObject getGui() {
        return contentView;
    }

    @Override
    protected void createAndSetGui() {
        if (contentView == null) {
            contentView = createGuiImpl();
            contentView.setAttachment(this);
            contentView.setName(name);
            contentView.setLocation(x, y);
            contentView.setSize(width, height);
        } else {
            contentView.setLocation(x, y);
            contentView.setSize(width, height);
        }
    }

}
