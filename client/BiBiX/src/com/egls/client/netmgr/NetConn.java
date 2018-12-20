/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.client.netmgr;

import com.egls.core.net.DataPackage;
import java.io.IOException;
import java.util.Date;
import java.util.Queue;

import com.egls.core.net.PkgProtocol;
import com.egls.core.net.Session;
import com.egls.core.net.impl.Client;
import com.egls.core.util.Log;

/**
 * 其他主机连到本机后的代理
 *
 * @author gust
 */
public class NetConn implements Session {

    private Client client;
    PkgProtocol pkgProtocol;
    private Queue<byte[]> rpool = new SyncNetQueue<byte[]>();
    private Queue<byte[]> spool = new SyncNetQueue<byte[]>();
    static public int networkMaxPoolSize = 3000;
    //
    private long connTimeout = 7200;// 连接无数据收发超时时间
    //
    ZipCompressor compress = ZipCompressor.getInstance();
    //
    long lastSnd = System.currentTimeMillis();// 最后发送时间
    long lastRcv = System.currentTimeMillis();// 最后接收时间

    public NetConn(Client clt, PkgProtocol prot) {
        client = clt;
        pkgProtocol = prot;
        pkgProtocol.setNetConn(this);
        client.setPtotocol(pkgProtocol);
    }

    public String getIp() {
        return client.getRemoteIp();
    }

    /**
     * 发送包,如果有压缩器则压缩
     *
     * @param pkg
     */
    @Override
    public void send(DataPackage pkg) {
        byte[] buf = pkg.toByteArray();
        checkValid();

        buf[CmdPkg.POS_ZIP] = Compressor.FORCE_NORMAL;// gust 20090717 置压缩标志为自动压缩

        if (buf != null) {
            if (compress != null) {
                compress.push(buf, this);
            } else {
                pkg.setByteArray(buf);
                sendWithoutCompress(pkg);
            }
        }
    }

    /**
     * 发送包不压缩
     *
     * @param pkg
     */
    @Override
    public void sendWithoutCompress(DataPackage pkg) {
        byte[] buf = pkg.toByteArray();

        buf[CmdPkg.POS_ZIP] = Compressor.FORCE_NOT_COMPRESS;// gust 20090514 置压缩标志为不压缩

        if (buf != null) {
            if (compress != null) {
                compress.push(buf, this);
            } else {
                sendImpl(buf);
            }
        }
    }

    public void sendServerCompress(byte[] buf) {

        buf[CmdPkg.POS_ZIP] = Compressor.FORCE_ZIP;// server压缩

        if (buf != null) {
            if (compress != null) {
                compress.push(buf, this);
            } else {
                sendImpl(buf);
            }
        }

    }

    /**
     * 加入缓冲池
     *
     * @param b
     */
    void sendImpl(byte[] b) {
        getSpool().add(b);
        lastSnd = System.currentTimeMillis();
    }



    @Override
    public boolean isClosed() {
        return client.isClosed();
    }

    @Override
    public byte[] receive() {
        checkValid();
        if (!rpool.isEmpty()) {
            lastRcv = System.currentTimeMillis();
            byte[] b = getRpool().poll();
            ZipCompressor.getInstance().incReceiveBytes(b.length);
            ZipCompressor.getInstance().incReceiveCount();
            // 处理压缩

            byte type = b[CmdPkg.POS_ZIP];
            if (type == Compressor.FORCE_ZIP) {// normal
                b = compress.zipuncompress(b, CmdPkg.POS_DATA, b.length);
            }
            return b;
        }
        return null;
    }

    public void tick() {
        try { // 从client中取数据并组数据包
            pkgProtocol.rcvAndDecode();

            // 发数据
            pkgProtocol.sndAndEncode();

            checkValid();
        } catch (Exception e) {
            Log.error("tick error:", e);
            close();
        }
    }

    @Override
    public boolean checkValid() {
        long curMils = System.currentTimeMillis();
        if (rpool.size() > networkMaxPoolSize || spool.size() > networkMaxPoolSize || curMils - lastSnd > connTimeout * 1000 || curMils - lastRcv > connTimeout * 1000) {
            Log.info("CONN CLOSE |" + client.getMemo() + "|" + toString());
            try {
                rpool.clear();
                client.close();
            } catch (IOException ex) {
            }
            return false;
        }
        return true;
    }

    @Override
    public void close() {
        try {
            client.close();
        } catch (IOException e) {
            Log.error("关闭链接出错", e);
        }
    }

    /**
     * @return the client
     */
    @Override
    public Client getClient() {
        return client;
    }

    /**
     * @return the rpool
     */
    @Override
    public Queue<byte[]> getRpool() {
        return rpool;
    }

    /**
     * @return the spool
     */
    @Override
    public Queue<byte[]> getSpool() {
        return spool;
    }

    @Override
    public PkgProtocol getPkgProtocol() {
        return pkgProtocol;
    }

    /**
     * @return the connTimeout
     */
    public long getConnTimeout() {
        return connTimeout;
    }

    /**
     * @param connTimeout the connTimeout to set
     */
    public void setConnTimeout(long connTimeout) {
        this.connTimeout = connTimeout;
    }

    @Override
    public String toString() {
        return "|" + client.getRemoteIp() + ":" + client.getRemotePort() + "|rp:" + rpool.size() + "|sp:" + spool.size() + "|rd:" + new Date(lastRcv) + "|sd:"
                + new Date(lastSnd);
    }

}
