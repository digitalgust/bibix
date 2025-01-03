package com.egls.core.net.impl;

import com.egls.core.net.PkgProtocol;
import com.egls.core.util.Log;

import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author EGLS
 */
public class Client {

    /**
     * 连接
     */
    SocketConnection conn;

    private PkgProtocol ptotocol;
    /**
     *
     */
    static public final int STATE_OPEN = 0;
    static public final int STATE_CLOSE = 1;
    int state = STATE_OPEN;
    // 连接超时时间
    static public int CONNECTING_TIMEOUT_DEFAULT = 30;// 连接超时时间
    private int connectTimeout = CONNECTING_TIMEOUT_DEFAULT;
    long connectStartAt = System.currentTimeMillis();
    ;// 连接开始时间
    /**
     * 输入缓存
     */
    static final int MAX_RCV_BUF_SIZE = 2048;
    /**
     * 远端ip,port
     */
    String r_ip;
    int r_port;
    /**
     * 上层应用的备注信息
     */
    private String memo = "";
    String breakCause = "";

    byte[] rbuf = new byte[MAX_RCV_BUF_SIZE];
    int rbuf_offset, rbuf_len;

    /**
     * 通过ip地址创建连接
     *
     * @param host
     * @param port
     */
    public Client(String host, int port) {
        try {
            //
            r_ip = host;
            r_port = port;
            // Selector selector = reactor.getSelector();
            // System.out.println(""+selector);
            conn = (SocketConnection) Connector.open("socket://" + r_ip + ":" + r_port);
            InetSocketAddress isa = new InetSocketAddress(host, port);
            conn.setSocketOption(SocketConnection.NONBLOCK, 1);
        } catch (IOException ex) {
            Log.error("construct Client error", ex);
            state = STATE_CLOSE;
        }
    }

    public boolean isClosed() {
        return state == STATE_CLOSE;
    }

    public int write(byte[] buf, int offset, int len) throws IOException {
        if (!isClosed()) { // 连接中

            try {
                int w = conn.write(buf, offset, len);
                if (w == -1) {
                    close();
                    return -1;
                }
                return w;
            } catch (Exception e) {
                Log.info("channel write error, " + r_ip + ":" + r_port + "|" + memo + "|" + e.getMessage());
                breakCause = "W|" + e.getMessage();
                close();
            }
        } else {
            close();
        }
        return -1;
    }

    public int available() throws IOException {
        try {
            if (rbuf_len == 0) {
                rbuf_offset = 0;
            }
            if (rbuf_offset > rbuf.length / 2) {
                System.arraycopy(rbuf, rbuf_offset, rbuf, 0, rbuf_len);
                rbuf_offset = 0;
            }
            int space = rbuf.length - (rbuf_offset + rbuf_len);
            if (space > 0) {
                int r = conn.read(rbuf, rbuf_offset + rbuf_len, rbuf.length - (rbuf_offset + rbuf_len));
                if (r == -1) {
                    close();
                    return -1;
                }
                rbuf_len += r;
            }
            return rbuf_len;
        } catch (Exception ex) {
            close();
            return -1;
        }
    }

    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    public int read(byte[] b, int offset, int len) throws IOException {
        if (!isClosed()) {// 已连接
            try {
                int r = available();
                if (r == -1) {
                    close();
                    return -1;
                }
                int min = rbuf_len < len ? rbuf_len : len;
                System.arraycopy(rbuf, rbuf_offset, b, offset, min);
                rbuf_offset += min;
                rbuf_len -= min;
                return min;
            } catch (Exception e) {
                Log.info("channel read error, " + r_ip + ":" + r_port + "|" + memo + "|" + e.getMessage());
                breakCause = "R|" + e.getMessage();
                close();
            }
        } else {
            close();

        }
        return -1;
    }

    public void close() throws IOException {
        state = STATE_CLOSE;
        if (conn != null) {
            conn.close();
        }
    }

    public String getRemoteIp() {
        return r_ip;
    }

    public int getRemotePort() {
        return r_port;
    }

    @Override
    public String toString() {
        return r_ip + ":" + r_port + "|" + memo + "|" + breakCause;
    }

    /**
     * @return the ptotocol
     */
    public PkgProtocol getPtotocol() {
        return ptotocol;
    }

    /**
     * @param ptotocol the ptotocol to set
     */
    public void setPtotocol(PkgProtocol ptotocol) {
        this.ptotocol = ptotocol;
    }

    /**
     * @return the connectTimeout
     */
    public int getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * @param connectTimeout the connectTimeout to set
     */
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    /**
     * @return the memo
     */
    public String getMemo() {
        return memo;
    }

    /**
     * @param memo the memo to set
     */
    public void setMemo(String memo) {
        this.memo = memo;
    }

    public SocketConnection getSocketConnection() {
        return conn;
    }

}
