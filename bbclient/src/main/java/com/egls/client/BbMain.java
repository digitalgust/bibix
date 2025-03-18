/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.client;

import com.egls.client.audio.AudioLoader;
import org.mini.apploader.GApplication;
import org.mini.gui.*;
import org.mini.gui.callback.GCallBack;
import org.mini.gui.callback.GCmd;
import org.mini.gui.event.GSizeChangeListener;
import org.mini.layout.XContainer;
import org.mini.layout.XEventHandler;
import org.mini.layout.loader.UITemplate;
import org.mini.layout.loader.XmlExtAssist;
import org.mini.layout.loader.XuiAppHolder;

/**
 * @author gust
 */
public class BbMain extends GApplication implements XuiAppHolder {

    static {
        AudioLoader.loadAll();
    }

    static BbMain app;
    GForm form;
    BbClient client;
    BbChatUI chatUI;
    int devW, devH;
    XmlExtAssist assist;


    @Override
    public void onInit() {

        app = this;
        BbStrings.loadString(this);
        form = new GForm(this);
        assist = new XmlExtAssist(this);
        devW = GCallBack.getInstance().getDeviceWidth();
        devH = GCallBack.getInstance().getDeviceHeight();
        showLoginFrame();
    }

    void showLoginFrame() {
        closeLoginFrame();
        closeChatUI();
        GFrame frame = (GFrame) form.findByName("FRAME_LOGIN");
        if (frame != null) {
            frame.close();
        }

        String username = "";
        String password = "";
        String s = BbClient.load();
        if (s != null && s.indexOf('\n') > 0) {
            String[] t = s.split("\n");
            username = t[0];
            if (t.length > 1) {
                password = t[1];
            }
            if (t.length > 2) {
                int lang;
                try {
                    lang = Integer.parseInt(t[2]);
                    GLanguage.setCurLang(lang);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


        String xmlStr = GToolkit.readFileFromJarAsString("/res/ui/Login.xml", "utf-8");

        UITemplate uit = new UITemplate(xmlStr);
        for (String key : uit.getVariable()) {
            uit.setVar(key, BbStrings.getString(key));
        }
        uit.setVar("LOAD_USERNAME", username);
        uit.setVar("LOAD_PASSWORD", password);


        XContainer xc = (XContainer) XContainer.parseXml(uit.parse(), assist);
        LoginEventHandler eventHandler = new LoginEventHandler();
        xc.build(devW, (devH), eventHandler);

        frame = (GFrame) xc.getGui();

        GList lang = (GList) frame.findByName("LIST_LANG");
        lang.setSelectedIndex(GLanguage.getCurLang());

        form.setSizeChangeListener(new GSizeChangeListener() {
            @Override
            public void onSizeChange(int width, int height) {
                xc.reSize(width, height);
                ((GFrame) xc.getGui()).align(GGraphics.VCENTER | GGraphics.HCENTER);
            }
        });
        eventHandler.setContainer(frame);

        GToolkit.showFrame(frame);
    }

    @Override
    public GApplication getApp() {
        return this;
    }

    @Override
    public GContainer getWebView() {
        return null;
    }

    class LoginEventHandler extends XEventHandler {
        GContainer container;
        GTextObject mail;
        GTextObject pwd;
        GLabel lb_state;
        GCheckBox cb_rember;

        public void setContainer(GContainer container) {
            this.container = container;
            mail = (GTextObject) container.findByName("INPUT_EMAIL");
            pwd = (GTextObject) container.findByName("INPUT_PWD");
            lb_state = (GLabel) container.findByName("LAB_STATE");
            cb_rember = (GCheckBox) container.findByName("CHECK_REMBER");
        }

        @Override
        public void action(GObject gobj) {
            String name = gobj.getName();

            if ("BT_LOGIN".equals(name)) {
                AudioLoader.play(AudioLoader.BIBI);
                String passport = mail.getText();
                String password1 = pwd.getText();
                uilogin(passport, password1, GLanguage.getCurLang());
            } else if ("BT_EXIT".equals(name)) {
                app = null;
                closeApp();
            } else if ("CHECK_REMBER".equals(name)) {

            } else if ("LI_ENG".equals(name)) {
                GLanguage.setCurLang(GLanguage.ID_ENG);
                BbClient.save(mail.getText(), pwd.getText(), GLanguage.ID_ENG);
                showLoginFrame();
            } else if ("LI_CHS".equals(name)) {
                GLanguage.setCurLang(GLanguage.ID_CHN);
                BbClient.save(mail.getText(), pwd.getText(), GLanguage.ID_CHN);
                showLoginFrame();
            } else if ("LI_CHT".equals(name)) {
                GLanguage.setCurLang(GLanguage.ID_CHT);
                BbClient.save(mail.getText(), pwd.getText(), GLanguage.ID_CHT);
                showLoginFrame();
            }
        }


        public void onStateChange(GObject gobj, String cmd) {
            String name = gobj.getName();
            if ("FRAME_LOGIN".equals(name)) {

            }
        }


        int note = 0;
        final GCmd cmd = new GCmd(new Runnable() {
            @Override
            public void run() {

                int state = client.getState();
                if (state == BbClient.STATE_LOGING) {
                    switch (note % 3) {
                        case 0:
                            lb_state.setText(".");
                            break;
                        case 1:
                            lb_state.setText("..");
                            break;
                        case 2:
                            lb_state.setText("...");
                            break;
                    }
                    note++;
                    GForm.addCmd(cmd);
                } else if (state == BbClient.STATE_GAMERUN) {
                    //System.out.println("login success");
                } else if (state == BbClient.STATE_NONE) {
                    String msg = client.getLoginMessage();
                    lb_state.setText(msg);
                    System.out.println("login fail");
                }
                GForm.flush();
            }
        });

        public void uilogin(String passport, String password, int lang) {
            BbClient.save(passport, password, lang);
            client = new BbClient();
            client.setState(BbClient.STATE_LOGIN);
            //
            GForm.addCmd(cmd);

        }
    }

    public void closeLoginFrame() {
        GFrame frame = (GFrame) form.findByName("FRAME_LOGIN");
        if (frame != null) {
            frame.close();
        }
    }

    public void closeChatUI() {
        if (chatUI != null) {
            chatUI.close();
            chatUI = null;
        }
    }

    public void showChatUI() {
        closeLoginFrame();
        closeChatUI();
        chatUI = new BbChatUI(form, client);
        GContainer gcontainer = chatUI.getUI();
        form.add(gcontainer);
    }

    public BbChatUI getChatUI() {
        return chatUI;
    }

    @Override
    public void onClose() {
        if (client != null) {
            client.close();
        }
        client = null;
    }

    static public BbMain getInstance() {
        return app;
    }
}
