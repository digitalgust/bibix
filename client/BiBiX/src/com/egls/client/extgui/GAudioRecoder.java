/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.client.extgui;

import com.egls.client.BbStrings;
import com.egls.client.audio.AudioLoader;
import static com.egls.client.extgui.GAudioRecoder.SCROLLBAR_TIME_NAME;
import org.mini.gui.GButton;
import org.mini.gui.GForm;
import org.mini.gui.GFrame;
import org.mini.gui.GImage;
import org.mini.gui.GImageItem;
import org.mini.gui.GLabel;
import org.mini.gui.GObject;
import org.mini.gui.GScrollBar;
import org.mini.gui.event.GActionListener;
import org.mini.gui.event.GFocusChangeListener;
import org.mini.media.AudioDecoder;
import org.mini.nanovg.Nanovg;
import org.mini.zip.Zip;
import org.mini.gui.GViewPort;
import org.mini.media.AudioCallback;
import org.mini.media.AudioMgr;

/**
 *
 * @author Gust
 */
public class GAudioRecoder extends GFrame {

    static final String BTN_LEFT_NAME = "btnLeft";
    static final String BTN_RIGHT_NAME = "btnRight";
    static final String SCROLLBAR_TIME_NAME = "scrTime";

    boolean playOnly = false;
    byte[] data;
    AudioDecoder decoder;
    int audioTime = 0;

    public GAudioRecoder(GForm form, boolean playOnly) {
        super("", 0, 0, 320, 200);
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
                AudioMgr.captureStop();
                AudioMgr.playStop();
                GAudioRecoder.this.close();
            }
        });
        GViewPort view = getView();

        GImage img = GImage.createImageFromJar("/res/img/mic.png");

        float gap = 20;
        float imgw = 50;
        float imgh = imgw;
        GImageItem imgItem = new GImageItem(img);
        imgItem.setSize(imgw, imgh);
        imgItem.setLocation(gap, y);
        imgItem.setDrawBoader(false);
        view.add(imgItem);

        GScrollBar scrobar = new GScrollBar();
        scrobar.setLocation(pad + imgw + gap * 2, y);
        scrobar.setSize(view.getW() - imgw - gap * 3, imgh);
        scrobar.setName(SCROLLBAR_TIME_NAME);
        view.add(scrobar);
        y += imgh + pad;

        GLabel lab = new GLabel() {
            @Override
            public boolean update(long vg) {
                super.update(vg);
                setText(Integer.toString((int) (scrobar.getPos() * audioTime)));
                return true;
            }
        };
        lab.setLocation(gap, y);
        lab.setSize(imgw, btnH);
        lab.setAlign(Nanovg.NVG_ALIGN_CENTER | Nanovg.NVG_ALIGN_MIDDLE);
        view.add(lab);

        if (!playOnly) {
            GButton capRerecord = new GButton(BbStrings.getString("Record"), imgw + gap * 2, y, btnW, btnH) {
                @Override
                public boolean update(long vg) {
                    super.update(vg);
                    if (scrobar.getPos() >= 1.f) {
                        AudioMgr.captureStop();
                        data = AudioMgr.getCaptureData();
                    }
                    if (AudioMgr.isCapturing()) {
                        setText(BbStrings.getString("Stop"));
                    } else {
                        setText(BbStrings.getString("Record"));
                    }
                    return true;
                }
            };
            view.add(capRerecord);

            capRerecord.setActionListener((GObject gobj) -> {
                if (AudioMgr.isCapturing()) {
                    stopCapture();
                } else {
                    startCapture(audioTime);
                }

            });
        }
        GButton capPlay = new GButton(BbStrings.getString("Playback"), view.getW() - gap - btnW, y, btnW, btnH);
        view.add(capPlay);
        capPlay.setActionListener((GObject gobj) -> {
            startPlay();
        });
        y += btnH + pad * 5;

        GButton btnLeft = new GButton(BbStrings.getString("Cancel"), gap, y, btnW, btnH);
        btnLeft.setName(BTN_LEFT_NAME);
        view.add(btnLeft);
        btnLeft.setActionListener((GObject gobj) -> {
            AudioMgr.captureStop();
            AudioMgr.playStop();
            GAudioRecoder.this.close();
        });

        GButton btnRight = new GButton(BbStrings.getString("Ok"), view.getW() - gap - btnW, y, btnW, btnH);
        btnRight.setName(BTN_RIGHT_NAME);
        view.add(btnRight);
        btnRight.setActionListener((GObject gobj) -> {
            AudioMgr.captureStop();
            AudioMgr.playStop();
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

    public void setAudioDecoder(AudioDecoder decoder) {
        this.decoder = decoder;
    }

    public byte[] getCaptureData() {
        return AudioMgr.getCaptureData();
    }

    public byte[] getCaptureZipData() {
        return AudioMgr.getCaptureZipData();
    }

    public void startCapture(int maxSecond) {
        audioTime = maxSecond;
        AudioMgr.setCallback(getCallback());
        AudioMgr.captureStart();
    }

    public void stopCapture() {
        AudioMgr.captureStop();
    }

    public void startPlay() {
        AudioMgr.setCallback(getCallback());
        if (data != null) {
            audioTime = (int) AudioMgr.getDataTime(data);
            if (audioTime == 0) {
                audioTime = 1;
            }
            AudioMgr.playData(data);
        } else if (decoder != null) {
            AudioMgr.playDecoder(decoder);
        } else {
            AudioMgr.playCapAudio();
        }
    }

    public void stopPlay() {
        AudioMgr.playStop();
    }

    AudioCallback getCallback() {
        return new AudioCallback() {
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
