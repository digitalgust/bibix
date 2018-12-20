/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.client;

import com.egls.client.audio.AudioMgr;
import java.util.TimerTask;
import org.mini.gui.*;
import org.mini.gui.event.*;

/**
 *
 * @author gust
 */
public class BbLogin {

    GForm form;
    GLabel lb_state;
    static GImage logoImg;

    public BbLogin() {
    }

    public GForm getLoginForm() {
        form = new GForm();
        long vg = form.getNvContext();

        if (logoImg == null) {
            logoImg = GImage.createImageFromJar("/res/img/logo128.png");
        }
        GImageItem logoItem = new GImageItem(logoImg);
        logoItem.setLocation((form.getDeviceWidth() - logoImg.getWidth()), 10);
        logoItem.setSize(logoImg.getWidth(), logoImg.getHeight());
        logoItem.setDrawBoader(false);
        form.add(logoItem);

        GFrame gframe = new GFrame(BbStrings.getString("BiBi"), 50, 50, 300, 240);
        init(gframe.getView(), vg);
        gframe.setClosable(false);
        form.add(gframe);
        form.setFps(30f);
        gframe.align(GGraphics.HCENTER | GGraphics.VCENTER);

        return form;
    }

    public void init(GViewPort parent, final long vg) {
        
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
                lang = Integer.parseInt(t[2]);
                GLanguage.setCurLang(lang);
            }
        }

        int x = 8, y = 10;
        int width = 280;

        GLabel lb1 = new GLabel(BbStrings.getString("Email (auto sign up if not exists)"), x, y, width, 20);
        parent.add(lb1);
        y += 25;
        GTextField mail = new GTextField(username, BbStrings.getString("Email"), x, y, width, 28);
        parent.add(mail);
        y += 35;
        GTextField pwd = new GTextField(password, BbStrings.getString("Password"), x, y, width, 28);
        pwd.setPasswordMode(true);
        parent.add(pwd);
        y += 35;

        GCheckBox cbox = new GCheckBox(BbStrings.getString("Remember me"), true, x, y, 140, 28);
        parent.add(cbox);
        GButton sig = new GButton(BbStrings.getString("Sign in/up"), x + 138, y, 140, 28);
        sig.setBgColor(0, 96, 128, 255);
        sig.setIcon(GObject.ICON_LOGIN);
        parent.add(sig);
        sig.setActionListener((GObject gobj) -> {
            AudioMgr.play(AudioMgr.BIBI);
            String passport = mail.getText();
            String password1 = pwd.getText();
            uilogin(passport, password1, GLanguage.getCurLang());
        });
        y += 35;

        GList langList = new GList(x, y, width, 35);
        //langList.setShowMode(GList.MODE_MULTI_SHOW);
        langList.setBgColor(GToolkit.getStyle().getFrameBackground());
        GListItem item;
        item = new GListItem(null, "English");
        item.setActionListener(new GActionListener() {
            @Override
            public void action(GObject gobj) {
                GLanguage.setCurLang(GLanguage.ID_ENG);
                BbClient.save(mail.getText(), pwd.getText(), GLanguage.ID_ENG);
                BbApplication.showLoginForm();
            }
        });
        langList.addItem(item);
        item = new GListItem(null, "简体中文");
        item.setActionListener(new GActionListener() {
            @Override
            public void action(GObject gobj) {
                GLanguage.setCurLang(GLanguage.ID_CHN);
                BbClient.save(mail.getText(), pwd.getText(), GLanguage.ID_CHN);
                BbApplication.showLoginForm();
            }
        });
        langList.addItem(item);
        item = new GListItem(null, "繁體中文");
        item.setActionListener(new GActionListener() {
            @Override
            public void action(GObject gobj) {
                GLanguage.setCurLang(GLanguage.ID_CHT);
                BbClient.save(mail.getText(), pwd.getText(), GLanguage.ID_CHT);
                BbApplication.showLoginForm();
            }
        });
        langList.addItem(item);

        int idx = 0;
        if (GLanguage.getCurLang() == GLanguage.ID_ENG) {
            idx = 0;
        }
        if (GLanguage.getCurLang() == GLanguage.ID_CHN) {
            idx = 1;
        }
        if (GLanguage.getCurLang() == GLanguage.ID_CHT) {
            idx = 2;
        }
        langList.setLocation(x, y);
        langList.setSize(width, langList.getH());
        parent.add(langList);
        langList.setSelectedIndex(idx);
        y += langList.getH() + 5;

        lb_state = new GLabel("", x, y, 280, 20);
        parent.add(lb_state);
    }

    int note = 0;

    public void uilogin(String passport, String password, int lang) {
        BbClient.save(passport, password, lang);
        BbApplication.getInstance().client = new BbClient();
        BbApplication.getInstance().client.setState(BbClient.STATE_LOGIN);
        //
        form.getTimer().schedule(new TimerTask() {
            @Override
            public void run() {
                int state = BbApplication.getInstance().client.getState();
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
                } else if (state == BbClient.STATE_GAMERUN) {
                    this.cancel();
                    //System.out.println("login success");
                } else if (state == BbClient.STATE_NONE) {
                    String msg = BbApplication.getInstance().client.getLoginMessage();
                    lb_state.setText(msg);
                    System.out.println("login fail");
                    this.cancel();
                }

                GForm.flush();
            }
        }, 0, 500);

    }

}
