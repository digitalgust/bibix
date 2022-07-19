/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.client.audio;

import com.egls.client.BbMain;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.mini.gui.GToolkit;
import org.mini.media.MaDecoder;
import org.mini.media.MaDevice;
import org.mini.media.MiniAudio;
import org.mini.media.audio.AudioManager;

/**
 * @author Gust
 */
public class AudioLoader {

    static int format = MiniAudio.mal_format_s16;
    static int channels = 2;
    static int ratio = 22050;

    /**
     * ========================================================================================================
     * playback
     * ========================================================================================================
     */
    public static final Integer BIBI = 0;
    public static final Integer DONG = 1;
    public static final Integer HUA = 2;
    public static final Integer MUSIC = 3;

    static Map<Integer, byte[]> audios = new HashMap();
    static MaDevice playDevice;


    public static byte[] readFile(String s) {
        try {
            File f = new File(BbMain.getInstance().getSaveRoot() + s);
            byte[] b = new byte[(int) f.length()];

            FileInputStream dis = new FileInputStream(f);
            dis.read(b);
            dis.close();
            return b;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void loadAll() {
        byte[] b;
        b = GToolkit.readFileFromJar("/res/audio/bibi.flac");
        System.out.println("=========b:" + b);
        audios.put(BIBI, b);
        b = GToolkit.readFileFromJar("/res/audio/bibi.flac");
        audios.put(DONG, b);

    }

    public static void play(Integer code) {

        try {
            byte[] b = audios.get(code);
            MaDecoder decoder = new MaDecoder(b, format, channels, ratio);
            AudioManager.playDecoder(decoder);
        } catch (Exception e) {
        }
    }

}
