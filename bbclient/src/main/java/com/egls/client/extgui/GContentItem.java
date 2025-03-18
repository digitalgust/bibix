/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.client.extgui;

import com.egls.client.BbChatUI;
import com.egls.client.BbStrings;
import com.egls.client.chat.MsgItem;
import org.mini.glfm.Glfm;
import org.mini.glwrap.GLUtil;
import org.mini.gui.*;
import org.mini.gui.callback.GCallBack;
import org.mini.gui.event.GActionListener;
import org.mini.media.audio.AudioManager;
import org.mini.nanovg.Nanovg;

import static org.mini.gui.GToolkit.nvgRGBA;
import static org.mini.nanovg.Nanovg.*;

/**
 * @author Gust
 */
public class GContentItem extends GObject {

    public static float defaultItemW = 200;
    public static float defaultIconW = 40;
    public static float defaultImageW = 90;
    public static float defaultImageH = 120;
    public static float defaultVoiceW = 46;
    public static float defaultVoiceH = 46;
    public static float defaultVideoW = 200;
    public static float defaultVideoH = 135;
    public static float defaultVoiceIconWH = 40;
    public static float pad = 5;

    public static GImage voiceIcon, videoIcon;

    BbChatUI app;
    MsgItem msg;
    GImage img;//byte[] icon;
    String attachmentid;
    public boolean left = true;
    int voiceTimeSec;

    float[] leftColor = nvgRGBA(128, 128, 135, 32);
    float[] rightColor = nvgRGBA(32, 192, 32, 32);
    //
    byte[] text_arr;
    float[] bond;
    float contW, contH;
    GForm form;

    public GContentItem(GForm form, MsgItem mi, BbChatUI app) {
        super(form);
        this.msg = mi;
        this.form = form;
        this.app = app;
        init(GCallBack.getInstance().getDisplay());
    }

    final void init(long vg) {
        switch (msg.getMediaType()) {
            case MsgItem.TYPE_IMAGE:
                contW = defaultImageW;
                contH = defaultImageH;
                break;
            case MsgItem.TYPE_VIDEO:
                contW = defaultVideoW;
                contH = defaultVideoH;
                break;
            case MsgItem.TYPE_VOICE:
                voiceTimeSec = (int) AudioManager.getZipDataTime(msg.thumb);
                contW = defaultVoiceW + voiceTimeSec * 3;
                if (contW > defaultItemW) {
                    contW = defaultItemW;
                }
                contH = defaultVoiceH;
                break;
            default:
                contW = defaultItemW - defaultIconW - pad * 4;
                String s = msg.msg;
                if (s == null) {
                    contH = defaultIconW;
                } else {
                    nvgTextAlign(vg, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
                    text_arr = GLUtil.toCstyleBytes(s);

                    bond = GToolkit.getTextBoundle(vg, s, contW, getFontSize(), true);
                    if (bond[HEIGHT] < GToolkit.getStyle().getTextFontSize() * 2) {
                        contW = bond[WIDTH] + 1;
                    }
                    contH = bond[GObject.HEIGHT] + pad * 2;
                    if (contH < defaultIconW) {
                        contH = defaultIconW;
                    }
                }
                break;
        }
        setSize(contW + pad * 4 + defaultIconW, contH + pad * 2);
    }


    public MsgItem getMsgItem() {
        return msg;
    }

    public GImage getHeader() {
        return app.getHead(msg.fromid);
    }

    @Override
    public void longTouchedEvent(int x, int y) {
        super.longTouchedEvent(x, y);
        touched = false;
        GList menu = GToolkit.getListMenu(form,
                new String[]{
                        BbStrings.getString("Copy"),
                        BbStrings.getString("Forward") + "...",
                        BbStrings.getString("More"),},//
                null, //
                new GActionListener[]{
                        new GActionListener() {
                            @Override
                            public void action(GObject gobj) {
                                if (!msg.isMediaMsg()) {
                                    Glfm.glfmSetClipBoardContent(msg.msg);
                                }
                                form.setCurrent(null);
                            }
                        }, new GActionListener() {
                    @Override
                    public void action(GObject gobj) {
                        app.showForwardFrame(GContentItem.this);
                        form.setCurrent(null);
                    }
                }, new GActionListener() {
                    @Override
                    public void action(GObject gobj) {
                        form.setCurrent(null);
                    }
                },});
        float mx = x;
        float my = y;
        if (mx + menu.getW() > GCallBack.getInstance().getDeviceWidth()) {
            mx = GCallBack.getInstance().getDeviceWidth() - menu.getW();
        }
        if (my + menu.getH() > GCallBack.getInstance().getDeviceHeight()) {
            my = GCallBack.getInstance().getDeviceHeight() - menu.getH();
        }
        menu.setLocation(mx, my);
        form.add(menu);

        //form.setFocus(menu);

    }

    boolean touched = false;

    @Override
    public boolean dragEvent(int button, float dx, float dy, float x, float y) {
        //touched = false;
        return false;
    }

    float mouseX, mouseY;

    @Override
    public void touchEvent(int touchid, int phase, int x, int y) {
        if (isInArea(x, y)) {
            switch (phase) {
                case Glfm.GLFMTouchPhaseEnded:
                    if (Math.abs(x - mouseX) < 40 && Math.abs(y - mouseY) < 40) {
                        if (touched && msg.isMediaMsg()) {
                            switch (msg.getMediaType()) {
                                case MsgItem.TYPE_IMAGE: {
                                    GForm form = getForm();
                                    if (form != null) {
                                        GViewPort go = GToolkit.getImageView(getForm(), img, null);
                                        go.setLocation(0 - form.getInnerX(), go.getY());
                                        form.add(go);
                                        form.setCurrent(go);
                                        //System.out.println("picture shown");
                                    }
                                    break;
                                }
                                case MsgItem.TYPE_VOICE: {
                                    GAudioRecoder recoder = new GAudioRecoder(form, true);
                                    recoder.setAudioZipData(msg.thumb);
                                    form.add(recoder);
                                    form.setCurrent(recoder);
                                    recoder.align(GGraphics.HCENTER | GGraphics.VCENTER);
                                    recoder.startPlay();
                                    break;
                                }
                                case MsgItem.TYPE_VIDEO: {
                                    String path = app.getMsgDatabase().getMediaFilePath(msg.getMediaId());
                                    long handle = Glfm.glfmPlayVideo(GCallBack.getInstance().getDisplay(), path, "video/mp4");
                                    break;
                                }
                            }
                        }
                    }
                    touched = false;
                    break;
                case Glfm.GLFMTouchPhaseBegan:
                    mouseX = x;
                    mouseY = y;
                    touched = true;
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public boolean paint(long vg) {
        float x = getX();
        float y = getY();
        float w = getW();
        float h = getH();

        if (msg.getMediaType() == MsgItem.TYPE_IMAGE && img == null) {
            this.img = GImage.createImage(msg.thumb);
        }

        nvgFontSize(vg, GToolkit.getStyle().getTextFontSize());
        nvgFillColor(vg, GToolkit.getStyle().getTextFontColor());
        nvgFontFace(vg, GToolkit.getFontWord());
        nvgTextAlign(vg, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);

        float iconW = defaultIconW;
        float iconH = defaultIconW;

        float fillW = contW + pad * 2;
        if (left) {
            float dx = x + pad;
            float dy = y + pad;
            GToolkit.drawRect(vg, dx, dy, iconW, iconH, leftColor);
            GToolkit.drawImage(vg, getHeader(), dx, dy, iconW, iconH);
            dx += iconW + pad;

            switch (msg.getMediaType()) {
                case MsgItem.TYPE_IMAGE:
                    GToolkit.drawImage(vg, img, dx + pad, dy + pad, contW, h - pad * 2);
                    break;
                case MsgItem.TYPE_VIDEO:
                    GToolkit.drawImage(vg, getVedioImg(vg), dx + pad, dy + pad, contW, h - pad * 2);
                    break;
                case MsgItem.TYPE_VOICE:
                    drawVoiceBar(vg, dx, dy, contW, h - pad * 2, leftColor);
                    GToolkit.drawImage(vg, getVoiceImg(vg), dx + pad, dy + pad * .5f, defaultVoiceIconWH, defaultVoiceIconWH, false, .5f);
                    break;
                default:
                    GToolkit.drawRoundedRect(vg, dx, dy, fillW, h - pad * 2, 6f, leftColor);
                    GToolkit.drawText(vg, dx + pad, dy + pad, contW, h, msg.msg);
                    break;
            }

        } else {

            float dx = x + w - pad - iconW;
            float dy = y + pad;
            GToolkit.drawRect(vg, dx, dy, iconW, iconH, rightColor);
            GToolkit.drawImage(vg, getHeader(), dx, dy, iconW, iconH);
            dx -= pad + fillW;

            switch (msg.getMediaType()) {
                case MsgItem.TYPE_IMAGE:
                    GToolkit.drawImage(vg, img, dx + pad, dy + pad, contW, h - pad * 2);
                    break;
                case MsgItem.TYPE_VIDEO:
                    GToolkit.drawImage(vg, getVedioImg(vg), dx + pad, dy + pad, contW, h - pad * 2);
                    break;
                case MsgItem.TYPE_VOICE:
                    drawVoiceBar(vg, dx, dy, contW, h - pad * 2, rightColor);
                    GToolkit.drawImage(vg, getVoiceImg(vg), dx + contW - defaultVoiceIconWH - pad, dy + pad * .5f, defaultVoiceIconWH, defaultVoiceIconWH, false, .5f);
                    break;
                default:
                    GToolkit.drawRoundedRect(vg, dx, dy, fillW, h - pad * 2, 6f, rightColor);
                    GToolkit.drawText(vg, dx + pad, dy + pad, contW, h, msg.msg);
                    break;
            }
        }
        return true;
    }

    void drawVoiceBar(long vg, float x, float y, float w, float h, float[] color) {
        byte[] bg;

        float cornerRadius = 6.0f;
        bg = nvgLinearGradient(vg, x, y, x, y + h, Nanovg.nvgRGBAf(color[0], color[1], color[2], .2f), nvgRGBA(0, 0, 0, 32));
        nvgBeginPath(vg);
        nvgRoundedRect(vg, x + 1, y + 1, w - 2, h - 2, cornerRadius - 1);
        //nvgFillColor(vg, bgColor);
        nvgFillPaint(vg, bg);
        nvgFill(vg);

    }

    GImage getVoiceImg(long vg) {
        if (voiceIcon == null) {
            voiceIcon = GImage.createImageFromJar("/res/img/play.png");
        }
        return voiceIcon;
    }

    GImage getVedioImg(long vg) {
        if (videoIcon == null) {
            videoIcon = GImage.createImageFromJar("/res/img/video.png");
        }
        return videoIcon;
    }
}
