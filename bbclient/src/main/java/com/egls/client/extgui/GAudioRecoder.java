/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.client.extgui;

import com.egls.client.BbStrings;
import org.mini.gui.*;
import org.mini.gui.event.GActionListener;
import org.mini.gui.event.GFocusChangeListener;
import org.mini.media.MaDecoder;
import org.mini.media.audio.AudioListener;
import org.mini.media.audio.AudioManager;
import org.mini.nanovg.Nanovg;
import org.mini.zip.Zip;

/**
 * @author Gust
 */
public class GAudioRecoder extends GFrame {

    static final String BTN_LEFT_NAME = "btnLeft";
    static final String BTN_RIGHT_NAME = "btnRight";
    static final String SCROLLBAR_TIME_NAME = "scrTime";

    boolean playOnly = false;
    byte[] data;
    MaDecoder decoder;
    int audioTime = 0;

    public GAudioRecoder(GForm form, boolean playOnly) {
        super(form, "", 0, 0, 320, 200);
        this.playOnly = playOnly;
        setFront(true);
        setTitle(BbStrings.getString("Audio Recodr"));
        float pad = 5, btnW = 80, btnH = 35;
        float y = pad;
        setFocusListener(new GFocusChangeListener() {
            @Override
            public void focusGot(GObject go) {
            }

            @Override
            public void focusLost(GObject go) {
                AudioManager.captureStop();
                AudioManager.playStop();
                GAudioRecoder.this.close();
            }
        });
        GViewPort view = getView();

        GImage img = GImage.createImageFromJar("/res/img/mic.png");

        float gap = 20;
        float imgw = 50;
        float imgh = imgw;
        GImageItem imgItem = new GImageItem(form, img);
        imgItem.setSize(imgw, imgh);
        imgItem.setLocation(gap, y);
        imgItem.setDrawBorder(false);
        view.add(imgItem);

        GScrollBar scrobar = new GScrollBar(form);
        scrobar.setLocation(pad + imgw + gap * 2, y);
        scrobar.setSize(view.getW() - imgw - gap * 3, imgh);
        scrobar.setName(SCROLLBAR_TIME_NAME);
        view.add(scrobar);
        y += imgh + pad;

        GLabel lab = new GLabel(form) {
            @Override
            public boolean paint(long vg) {
                super.paint(vg);
                setText(Integer.toString((int) (scrobar.getPos() * audioTime)));
                return true;
            }
        };
        lab.setLocation(gap, y);
        lab.setSize(imgw, btnH);
        lab.setAlign(Nanovg.NVG_ALIGN_CENTER | Nanovg.NVG_ALIGN_MIDDLE);
        view.add(lab);

        if (!playOnly) {
            GButton capRerecord = new GButton(form, BbStrings.getString("Record"), imgw + gap * 2, y, btnW, btnH) {
                @Override
                public boolean paint(long vg) {
                    super.paint(vg);
                    if (scrobar.getPos() >= 1.f) {
                        AudioManager.captureStop();
                        data = AudioManager.getCaptureData();
                    }
                    if (AudioManager.isCapturing()) {
                        setText(BbStrings.getString("Stop"));
                    } else {
                        setText(BbStrings.getString("Record"));
                    }
                    return true;
                }
            };
            view.add(capRerecord);

            capRerecord.setActionListener((GObject gobj) -> {
                if (AudioManager.isCapturing()) {
                    stopCapture();
                } else {
                    startCapture(audioTime);
                }

            });
        }
        GButton capPlay = new GButton(form, BbStrings.getString("Playback"), view.getW() - gap - btnW, y, btnW, btnH);
        view.add(capPlay);
        capPlay.setActionListener((GObject gobj) -> {
            startPlay();
        });
        y += btnH + pad * 5;

        GButton btnLeft = new GButton(form, BbStrings.getString("Cancel"), gap, y, btnW, btnH);
        btnLeft.setName(BTN_LEFT_NAME);
        view.add(btnLeft);
        btnLeft.setActionListener((GObject gobj) -> {
            AudioManager.captureStop();
            AudioManager.playStop();
            GAudioRecoder.this.close();
        });

        GButton btnRight = new GButton(form, BbStrings.getString("Ok"), view.getW() - gap - btnW, y, btnW, btnH);
        btnRight.setName(BTN_RIGHT_NAME);
        view.add(btnRight);
        btnRight.setActionListener((GObject gobj) -> {
            AudioManager.captureStop();
            AudioManager.playStop();
            GAudioRecoder.this.close();
        });

        y += btnH + pad;
    }

    public void setLeftButton(String text, GActionListener listener) {
        GButton btn = (GButton) findByName(BTN_LEFT_NAME);
        btn.setText(text);
        btn.setActionListener(listener);
    }

    public void setRightButton(String text, GActionListener listener) {
        GButton btn = (GButton) findByName(BTN_RIGHT_NAME);
        btn.setText(text);
        btn.setActionListener(listener);
    }

    public void setAudioZipData(byte[] data) {
        this.data = Zip.extract0(data);
    }

    public void setMaDecoder(MaDecoder decoder) {
        this.decoder = decoder;
    }

    public byte[] getCaptureData() {
        return AudioManager.getCaptureData();
    }

    public byte[] getCaptureZipData() {
        return AudioManager.getCaptureZipData();
    }

    public void startCapture(int maxSecond) {
        audioTime = maxSecond;
        AudioManager.setAudioListener(getCallback());
        AudioManager.captureStart();
    }

    public void stopCapture() {
        AudioManager.captureStop();
    }

    public void startPlay() {
        AudioManager.setAudioListener(getCallback());
        if (data != null) {
            audioTime = (int) AudioManager.getDataTime(data);
            if (audioTime == 0) {
                audioTime = 1;
            }
            AudioManager.playData(data);
        } else if (decoder != null) {
            AudioManager.playDecoder(decoder);
        } else {
            AudioManager.playCapAudio();
        }
    }

    public void stopPlay() {
        AudioManager.playStop();
    }

    AudioListener getCallback() {
        return new AudioListener() {
            @Override
            public void onCapture(int millSecond, byte[] data) {
                float pos = (float) millSecond / 60000;
                GScrollBar scrobar = (GScrollBar) findByName(SCROLLBAR_TIME_NAME);
                scrobar.setPos(pos);
                GForm.flush();
            }

            @Override
            public void onStop() {
            }

            @Override
            public void onPlayback(int millSecond, byte[] data) {
                float pos = (float) millSecond / 60000;
                GScrollBar scrobar = (GScrollBar) findByName(SCROLLBAR_TIME_NAME);
                scrobar.setPos(pos);
                GForm.flush();
            }
        };
    }
}
