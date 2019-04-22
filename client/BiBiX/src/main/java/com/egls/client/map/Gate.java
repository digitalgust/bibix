/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.client.map;

import org.mini.gui.GGraphics;


/**
 *
 * @author gust
 */
public class Gate {

    public String toName;
    public int col;
    public int row;
    public int cols;
    public int rows;
    public GameMap map;

    public Gate(GameMap map) {
        this.map = map;
    }

    public void draw(GGraphics g) {
        g.setColor(0xffffff);
        for (int i = row; i < row + rows; i++) {
            for (int j = col; j < col + cols; j++) {
                g.fillRect(map.getX(j) + 1, map.getY(i) + 1, map.getCellW() - 2, map.getCellH() - 2);
            }
        }
        g.drawString(toName, map.getX(col) + 1, map.getY(row) - 3, GGraphics.BOTTOM | GGraphics.HCENTER);
    }

    public boolean isInGate(int px, int py) {
        int x1 = map.getX(col);
        int y1 = map.getY(row);
        int x2 = map.getX(col + cols);
        int y2 = map.getY(row + rows);
        if (px >= x1 && px <= x2 && py >= y1 && py <= y2) {
            return true;
        }
        return false;
    }
}
