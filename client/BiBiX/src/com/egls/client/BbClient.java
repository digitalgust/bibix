/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.client;

import com.egls.client.audio.AudioMgr;
import com.egls.client.chat.Chat;
import com.egls.client.chat.ChatStateListener;
import com.egls.client.game.Const;
import com.egls.client.game.GameRun;
import com.egls.client.game.MainCanvas;
import com.egls.core.net.impl.Client;
import com.egls.client.netmgr.CmdPkg;
import com.egls.client.netmgr.HeroPkgProtocol;
import com.egls.client.netmgr.NetConn;
import com.egls.client.util.NetCmdHandler;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import org.mini.crypt.AsynCrypt;
import org.mini.crypt.XorCrypt;
import org.mini.gui.GViewPort;

/**
 *
 * @author Gust
 */
public class BbClient implements Runnable, NetCmdHandler {

    //______________公用的控制变量及常量__________________________
    static private final int MILLIS_PER_TICK = 50;
    public static final byte STATE_NONE = 0, STATE_LOGIN = 1, STATE_LOGING = 2, STATE_RELOGIN = 3, STATE_RELOGING = 4, STATE_GAMERUN = 5, STATE_EXIT = 7;
    private byte state = STATE_LOGIN; //游戏状态，用getState()和setState()进行访问
    long timeRun; //run()的一个周期所用的时间

    static final long LOGIN_TIMEOUT = 10 * 1000;
    long loginTimeAt;
    String loginMsg;

    boolean exit = false;

    static String passport, password;
    long roleid;

    BigInteger certificate;
    BigInteger publicKey;

    byte[] xorkey;

    /**
     * 游戏主线程
     */
    //网络服务器
    NetConn conn;
    MainCanvas mCanvas;
    private volatile Thread gameThread;
    private GameRun gameRun;
    Chat chat;


    public BbClient() {


        mCanvas = new MainCanvas(this, 0, 0, 1, 1);
        gameRun = new GameRun(this);

        //
        gameThread = new Thread(this);
        gameThread.start();
        setState(BbClient.STATE_NONE); //初始状态为显示LOGO
    }

    public void close() {
        if (conn != null) {
            conn.close();
            conn = null;
        }
        exit = true;
    }

    public boolean isOnline() {
        if (conn != null && !conn.isClosed()) {
            return true;
        }
        return false;
    }

    /**
     * 初始化操作，主要完成一此数据文件，图片等资源的载入,并完成初始化变量 当按了主菜单的新游戏后被调用
     */
    public void initGui(GViewPort parent) {
        //做相应的初始化操作
        mCanvas.setSize(parent.getW(), parent.getH());
        parent.add(mCanvas);
    }

    /**
     * 线程体
     */
    @Override
    public void run() {
        while (!exit) {
            try {
                final long startTime = System.currentTimeMillis();

                processCommand();
                switch (state) {
                    case STATE_GAMERUN: {
                        if (gameRun != null) {
                            gameRun.tick();
                        }
                        break;
                    }
                    case STATE_LOGIN:
                        login();
                        loginTimeAt = System.currentTimeMillis();
                        setState(STATE_LOGING);
                        break;
                    case STATE_LOGING:
                        if (System.currentTimeMillis() - loginTimeAt > LOGIN_TIMEOUT) {
                            loginMsg = BbStrings.getString("login timeout");
                            setState(STATE_NONE);
                            BbApplication.showLoginForm();
                        }
                        break;
                    case STATE_RELOGIN:
                        login();
                        loginTimeAt = System.currentTimeMillis();
                        setState(STATE_RELOGING);
                        break;
                    case STATE_RELOGING:
                        if (System.currentTimeMillis() - loginTimeAt > LOGIN_TIMEOUT) {

                            if (conn != null) {
                                conn.close();
                                setState(STATE_RELOGIN);
                            }
                        }
                    case STATE_EXIT:
                        break;
                    default:
                        break;
                }

                timeRun = System.currentTimeMillis() - startTime;
                if (timeRun < MILLIS_PER_TICK) {
                    synchronized (this) {
                        wait(MILLIS_PER_TICK - timeRun); //使每个周期大致相等
                    }
                }

            } catch (Exception ex) {
                // won't be thrown
                ex.printStackTrace();
            }
        }
        //release
    }

    /**
     * 设置当前状态
     *
     * @param s byte 状态值
     */
    public final void setState(byte s) {
        state = s;
    }

    /**
     * 得到状态
     *
     * @return byte 返回当前状态
     */
    public final byte getState() {
        return state;
    }

    void clear() {
        conn = null;

    }

    public void processCommand() {
        if (conn != null) {
            if (conn.isClosed()) {
                if (getState() == STATE_GAMERUN) {
                    setState(STATE_RELOGIN);
                }
            } else {
                conn.tick();
                byte[] b = null;
                while ((b = conn.receive()) != null) {
                    CmdPkg cmd = new CmdPkg(b);
                    //System.out.println("cmdid:" + Integer.toHexString(cmd.getCommandID()));

                    switch (state) {
                        case STATE_LOGING:
                        case STATE_RELOGING: {
                            processCmd(cmd);
                            break;
                        }
                        case STATE_GAMERUN: {
                            if (gameRun != null) {
                                gameRun.processCmd(cmd);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void processCmd(CmdPkg cmd) {

        switch (cmd.getCommandID()) {
            case Const.S_SYS_LOGIN: {
                boolean b = cmd.readBoolean();
                loginMsg = cmd.readUTF();
                roleid = cmd.readLong();
                int len = cmd.readInt();
                byte[] cert = cmd.readByteArray(len);
                certificate = new BigInteger(cert);
                len = cmd.readInt();
                byte[] pkey = cmd.readByteArray(len);
                publicKey = new BigInteger(pkey);

                if (b) {
                    sendXorKey();
                    chat = new Chat(this);
                    BbMain main = BbApplication.showMainForm();
                    chat.setChatStateListener((ChatStateListener) main);
                    chat.initFriendList();
                    chat.sendClientActive(true);
                    setState(BbClient.STATE_GAMERUN);
                } else if (state == STATE_LOGING) {
                    setState(BbClient.STATE_NONE);
                    BbApplication.showLoginForm();
                } else if (state == STATE_RELOGING) {
                }
                break;
            }
        }
    }

    public String getLoginMessage() {
        return loginMsg;
    }

    public void send(CmdPkg cmd) {
        if (conn != null && !conn.isClosed()) {
//            System.out.println("send "+cmd.getCommandID());
            conn.send(cmd);
        }
    }

    public long getRoleid() {
        return roleid;
    }

    /**
     * @return the gameRun
     */
    public GameRun getGameRun() {
        return gameRun;
    }

    public Chat getChat() {
        return chat;
    }

    public MainCanvas getMainCanvas() {
        return mCanvas;
    }

    static String CFG_FILE_NAME = "/cfg.txt";

    static public void save(String passport, String password, int lang) {
        BbClient.passport = passport;
        BbClient.password = password;

        String respath = BbApplication.getInstance().getSaveRoot();
        File f = new File(respath + CFG_FILE_NAME);

        try {
            FileOutputStream fos = new FileOutputStream(f);
            DataOutputStream dos = new DataOutputStream(fos);
            dos.writeUTF(passport + "\n" + password + "\n" + lang);
            fos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    static public String load() {
        String respath = BbApplication.getInstance().getSaveRoot();
        File f = new File(respath + CFG_FILE_NAME);
        String s = null;
        if (f.exists()) {
            try {
                FileInputStream fis = new FileInputStream(f);
                DataInputStream dos = new DataInputStream(fis);
                s = dos.readUTF();

                fis.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return s;
    }

    public void login() {
        if (conn != null) {
            conn.close();
        }
        conn = new NetConn(new Client(Const.serverIp, Const.port), new HeroPkgProtocol());

        CmdPkg cmd = new CmdPkg(Const.C_SYS_LOGIN);
        cmd.writeUTF(Const.clientVer);
        cmd.writeUTF(passport);
        cmd.writeUTF(password);
        send(cmd);

    }

    public void sendXorKey() {

        xorkey = XorCrypt.genKey(6);
//        System.out.println("publicKey:" + AsynCrypt.bytesToHex(publicKey.toByteArray()));
//        System.out.println("certificate:" + AsynCrypt.bytesToHex(certificate.toByteArray()));
//        System.out.println("xorkey:" + AsynCrypt.bytesToHex(xorkey));
        AsynCrypt ac = new AsynCrypt(null, publicKey, certificate);
        byte[] encodedXorKey = ac.encryptMessage(xorkey);

        CmdPkg cmd = new CmdPkg(Const.C_SYS_XOR_KEY);
        cmd.writeInt(encodedXorKey.length);
        cmd.writeByteArray(encodedXorKey);
        send(cmd);
    }

    public byte[] getXorkey() {
        return xorkey;
    }

    public void logout() {
        conn.close();
        roleid = 0;
        setState(BbClient.STATE_NONE);
        BbApplication.showLoginForm();
    }
}
