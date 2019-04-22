/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.client;

import com.egls.client.audio.AudioLoader;
import org.mini.gui.GApplication;
import org.mini.gui.GForm;

/**
 *
 * This class MUST be app.BbApplication
 *
 * And this jar MUST be resfiles/ExApp.jar
 *
 * it used in c source glfmapp/main.c
 *
 * @author gust
 */
public class BbApplication extends GApplication {

    static {
        AudioLoader.loadAll();
    }

    static BbApplication app;
    GForm curForm;
    BbClient client;

    @Override
    public GForm getForm(GApplication appins) {
        this.app = (BbApplication) appins;
        if (curForm == null) {
            BbLogin login = new BbLogin();
            curForm = login.getLoginForm();
        }
        return curForm;
    }

    @Override
    public void close() {
        if (client != null) {
            client.close();
        }
        client = null;
        curForm = null;
    }

    static public void showLoginForm() {
        BbLogin login = new BbLogin();
        app.curForm = login.getLoginForm();
        app.notifyCurrentFormChanged();
    }

    static public BbMain showMainForm() {
        BbMain main = new BbMain();
        app.curForm = main.getMainForm();
        app.notifyCurrentFormChanged();
        return main;
    }

    static public BbApplication getInstance() {
        return app;
    }
}
