/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.client.chat;

import com.egls.client.game.Const;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import org.mini.gui.GToolkit;

/**
 *
 * @author Gust
 */
public class ImageDatabase {

    String saveRoot;
    String resRoot;

    final String defaultHeadName = "head.png";
    final String defaultGroupName = "group.png";
    final String bbidQrName = "mybbidqr.png";

    public ImageDatabase(String saveRoot, String resRoot) {
        try {
            this.saveRoot = saveRoot;
            this.resRoot = resRoot;
            File f = new File(saveRoot + Const.SAVE_MSG_PATH);
            if (!f.exists()) {
                f.mkdirs();
            }
        } catch (Exception e) {
        }
    }

    public byte[] getDefaultHead() {
        return GToolkit.readFileFromJar(resRoot + Const.RES_IMG_PATH + defaultHeadName);
    }

    public byte[] getGroupHead() {
        return GToolkit.readFileFromJar(resRoot + Const.RES_IMG_PATH + defaultGroupName);
    }

    public byte[] getMyBbidQr() {
        return loadImage(saveRoot + Const.SAVE_MSG_PATH + bbidQrName);
    }

    public byte[] getHeader(long friendid) {
        return loadImage(saveRoot + Const.SAVE_MSG_PATH + friendid + ".jpg");
    }

    public void setMyBbidQr(byte[] imgdata) {
        if (imgdata == null) {
            return;
        }
        saveImage(saveRoot + Const.SAVE_MSG_PATH + bbidQrName, imgdata);
    }

    public void putHeader(long friendid, byte[] imgdata) {
        if (imgdata == null) {
            return;
        }
        saveImage(saveRoot + Const.SAVE_MSG_PATH + friendid + ".jpg", imgdata);
    }

    void saveImage(String path, byte[] imgdata) {
        try {
            File f = new File(path);
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(imgdata);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    byte[] loadImage(String path) {
        try {
            byte[] data;
            File f = new File(path);
            if (f.exists()) {
                data = new byte[(int) f.length()];
                FileInputStream fis = new FileInputStream(f);
                fis.read(data);
                fis.close();
                return data;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    void clearAll() {
        try {
            String folderName = saveRoot + Const.SAVE_MSG_PATH;
            File f = new File(folderName);
            deleteTree(f);

            f.mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void deleteTree(File f) {
        if (f.isDirectory()) {
            File[] files = f.listFiles();
            for (File sf : files) {
                System.out.println("file:" + sf.getAbsolutePath());
                deleteTree(sf);
            }
        }
        boolean s = f.delete();
        System.out.println("delete " + f.getAbsolutePath() + " state:" + s);
    }
}
