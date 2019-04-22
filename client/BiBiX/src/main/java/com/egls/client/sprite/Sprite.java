/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.client.sprite;

import com.egls.client.map.GameMap;
import com.egls.client.util.NetCmdHandler;
import com.egls.client.netmgr.CmdPkg;
import org.mini.gui.GGraphics;

/**
 *
 * @author gust
 */
public abstract class Sprite implements NetCmdHandler {

    public static byte TYPE_PLAYER = 0;// 玩家
    public static byte TYPE_MONSTER = 1; // 怪

    //敌友关系
    public static byte RELATION_FRIEND = 0;
    public static byte RELATION_ENEMY = 1;
    //位置
    private int x;
    //位置
    private int y;
    int activeRange = 3;//可点击范围

    private GameMap map;

    private long id;
    private int lev;
    private int gold;
    private int exp;
    private byte camp;
    private String name;
    private byte relation;
    //移动
    static public final int NO_DEF = -1;
    private int moveToX = NO_DEF, moveToY = NO_DEF;
    int speed = 2;

    /**
     *
     * @param g
     */
    public void draw(GGraphics g) {

    }

    @Override
    public void processCmd(CmdPkg cmd) {

    }

    public void tick() {
        if (moveToX != NO_DEF) {
            autoWalk();
        }
        if (isArrived()) {
            moveToX = moveToY = NO_DEF;
        }

    }

    /**
     * 寻径过程中自己走
     */
    void autoWalk() {
        int dx = moveToX - x;
        int dy = moveToY - y;
        int offsetX = speed;
        int offsetY = speed;
        if (Math.abs(dx) > speed) {
            if (dx < 0) {
                offsetX = -offsetX;
            }
        } else {
            offsetX = dx;
        }
        if (Math.abs(dy) > speed) {
            if (dy < 0) {
                offsetY = -offsetY;
            }
        } else {
            offsetY = dy;
        }
        move(offsetX, offsetY);
    }

    /**
     * 移动
     *
     * @param offsetX
     * @param offsetY
     * @return
     */
    public boolean move(int offsetX, int offsetY) {
        if (map != null) {
            int nx = x + offsetX;
            int ny = y + offsetY;
            if (nx >= 0 && nx < map.getWidth() && ny >= 0 && ny < map.getHeight()) {
                x = nx;
                y = ny;
                return true;
            }
        }
        return false;
    }

    public void moveto(int tox, int toy) {
        moveToX = tox;
        moveToY = toy;
    }

    /**
     * 到达目的地
     *
     * @return
     */
    boolean isArrived() {
        if (map != null && moveToX != NO_DEF) {
            return getMap().getCol(x) == getMap().getCol(moveToX) && getMap().getCol(y) == getMap().getCol(moveToY);
        } else {
            return true;
        }
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    public void setXY(int px, int py) {
        x = px;
        y = py;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the lev
     */
    public int getLev() {
        return lev;
    }

    /**
     * @param lev the lev to set
     */
    public void setLev(int lev) {
        this.lev = lev;
    }

    /**
     * @return the gold
     */
    public int getGold() {
        return gold;
    }

    /**
     * @param gold the gold to set
     */
    public void setGold(int gold) {
        this.gold = gold;
    }

    /**
     * @return the exp
     */
    public int getExp() {
        return exp;
    }

    /**
     * @param exp the exp to set
     */
    public void setExp(int exp) {
        this.exp = exp;
    }

    /**
     * @return the camp
     */
    public byte getCamp() {
        return camp;
    }

    /**
     * @param camp the camp to set
     */
    public void setCamp(byte camp) {
        this.camp = camp;
    }

    /**
     * @return the relation
     */
    public byte getRelation() {
        return relation;
    }

    /**
     * @param relation the relation to set
     */
    public void setRelation(byte relation) {
        this.relation = relation;
    }

    /**
     * @return the map
     */
    public GameMap getMap() {
        return map;
    }

    /**
     * @param map the map to set
     */
    public void setMap(GameMap map) {
        this.map = map;
    }

    /**
     * 某点是否在我的可点击范围内
     * @param px
     * @param py
     * @return 
     */
    public boolean inRange(int px, int py) {
        return Math.abs(px - x) <= activeRange && Math.abs(py - y) <= activeRange;
    }

    /**
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(int y) {
        this.y = y;
    }
}
