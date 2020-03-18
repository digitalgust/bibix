/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.client.netmgr;

import com.egls.core.util.Log;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Queue;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 压缩器
 *
 * @author Gust
 */
public class ZipCompressor extends Compressor implements Runnable {

    static ZipCompressor instance = new ZipCompressor();

    static public ZipCompressor getInstance() {
        return instance;
    }

    private ZipCompressor() {
        Thread t = new Thread(this);
        // t.setDaemon(true);
        t.start();
        t.setName("ZipCompressor");
        debug_startTime = System.currentTimeMillis();
    }

    /**
     *
     * @author gust
     */
    /**
     * 数据结构
     */
    class Pair<L, R> {

        public L left;
        public R right;

        public Pair(L left, R right) {
            this.left = left;
            this.right = right;
        }
    }

    private Queue<Pair<byte[], NetConn>> pkgs = new SyncNetQueue<Pair<byte[], NetConn>>();
    private boolean shutdown = false;
    static public int WAITTING = 5;// ms 无工作时等待,20130527 gust
    // 修改使主线程不需要notify,压缩器自动不停探测
    /**
     * 压缩临界点
     */
    static public final int SIZE_OF_PKG_COMPRESS = 512 * 1024 * 1024;
    // 压缩临界点
    public static int networkZipSize = SIZE_OF_PKG_COMPRESS;

    /**
     * 压缩临界点
     *
     * @return
     */
    public int getZip() {
        return networkZipSize;
    }

    public void setZip(int z) {
        networkZipSize = z;
    }

    public int getPendingCompressPkg() {
        return pkgs.size();
    }

    // @Override
    /**
     * 线程体
     */
    @Override
    public void run() {
        long s = 0;
        while (!shutdown) {
            // synchronized (this) {
            try {
                if (!pkgs.isEmpty()) {
                    Pair<byte[], NetConn> pp = pkgs.poll();
                    byte[] data = pp.left;

                    NetConn handler = pp.right;

                    if ((data.length > networkZipSize && data[CmdPkg.POS_ZIP] == FORCE_NORMAL) // 正常包，超过临界点时压缩
                            || data[CmdPkg.POS_ZIP] == FORCE_ZIP// 强制
                            ) {
                        s = System.currentTimeMillis();
                        byte[] cdata = null;
                        {
                            cdata = zipcompress(data, CmdPkg.POS_DATA, data.length);
                        }

                        setDebug_compressSpent(getDebug_compressSpent() + System.currentTimeMillis() - s);
                        handler.sendImpl(cdata);

                        debug_compressedCount++;
                        debug_compressedBytes += cdata.length;
                        debug_beforeCompressBytes += data.length;
                    } else {// 不压缩
                        data[CmdPkg.POS_ZIP] = 0;
                        handler.sendImpl(data);

                        debug_uncompressedBytes += data.length;
                    }
                    debug_sendPkgCount++;
                } else {
                    Thread.sleep(WAITTING);
                    // System.out.println("-------------------------------active");
                }
            } catch (Exception ex) {
                Log.error("zipcompressor error:", ex);
            }
            // }
        }
        // Log.info(Log.RUNTIME, "包压缩器已关闭:" + toString());
    }

    /**
     *
     * @param pkg
     * @param nhandler
     */
    @Override
    synchronized public final void push(byte[] data, NetConn nhandler) {
        if (data != null) {
            pkgs.add(new Pair(data, nhandler));
            // notify();//20130527gust取消通知
        }
    }

    /**
     * 关闭
     */
    public void shutdown() {
        shutdown = true;
    }

    /**
     * 压缩
     *
     * @param data
     * @param from
     * @param to
     * @return
     * @throws java.io.IOException
     */
    public byte[] zipcompress(byte[] data, int from, int to) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bos.write(0);//crc
        bos.write(1);// 压缩标志
        GZIPOutputStream zos = new GZIPOutputStream(bos);
        zos.write(data, from, to - from);
        zos.close();

        byte[] c = bos.toByteArray();
        bos.close();

        return c;
    }

    /**
     *
     * @param src
     * @param from
     * @param to
     * @return
     */
    public byte[] zipuncompress(byte[] src, int from, int to) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(src, from, to - from);
            GZIPInputStream gis = new GZIPInputStream(bais);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bos.write(src[0]);
            bos.write(src[1]);
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

}
