/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.client.netmgr;

import java.util.Queue;

import com.egls.core.net.PkgProtocol;
import com.egls.core.net.Session;
import com.egls.core.net.impl.Client;

/**
 *
 * 负责处理协议对数据的影响
 *
 * @author gust
 */
public class HeroPkgProtocol implements PkgProtocol {

    Session conn;
    Client client;
    Queue<byte[]> rpool;
    Queue<byte[]> spool;
    HeroCoder coder;

    /**
     *
     */
    public HeroPkgProtocol() {
        coder = new HeroCoder();
    }

    /**
     * 设置连接通道
     *
     * @param conn
     */
    @Override
    public void setNetConn(Session conn) {
        this.conn = conn;
        client = conn.getClient();
        rpool = conn.getRpool();
        spool = conn.getSpool();
    }

    /**
     * 接收数据
     */
    int pkgNeed = -1;
    static final int PKG_BYTESSIZE_LEN = 4;// 包 长度定义 为几个字节
    public static final int MAX_PKG_LEN = 512 * 1024 * 1024;// 包最大长度字节
    public static int maxPkgSize = MAX_PKG_LEN;
    byte[] rcvBuf;

    @Override
    public void rcvAndDecode() throws Exception {
        conn.checkValid();
        if (client.isClosed()) {
            return;
        }
        while (true) {// 循环接收多个数据包
            if (rcvBuf == null) {
                int canRead = client.available();
                if (canRead < 0) {
                    client.close();
                    return;
                }
                if (canRead < PKG_BYTESSIZE_LEN) {// 如果不足取包长度的字节数
                    return;
                } else {
                    // 取长度
                    byte[] pkglen = new byte[PKG_BYTESSIZE_LEN];
                    int r = client.read(pkglen);
                    if (r < PKG_BYTESSIZE_LEN) {
                        throw new Exception("read pkg len error, need " + PKG_BYTESSIZE_LEN + " bytes, but " + r + " bytes");
                    }
                    pkgNeed = ((pkglen[0] & 0xff) << 24) | ((pkglen[1] & 0xff) << 16) | ((pkglen[2] & 0xff) << 8) | (pkglen[3] & 0xff);
                    if (pkgNeed > MAX_PKG_LEN) {
                        client.close();
                        return;
                    }
                    rcvBuf = new byte[pkgNeed];
                }
            }
            if (rcvBuf != null) {
                int canRead = client.available();
                if (canRead < 0) {
                    client.close();
                    return;
                } else if (canRead == 0) {
                    return;
                }
                if (canRead > pkgNeed) {
                    canRead = pkgNeed;
                }
                int read = client.read(rcvBuf, rcvBuf.length - pkgNeed, pkgNeed);
                pkgNeed -= read;
                if (pkgNeed == 0) {
                    if (isEncode()) {
                        rcvBuf = coder.decode(rcvBuf);
                    }
                    if (rcvBuf != null) {
                        rpool.add(rcvBuf);
                    }
                    rcvBuf = null;
                }
            }
        }
    }

    /**
     * 发送
     */
    private byte[] sndBuf;
    int sent = 0;

    @Override
    public void sndAndEncode() throws Exception {
        conn.checkValid();
        if (client.isClosed()) {
            return;
        }
        while (true) {// 循环接收多个数据包
            if (sndBuf == null) {
                if (!spool.isEmpty()) {
                    byte[] sd = spool.poll();

                    if (isEncode()) {
                        sd = coder.encode(sd);
                    }
                    // System.out.println("encode: "+CryptoUtils.byte2HexStr(sd));
                    if (sd.length == 0) {
                        System.out.println("=======================error:" + sd.length);
                        throw new Exception("data error, length =0");
                    }
                    byte[] nsd = new byte[sd.length + PKG_BYTESSIZE_LEN];
                    int dlen = sd.length;//
                    nsd[0] = (byte) ((dlen >> 24) & 0xff);
                    nsd[1] = (byte) ((dlen >> 16) & 0xff);
                    nsd[2] = (byte) ((dlen >> 8) & 0xff);
                    nsd[3] = (byte) (dlen & 0xff);
                    System.arraycopy(sd, 0, nsd, PKG_BYTESSIZE_LEN, sd.length);
                    sndBuf = nsd;

                } else {

                    break;
                }
            }
            if (sndBuf != null) {
                int len = client.write(sndBuf, sent, sndBuf.length - sent);
                if (len < 0) {
                    client.close();
                    return;
                } else if (len == 0) {
                    break;
                }
                sent += len;
                if (sent == sndBuf.length) {
                    // print(sndBuf.array());
                    sent = 0;
                    sndBuf = null;
                    // break;
                }
            }

        }
    }

    // ----------------------------------------------------------------------------
    // no comment
    // ----------------------------------------------------------------------------
    static public void print(byte[] b) {
        for (int i = 0; i < b.length; i++) {
            String s = Integer.toHexString(b[i]&0xff);
            s = s.length() > 1 ? s : "0" + s;
            System.out.print(" " + s);
        }
        System.out.println();
    }

    /**
     * @return the encode
     */
    public boolean isEncode() {
        return coder != null;
    }

    /**
     * 设最大包长度
     *
     * @param size
     */
    @Override
    public void setMaxPkgSize(int size) {
        maxPkgSize = size;
    }

    /**
     * 取最大包长度
     *
     * @return
     */
    @Override
    public int getMaxPkgSize() {
        return maxPkgSize;
    }
}
