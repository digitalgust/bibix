/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.client.netmgr;

/**
 * 加密工具类
 *
 * @version 1.0
 * @author gust
 */
public class HeroCoder {

    // ----------------------------------------------------------------------------
    // no comment
    // ----------------------------------------------------------------------------
    static private final int ENC_KEY = 0x6e9f3a;
    private int s_pkgKey = ENC_KEY, r_pkgKey = ENC_KEY;

    /**
     *
     * @param data
     * @return
     */
    public byte[] encode(byte[] data) {
        if (data == null) {
            return null;
        }
        //
        s_pkgKey++;
        if (s_pkgKey == Integer.MAX_VALUE) {
            s_pkgKey = ENC_KEY;
        }
        // System.out.println("发送, encode 前:" + s_pkgKey);
        // print(data);
        //
        int resume = 0;
        byte s_crc1 = (byte) ((~(s_pkgKey % 0x100)) & 0xff);
        for (int i = CmdPkg.POS_ZIP, len = data.length; i < len; i++) {
            resume += data[i];
            data[i] = encryptByte(data[i], s_crc1);
        }
        byte s_crc2 = (byte) ((resume % 0x100) & 0xff);

        data[CmdPkg.POS_CRC] = encryptByte(s_crc2, s_crc1);
        // System.out.println("发送, encode 后:" + s_pkgKey);
        // print(data);

        return data;
    }

    /**
     *
     * @param data
     * @return
     */
    public byte[] decode(byte[] data) throws Exception {
        if (data == null) {
            return null;
        }
        //
        r_pkgKey++;
        if (r_pkgKey == Integer.MAX_VALUE) {
            r_pkgKey = ENC_KEY;
        }
        // System.out.println("接收, decode 前:" + r_pkgKey);
        // print(data);
        //
        int resume = 0;
        byte s_crc1 = (byte) ((~(r_pkgKey % 0x100)) & 0xff);
        for (int i = CmdPkg.POS_ZIP, len = data.length; i < len; i++) {
            data[i] = decryptByte(data[i], s_crc1);
            resume += data[i];
        }

        data[CmdPkg.POS_CRC] = decryptByte(data[CmdPkg.POS_CRC], s_crc1);
        byte s_crc2 = (byte) ((resume % 0x100) & 0xff);

        // System.out.println("接收, decode 后:" + r_pkgKey);
        // print(data);
        if (data[CmdPkg.POS_CRC] != s_crc2) {
            // System.out.println("解码数据包时错误.");
            // closeConnection();
            throw new Exception("attempt to modify netpkg ,break conn ");
            // close(STATE_CLOSED_PKG_MODIFIED);// 关闭连接
        }

        return data;
    }

    private byte encryptByte(byte b, byte key) {
        int i = b & 0xff;
        i = (i + key) % 0x100;
        b = (byte) ((~(i & 0xff)) & 0xff);
        b = (byte) ((b ^ ((key << 4) | (key >>> 4))) & 0xff);
        return b;
    }

    private byte decryptByte(byte b, byte key) {
        b = (byte) ((b ^ ((key << 4) | (key >>> 4))) & 0xff);
        b = (byte) ((~b) & 0xff);
        int i = b & 0xff;
        i = (i + 256 - key) % 0x100;
        return (byte) (i & 0xff);
    }

    public void print(byte[] b) {
        for (int i = 0; i < b.length; i++) {
            String s = Integer.toHexString(b[i]&0xff);
            s = s.length() > 1 ? s : "0" + s;
            System.out.print(" " + s);
        }
        System.out.println();
    }

}
