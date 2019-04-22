/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.client.sprite;

import com.egls.client.game.GameRun;
import com.egls.client.game.MainCanvas;
import org.mini.gui.GGraphics;

/**
 *
 * @author gust
 */
public class Player extends Sprite {

    MainCanvas mcvs;

    GameRun runtime;



    /**
     *
     * @param gr
     */
    public Player(GameRun gr) {
        runtime = gr;
        mcvs = runtime.getBbClient().getMainCanvas();

    }

    /**
     * ========================================================= 绘制
     * =========================================================
     */
    @Override
    public void draw(GGraphics g) {

        g.setColor(0xffff00);
        int SPACING = 20;
        g.drawArc(getX() - 3, getY() - 3, 6, 6, 0, 360);
        g.drawString(getName(), getX(), getY() - 5, GGraphics.BOTTOM | GGraphics.HCENTER);
    }

    /**
     * ========================================================= 逻辑处理
     * =========================================================
     */
    @Override
    public void tick() {
        super.tick();

    }

}
