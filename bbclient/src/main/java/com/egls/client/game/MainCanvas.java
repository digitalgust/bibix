package com.egls.client.game;

import com.egls.client.BbClient;
import com.egls.client.util.Util;
import org.mini.glfm.Glfm;
import org.mini.glfw.Glfw;
import org.mini.gui.GCanvas;
import org.mini.gui.GForm;
import org.mini.gui.GGraphics;

import java.awt.*;

/**
 * 游戏画面
 */
public final class MainCanvas
        extends GCanvas {

    long timePaint; //paint()的一个周期所用的时间

    int mouseX, mouseY;

    //______________延时用到的变量及常量__________________________
    private long[] curTime = new long[5]; //在isDelayed()方法中，可以实现同时对10种不同类型的等待
    static private final byte //此常量用于区分不同的等待
            DELAY_TYPE_SPLASH = 0, DELAY_TYPE_MESSAGE = 1;
    String message;

    //______________splash用到的变量及常量________________________
    private Image splashImage = null;
    private byte splashIndex = 0;

    //_______________主菜单用到的变量及常量__________________________
    private Image imgMain = null;

    //_______________用户自定义的变量及常量_________________________
    BbClient bbClient;

//--------------------------------------------------------------------------------------
//                                           方法
//--------------------------------------------------------------------------------------

    /**
     * 构造方法
     */
    public MainCanvas(GForm form, BbClient bb, int x, int y, int w, int h) {
        super(form, x, y, w, h);
        bbClient = bb;
    }

    /**
     * 绘制方法
     *
     * @param g Graphics 系统调度线程调度时传入的图像句柄
     */
    @Override
    public void paint(GGraphics g) { //由此触发Canvas的重绘。
        try {
            final long startTime = System.currentTimeMillis();
            int screenW = (int) this.getW();
            int screenH = (int) this.getH();

            g.setColor(0x000000);
            g.fillRect(0, 0, screenW, screenH); //清屏

            switch (bbClient.getState()) {
                case BbClient.STATE_GAMERUN:
                    drawGameRun(g);
                    break;
                case BbClient.STATE_LOGIN:
                    break;
                case BbClient.STATE_LOGING:
                    break;
                case BbClient.STATE_EXIT:
                    break;
                default:
                    g.setColor(0xffffff);
                    g.drawString(Util.getStr(Util.STR_WAITING), screenW / 2, screenH / 2,
                            GGraphics.TOP | GGraphics.HCENTER);
                    break;
            }

            timePaint = System.currentTimeMillis() - startTime;
//            if (timePaint < MILLIS_PER_TICK) {
//                synchronized (this) {
//                    wait(20); //使每个周期大致相等
//                }
//            }
            //debug
            int drawY = screenH;
            g.setColor(0xffffff);
            g.drawString("paint :" + timePaint, 0, drawY, GGraphics.BOTTOM | GGraphics.LEFT);
            //g.drawString("run :" + timeRun, 70, drawY, GGraphics.BOTTOM | GGraphics.LEFT);
            g.drawString(mouseX + "," + mouseY, 120, drawY, GGraphics.BOTTOM | GGraphics.LEFT);

            //显示消息
            if (isDelayed(3000, DELAY_TYPE_MESSAGE)) {
                message = null;
            }
            if (message != null) {
                g.setColor(0x00ff00);
                g.drawString(message, 250, drawY, GGraphics.BOTTOM | GGraphics.LEFT);
            }
            //end debug

            GForm.flush();
        } catch (Exception e) {
        } finally {
        }
    }

    @Override
    public boolean dragEvent(int button, float dx, float dy, float x, float y) {
        return false;
    }

    //;----------------------------------------------------------------------------
    //;                                   需要实现的方法
    //;----------------------------------------------------------------------------

    /**
     * 新开始一个游戏
     */
    private void newGame() {
        //做新游戏的开始操作

    }

    /**
     * 处理游戏状态下的画屏及实现游戏逻辑
     *
     * @param g Graphics
     */
    private void drawGameRun(GGraphics g) {

        bbClient.getGameRun().draw(g);

        //画结束
    }

    //;----------------------------------------------------------------------
    //;                                  延时系统
    //;----------------------------------------------------------------------

    /**
     * 实现延时
     *
     * @param waitFor int 等多长时间
     * @param type    byte 等待类型
     * @return boolean 是否超时
     */
    private boolean isDelayed(int waitFor, byte type) {
        if (curTime[type] + waitFor > System.currentTimeMillis()) {
            return false;
        } else {
            curTime[type] = 0; //清0
            return true;
        }
    }

    /**
     * 某个类型的延时开始
     *
     * @param type byte 类型值
     */
    private void beginDelay(byte type) {
        curTime[type] = System.currentTimeMillis();
    }

    public void setMessage(String s) {
        message = s;
        beginDelay(DELAY_TYPE_MESSAGE);
    }

    @Override
    public void touchEvent(int touchid, int phase, int x, int y) {
        if (isInArea(x, y)) {
            if (phase == Glfm.GLFMTouchPhaseBegan) {

            } else if (phase == Glfm.GLFMTouchPhaseEnded) {

                bbClient.getGameRun().pointerReleased(x - (int) getX(), y - (int) getY());
            }
        }
        super.touchEvent(touchid, phase, x, y);
    }

    @Override
    public void mouseButtonEvent(int button, boolean pressed, int x, int y) {
        super.mouseButtonEvent(button, pressed, x, y);
        touchEvent(Glfw.GLFW_MOUSE_BUTTON_1, pressed ? Glfm.GLFMTouchPhaseBegan : Glfm.GLFMTouchPhaseEnded, x, y);
    }
}
