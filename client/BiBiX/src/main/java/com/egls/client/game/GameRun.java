/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.client.game;

import com.egls.client.BbClient;
import com.egls.client.map.GameMap;
import com.egls.client.map.Gate;
import com.egls.client.sprite.MyPlayer;
import static com.egls.client.sprite.MyPlayer.NORMAL;
import com.egls.client.sprite.Npc;
import com.egls.client.sprite.Player;
import com.egls.client.sprite.Sprite;
import com.egls.client.util.NetCmdHandler;
import com.egls.client.util.Util;
import com.egls.client.netmgr.CmdPkg;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Hashtable;
import org.mini.gui.GGraphics;

/**
 *
 * @author gust
 */
public class GameRun implements NetCmdHandler {

    private BbClient bbClient;
    MainCanvas mcvs;
    //
    MyPlayer role;
    Hashtable<Long, Sprite> gameobj = new Hashtable();
    //
    GameMap map;
    //

//    String passport = "user" + (Util.genRandom() % 1000);
//    String password = "123456";
    public GameRun(BbClient bb) {
        bbClient = bb;
        mcvs = bbClient.getMainCanvas();
        role = new MyPlayer(this);//表示正在登录过程中,避免重复发送登录指令
        //
    }

    public void draw(GGraphics g) {
        int SPACING = 20;

        //在这里实现对游戏画面的控制和游戏逻辑的实现
        //地图
        if (map != null) {
            map.draw(g);
            //遍历执行
            for (Sprite obj : gameobj.values()) {
                obj.draw(g);
            }
            role.draw(g);
        }
        g.setColor(0xfffffff);
        g.drawString("点击地图空白区域-移动, 点击绿圈-NPC菜单,ESC-返回 ", (int)mcvs.getW() / 2, (int)mcvs.getH() - SPACING, GGraphics.VCENTER | GGraphics.HCENTER); //debug

    }

    public void tick() {

        //遍历执行
        for (Sprite obj : gameobj.values()) {
            obj.tick();
        }
        role.tick();

    }

    /**
     * 处理网络指令
     *
     * @param cmd
     */
    public void processCmd(CmdPkg cmd) {
        //System.out.println("cmdpkg id:" + Integer.toHexString(cmd.getCommandID()));
        int cat = cmd.getCatId();
        switch (cat) {
            case Const.CMD_SYS: {
                procSysCmd(cmd);
                break;
            }
            case Const.CMD_ROLE: {
                role.processCmd(cmd);
                break;
            }
            case Const.CMD_MAP: {
                procMapCmd(cmd);
                break;
            }
            case Const.CMD_GAMEOBJ: {
                procGOCmd(cmd);
                break;
            }
            case Const.CMD_CHATSESSION: {
                bbClient.getChat().processCmd(cmd);
                break;
            }

        }
    }

    /**
     * 处理系统指令
     *
     * @param cmd
     */
    void procSysCmd(CmdPkg cmd) {
        int id = cmd.getCommandID();
        switch (id) {
            case Const.S_SYS_UI: {
                byte[] utf8 = cmd.readByteArray(cmd.remainRead());

                InputStream is = new ByteArrayInputStream(utf8);
                //System.out.println(new String(utf8));
            }
            break;
            case Const.S_SYS_MESSAGE: {
                byte type = cmd.readByte();
                String s = cmd.readUTF();
                System.out.println("系统消息: " + s);
                mcvs.setMessage(s);
            }
            break;
        }
    }

    /**
     * 处理地图指令
     *
     * @param cmd
     */
    void procMapCmd(CmdPkg cmd) {
        int id = cmd.getCommandID();
        switch (id) {
            case Const.S_MAP_SEND_DATA_GRID: {
                short len = cmd.readShort();
                if (len > 0) {
                    byte[] b = cmd.readByteArray(len);
                    String roomid = cmd.readUTF();
                    String name = cmd.readUTF();
                    map = new GameMap(this, roomid, b);
                    map.setName(name);
                    role.setMap(map);
                    role.setState(NORMAL);
                    role.loadSuccess();
                }
            }
            break;
            case Const.S_MAP_CHANGE_ROOM: {

                int idx = cmd.readInt();
                String roomid = cmd.readUTF();
                int x = cmd.readShort();
                int y = cmd.readShort();
                role.changeRoom(roomid, x, y);
            }
            break;
            case Const.S_MAP_SEND_GATES: {

                String room = cmd.readUTF();
                cmd.readByte();
                cmd.readByte();
                int len = cmd.readByte();
                for (int i = 0; i < len; i++) {
                    Gate gate = new Gate(map);
                    gate.col = cmd.readShort();
                    gate.row = cmd.readShort();
                    gate.cols = cmd.readByte();
                    gate.rows = cmd.readByte();
                    cmd.readByte();
                    gate.toName = cmd.readUTF();

                    map.addGate(gate);
                }

            }
            break;
            case Const.S_MAP_MIDI: {
                cmd.readByte();
            }
            break;
        }
    }

    /**
     * 处理系统指令
     *
     * @param cmd
     */
    void procGOCmd(CmdPkg cmd) {
        int id = cmd.getCommandID();
        switch (id) {
            case Const.S_GAMEOBJ_ADD: {
                byte type = cmd.readByte();
                if (type == Sprite.TYPE_MONSTER) {
                    long oid = cmd.readLong();
                    int ox = cmd.readShort();
                    int oy = cmd.readShort();
                    byte relation = cmd.readByte();
                    int lev = cmd.readShort();
                    String name = cmd.readUTF();
                    Npc npc = new Npc();
                    npc.setId(oid);
                    npc.setXY(ox, oy);
                    npc.setName(name);
                    npc.setLev(lev);
                    npc.setRelation(relation);
                    npc.setMap(map);
                    gameobj.put(oid, npc);
                } else if (type == Sprite.TYPE_PLAYER) {
                    long oid = cmd.readLong();
                    int ox = cmd.readShort();
                    int oy = cmd.readShort();
                    byte relation = cmd.readByte();
                    byte camp = cmd.readByte();
                    byte gender = cmd.readByte();
                    int lev = cmd.readShort();
                    String name = cmd.readUTF();
                    Player p = new Player(this);
                    p.setId(oid);
                    p.setCamp(camp);
                    p.setXY(ox, oy);
                    p.setLev(lev);
                    p.setName(name);
                    p.setRelation(relation);
                    p.setMap(map);
                    gameobj.put(oid, p);
                }
            }
            break;
            case Const.S_GAMEOBJ_REMOVE: {
                long oid = cmd.readLong();
                gameobj.remove(oid);
            }
            break;

            case Const.S_GAMEOBJ_MOVE: {
                long oid = cmd.readLong();
                int moveToX = cmd.readShort();
                int moveToY = cmd.readShort();
                Sprite sp = gameobj.get(oid);
                if (sp != null) {
                    sp.moveto(moveToX, moveToY);
                }
            }
            break;

        }
    }

    public void send(CmdPkg cmd) {
        bbClient.send(cmd);
    }

    /**
     * @return the mcvs
     */
    public BbClient getBbClient() {
        return bbClient;
    }

    public GameMap getMap() {
        return map;
    }

    public void activeNpc(Sprite npc) {
        if (npc.getRelation() == Sprite.RELATION_FRIEND) {
            CmdPkg out = new CmdPkg(Const.C_MONSTER_GET_MENU);
            out.writeLong(npc.getId());
            send(out);
        } else { //近前攻击
            role.srvFindPath(npc.getMap().getRoomid(), npc.getMap().getCol(npc.getX()), npc.getMap().getRow(npc.getY()));
            mcvs.setMessage(Util.getStr(Util.STR_GOTO_ENEMY) + " " + npc.getName());
        }
    }

    public void pointerReleased(int px, int py) {
        for (Sprite sp : gameobj.values()) {
            if (sp.inRange(px, py)) {
                activeNpc(sp);
                return;
            }
        }

        if (role != null && map != null) {
            role.srvFindPath(map.getRoomid(), map.getCol(px), map.getRow(py));
        }
    }

    public MyPlayer getMyPlayer(){
        return role;
    }
}
