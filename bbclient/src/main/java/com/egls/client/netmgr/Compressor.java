/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.client.netmgr;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author gust
 */
abstract public class Compressor {

    /**
     * 强制压缩
     */
    static public final byte FORCE_ZIP = (byte) 0xff;
    /**
     * 强制不压缩
     */
    static public final byte FORCE_NOT_COMPRESS = (byte) 0xfe;
    /**
     * 正常
     */
    static public final byte FORCE_NORMAL = (byte) 0x0;
    /**
     *
     */
    long debug_sendPkgCount = 0;// 包总数
    long debug_compressedCount = 0;// 压缩包总数
    long debug_uncompressedBytes = 0;// 未压缩字节总数
    long debug_compressedBytes = 0;// 压缩后字节总数
    long debug_beforeCompressBytes = 0;// 压缩前字节数
    long debug_startTime = 0; // 接收部分
    long debug_receiveCount = 0;
    long debug_receiveBytes = 0;
    long debug_pendingReceiveCount = 0;
    long debug_pendingReceiveBytes = 0;
    static public long debug_compressSpent = 0;

    /**
     * 压缩
     *
     * @param data
     * @param nhandler
     */
    abstract public void push(byte[] data, NetConn nhandler);

    
    static public void main(String[] args){
        FileOutputStream f=null;
        try {
            byte[] b=new byte[1000];
            byte[] z=toGzipData(b);
            f = new FileOutputStream("ztest.zip");
            f.write(z);
        } catch (IOException ex) {
            Logger.getLogger(Compressor.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                f.close();
            } catch (IOException ex) {
                Logger.getLogger(Compressor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    /**
     *
     * @param src
     * @return
     */
    static public byte[] toGzipData(byte[] src) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gos = new GZIPOutputStream(baos);
            gos.write(src, 0, src.length);
            gos.close();
            return baos.toByteArray();
        } catch (Exception e) {
            // showMsg("！压缩输出时失败：" + e.getMessage());
            return null;
        }
    }

    /**
     *
     * @param src
     * @param from
     * @param to
     * @return
     */
    static public byte[] fromGzipData(byte[] src, int from, int to) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(src, from, to - from);
            GZIPInputStream gis = new GZIPInputStream(bais);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int b;
            while ((b = gis.read()) != -1) {
                bos.write(b);
            }
            gis.close();
            return bos.toByteArray();

        } catch (Exception e) {
            //System.out.println("！解压缩输出时失败：" + e.getMessage());
            return null;
        }
    }

    /**
     * @return the debug_pendingReceiveBytes
     */
    public final long getDebug_pendingReceiveBytes() {
        return debug_pendingReceiveBytes;
    }

    /**
     * @return the debug_pendingReceiveCount
     */
    public final long getDebug_pendingReceiveCount() {
        return debug_pendingReceiveCount;
    }

    /**
     * @param debug_pendingReceiveBytes the debug_pendingReceiveBytes to set
     */
    public final void incDebug_pendingReceiveBytes(long debug_pendingReceiveBytes) {
        this.debug_pendingReceiveBytes += debug_pendingReceiveBytes;
    }

    /**
     */
    public final void incDebug_pendingReceiveCount() {
        this.debug_pendingReceiveCount++;
    }

    /**
     */
    public final void decDebug_pendingReceiveCount(int count) {
        this.debug_pendingReceiveCount -= count;
    }

    /**
     * 强制标志所在位置
     */
    public static long getDebug_compressSpent() {
        return debug_compressSpent;
    }

    public static void setDebug_compressSpent(long aDebug_compressSpent) {
        debug_compressSpent = aDebug_compressSpent;
    }

    /**
     *
     * @return
     */
    public long getDebug_compressedCount() {
        return debug_compressedCount;
    }

    public long getDebug_pkgCount() {
        return debug_sendPkgCount;
    }

    public long getTotalSentBytes() {
        return getDebug_compressedBytes() + getDebug_uncompressedBytes();
    }
// 压缩包总数

    public long getDebug_uncompressedBytes() {
        return debug_uncompressedBytes;
    }
// 未压缩字节总数

    public long getDebug_compressedBytes() {
        return debug_compressedBytes;
    }
// 压缩后字节总数

    public long getDebug_beforeCompressBytes() {
        return debug_beforeCompressBytes;
    }
// 压缩前字节数

    public long getDebug_startTime() {
        return debug_startTime;
    }

    public long getDebug_now() {
        return System.currentTimeMillis();
    }

    public long getDebug_receiveBytes() {
        return debug_receiveBytes;
    }

    public void incReceiveBytes(long bytes) {
        debug_receiveBytes += bytes;
        debug_pendingReceiveBytes -= bytes;
    }

    public long getDebug_receiveCount() {
        return debug_receiveCount;
    }

    public void incReceiveCount() {
        debug_receiveCount++;
        debug_pendingReceiveCount--;
    }

}
