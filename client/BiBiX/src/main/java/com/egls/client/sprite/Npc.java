/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.client.sprite;

import org.mini.gui.GGraphics;


/**
 *
 * @author gust
 */
public class Npc extends Sprite {

    @Override
    public void draw(GGraphics g) {
        if (getRelation() == RELATION_ENEMY) {//敌人
            g.setColor(0xff0000);
        } else {//朋友
            g.setColor(0x00ff00);
            g.drawString(getName(), getX(), getY() - 5, GGraphics.BOTTOM | GGraphics.HCENTER);
        }
        g.drawArc(getX() - 3, getY() - 3, 6, 6, 0, 360);
    }

    @Override
    public void tick() {
        super.tick();
    }

}
