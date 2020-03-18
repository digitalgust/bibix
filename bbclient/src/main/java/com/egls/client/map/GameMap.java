/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.client.map;

import com.egls.client.game.GameRun;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.Vector;
import org.mini.gui.GGraphics;

/**
 *
 * @author gust
 */
public class GameMap {

    GameRun runtime;

    byte[] mapdat;
    private String roomid;
    private String name;

    private int cols = 32;
    private int rows = 32;
    private int cellW = 8;
    private int cellH = 8;

    Vector<Gate> gates = new Vector<Gate>();

    /**
     *
     * @param gr
     * @param roomid
     * @param b
     */
    public GameMap(GameRun gr, String roomid, byte[] b) {
        runtime = gr;
        this.roomid = roomid;
        loadMapData(b);
        
    }

    public void draw(GGraphics g) {
        runtime.getBbClient().getMainCanvas().setSize(cols * cellW, rows * cellH);
        g.setColor(0xffa0a0a0);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(0xff000000);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (!canFromTo(j, i, j, i)) {
                    //g.drawChar('O', getX(j), getY(i), 0);
                    g.fillRect(getX(j) + 1, getY(i) + 1, cellW - 2, cellH - 2);
                }
            }
        }

        for (Gate gate : gates) {
            gate.draw(g);
        }

        g.setColor(0xffffffff);
//        float[] b=GToolkit.getFontBoundle(g.getForm().getWinContex());
//        g.fillRect(0, 0, GToolkit.getStringWidth(name), Font.CHAR_H+1);
        g.setColor(0xff000000);
        g.drawString(name, 0, 0, 0);
    }

    public void tick() {

    }

    public int getWidth() {
        return cols * cellW;
    }

    public int getHeight() {
        return rows * cellH;
    }

    /**
     * @return the cols
     */
    public int getCols() {
        return cols;
    }

    /**
     * @return the rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * @return the cellW
     */
    public int getCellW() {
        return cellW;
    }

    /**
     * @return the cellH
     */
    public int getCellH() {
        return cellH;
    }

    /**
     * @return the roomid
     */
    public String getRoomid() {
        return roomid;
    }

    /**
     * @param roomid the roomid to set
     */
    public void setRoomid(String roomid) {
        this.roomid = roomid;
    }

    public int getCol(int x) {
        return x / cellW;
    }

    public int getRow(int y) {
        return y / cellH;
    }

    public int getX(int col) {
        return col * cellW;
    }

    public int getY(int row) {
        return row * cellH;
    }

    /**
     *
     * @param b
     * @return boolean
     */
    public boolean loadMapData(byte[] b) { //存一维数字数组

        try {

            DataInputStream fis = new DataInputStream(new ByteArrayInputStream(b));
            int len = fis.available();
            byte[] data = new byte[len];
            int pos = 0;
            while (pos < len) {
                pos += fis.read(data);
            }
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            DataInputStream raf = new DataInputStream(bis);

//            if("banzhulin".equals(id)){
//                int debug=1;
//            }
            cols = raf.readByte(); //列
            rows = raf.readByte(); //行
            cellW = raf.readByte();
            cellH = raf.readByte();

            mapdat = new byte[cols * rows];

            for (int i = 0; i < mapdat.length; i++) {
                mapdat[i] = raf.readByte();
            }
            raf.close();
            fis.close();
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public boolean canFromTo(int fromC, int fromR, int toC, int toR) {
        if (fromC < 0 || fromR < 0
                || fromC >= cols || fromR >= rows
                || toC < 0 || toR < 0
                || toC >= cols || toR >= rows) {
            return false;
        }
        if (mapdat[toR * (cols) + toC] == 0) {
            return false;
        } else {
            return true;
        }
    }

    public void addGate(Gate g) {
        gates.add(g);
    }

    /**
     * @param cellW the cellW to set
     */
    public void setCellW(int cellW) {
        this.cellW = cellW;
    }

    /**
     * @param cellH the cellH to set
     */
    public void setCellH(int cellH) {
        this.cellH = cellH;
    }

    /**
     * 取某座标上的门
     *
     * @param px
     * @param py
     * @return
     */
    public Gate getGate(int px, int py) {
        for (Gate g : gates) {
            if (g.isInGate(px, py)) {
                return g;
            }
        }
        return null;
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
}
