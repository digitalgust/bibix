/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.client.sprite;

import com.egls.client.game.Const;
import com.egls.client.game.GameRun;
import com.egls.client.map.Gate;
import com.egls.client.netmgr.CmdPkg;
import org.mini.gui.GGraphics;

/**
 *
 * @author gust
 */
public class MyPlayer extends Player {

    static public final byte NORMAL = 0, WAITING = 1;
    byte state = NORMAL;
    int[][] path;
    int pathIndex = 0;
    long lastSendMove = System.currentTimeMillis();

    public MyPlayer(GameRun gr) {
        super(gr);
    }

    @Override
    public void draw(GGraphics g) {

        g.setColor(0xaffff00);
        int SPACING = 20;
        String s;
        if (getId() == 0) {
            s = "Loading role ... ";
        } else {
            s = "x=" + getX() + ",y=" + getY() + ",c=" + getMap().getCol(getX()) + ",r=" + getMap().getRow(getY());
        }
        g.fillRect(getX() - 3, getY() - 3, 6, 6);
        g.drawString(s, (int) mcvs.getW(), 0, g.TOP | g.RIGHT); //debug
        g.drawString(getName(), getX(), getY() - 5, GGraphics.BOTTOM | GGraphics.HCENTER);

        if (path != null) {
            int[][] tmp = path;//防止多线程赋值
            for (int[] tmp1 : tmp) {
                g.drawLine(getMap().getX(tmp1[0]), getMap().getY(tmp1[1]), getMap().getX(tmp1[0]), getMap().getY(tmp1[1]));
            }
        }

    }

    @Override
    public void tick() {
        if (getMap() == null) {
            return;
        }

        super.tick();
        //
//        if (mcvs.isPressed()) {
//            if (mcvs.isPressed(XCanvas.MASK_LEFT)) {
//                move(-speed, 0);
//            } else if (mcvs.isPressed(XCanvas.MASK_RIGHT)) {
//                move(speed, 0);
//            } else if (mcvs.isPressed(XCanvas.MASK_UP)) {
//                move(0, -speed);
//            } else if (mcvs.isPressed(XCanvas.MASK_DOWN)) {
//                move(0, speed);
//            }
//        }

        if (state == WAITING) {
            moveto(NO_DEF, NO_DEF);
        }
        //处理寻径
        if (path != null) {//有路径
            if (isArrived()) {//已到达目的地
                if (pathIndex < path.length) {
                    moveto(getMap().getX(path[pathIndex][0]), getMap().getY(path[pathIndex][1]));
                    pathIndex++;
                } else {
                    path = null;
                    pathIndex = 0;
                }
            }

        } else {//寻径路过不进门

            Gate gate = getMap().getGate(getX(), getY());
            if (gate != null && state != WAITING) {
                transGate();
                setState(WAITING);
            }
        }
    }

    /**
     * ========================================================= 指令
     * =========================================================
     */
    @Override
    public void processCmd(CmdPkg cmd) {
        int cmdid = cmd.getCommandID();

        switch (cmdid) {
            case Const.S_SYS_SEND_CLIENT_DATA:
                break;
            case Const.S_PLAYER_UPDATE:
                setExp(cmd.readInt());
                setLev(cmd.readInt());
                setId(cmd.readLong());
                setCamp(cmd.readByte());
                setName(cmd.readUTF());
                System.out.println("updated role info");
                break;
            case Const.S_PLAYER_MOVE_SHOW: {
                int len = cmd.readShort();
                path = new int[len][2];
                for (int i = 0; i < len; i++) {
                    path[i][0] = cmd.readShort();
                    path[i][1] = cmd.readShort();
                }
            }
            break;
        }
    }

    /**
     * 移动
     *
     * @param offsetX
     * @param offsetY
     * @return
     */
    @Override
    public boolean move(int offsetX, int offsetY) {
        if (super.move(offsetX, offsetY)) {
            if (System.currentTimeMillis() - lastSendMove > 1000) {
                CmdPkg out = new CmdPkg(Const.C_PLAYER_MOVE);
                out.writeShort(getX());
                out.writeShort(getY());
                runtime.send(out);
                lastSendMove = System.currentTimeMillis();
            }
            return true;
        }
        return false;
    }

    /**
     * 服务器寻径
     *
     * @param roomid
     * @param c
     * @param r
     */
    public void srvFindPath(String roomid, int c, int r) {

        CmdPkg out = new CmdPkg(Const.C_PLAYER_FIND_PATH);
        out.writeUTF(roomid);
        out.writeInt(c);
        out.writeInt(r);
        runtime.send(out);
    }

    public void loadSuccess() {
        CmdPkg out = new CmdPkg(Const.C_MAP_LOADING_SUCCEED);
        runtime.send(out);
    }

    public void changeRoom(String roomid, int tox, int toy) {
        setX(tox);
        setY(toy);

    }

    public void transGate() {
        CmdPkg out = new CmdPkg(Const.C_MAP_GATE);
        out.writeByte(getMap().getCol(getX()));
        out.writeByte(getMap().getRow(getY()));
        runtime.send(out);
    }

    public void setState(byte s) {
        state = s;
    }
}
