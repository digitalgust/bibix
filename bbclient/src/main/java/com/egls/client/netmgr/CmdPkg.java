package com.egls.client.netmgr;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.egls.core.net.DataPackage;


/**
 * 指令数据结构<br />
 * Server/Client之间以Cmd方式交互<br />
 * 指令 = 压缩标记(byte) + 指令类型(commandId,必须) + 参数(params,可选)<br />
 *
 * @see 《指令格式文档》
 * @author yaha
 *
 */
public final class CmdPkg implements DataPackage {

    static final Map<Short, Statistics> statistics = new HashMap<Short, Statistics>();
    static public int POS_CRC = 0, POS_ZIP = 1, POS_DATA = 2;

    /**
     *
     * @return
     */
    public static final String statistics() {
        Object[] list = statistics.values().toArray();
        Arrays.sort(list);
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (Object o : list) {
            count++;
            Statistics s = (Statistics) o;
            sb.append(count).append(". ").append(s).append("\n");
            if (count >= 20) {
                break;
            }
        }
        return sb.toString();
    }
    /**
     * 指令所属大类标识
     */
    static final int MASK_CAT = 0xff00;
    /**
     * 命令编号标识
     */
    static final int MASK_CMD = 0x00ff;
    /**
     * Command Id
     */
    short commandID;
    /**
     * Cmd Data
     */
    byte[] data = new byte[64];
    /**
     * 当前位置指针
     */
    int readPos;
    int writePos;
    StringBuilder log;// log记录

    /**
     * Create a new Cmd
     *
     * @param id Cmd id
     */
    public CmdPkg(short id) {

        reset();//留出zip标志位，占一个字节
        setCmdid(id);

    }

    /**
     * read pkg from net
     *
     * @param dataPkg
     */
    public CmdPkg(byte[] dataPkg) {
        setByteArray(dataPkg);
    }

    /**
     *
     * @param dataPkg
     */
    @Override
    public void setByteArray(byte[] dataPkg) {
        data = dataPkg;
        readPos = POS_DATA;//留出zip标志位，占一个字节
        commandID = readShort();
        writePos = dataPkg.length;
    }

    @Override
    public void setCmdid(short id) {
        writeShort(id);
        commandID = id;
    }

    @Override
    public void reset() {
        commandID = 0;
        readPos = POS_DATA;
        writePos = POS_DATA;
    }

    /**
     *
     * @return
     */
    public String getLog() {
        if (log != null) {
            String s = log.toString();
            s = s.replaceAll("\n", "");
            log = null;
            return s;
        } else {
            return null;
        }
    }

    public void appendLog(String s) {
    }

//    public byte[] getData() {
//        byte[] result;
//        if (writePos < data.length) {
//            byte[] temp = new byte[writePos];
//            System.arraycopy(data, 0, temp, 0, writePos);
//            result = temp;
//        } else {
//            result = data;
//        }
//        return result;
//    }
    /**
     * convert to byte array,ready for send
     *
     * @return
     */
    @Override
    public final byte[] toByteArray() {
        byte[] result;
        byte[] temp = new byte[writePos];
        System.arraycopy(data, 0, temp, 0, writePos);
        result = temp;

        //统计
        try {
            Statistics s = statistics.get(commandID);
            if (s == null) {
                s = new Statistics(commandID);
                statistics.put(commandID, s);
            }
            s.count++;
            s.pkg += result.length;
        } catch (Exception e) {
        }
        return result;
    }

    // gust 去掉压缩标志,把标志放入底层处理，上层不处理此问题20090514
    // /**
    // * convert to byte array,ready for send
    // *
    // * @param zip
    // * 是否压缩
    // *
    // * @return
    // */
    // public byte[] toByteArray(boolean zip) {
    // if (zip) {
    // data[0] = FORCE_COMPRESS;
    // } else {
    // data[0] = FORCE_NOT_COMPRESS;
    // }
    // return toByteArray();
    // }
    // ***********************************
    // * Cmd 结构 *
    // ***********************************
    public int getCommandID() {
        return commandID;
    }

    /**
     * 指令所属大类
     */
    public int getCatId() {
        return commandID >> 8;
    }

    // ***********************************
    // * 读操作，用于解析从client端读取的命令*
    // ***********************************
    public int remainRead() {
        return data.length - readPos;
    }

    public boolean readBoolean() {
        return data[readPos++] != 0;
    }

    public byte readByte() {
        return data[readPos++];
    }

    public char readChar() {
        char c = (char) (((data[readPos + 1] & 0xFF) << 0) + ((data[readPos + 0] & 0xFF) << 8));
        readPos += 2;
        return c;
    }

    public short readShort() {
        short s = (short) (((data[readPos + 1] & 0xFF) << 0) + ((data[readPos + 0] & 0xFF) << 8));
        readPos += 2;
        return s;
    }

    public int readInt() {
        int i = ((data[readPos + 3] & 0xFF) << 0) + ((data[readPos + 2] & 0xFF) << 8) + ((data[readPos + 1] & 0xFF) << 16) + ((data[readPos + 0] & 0xFF) << 24);
        readPos += 4;
        return i;
    }

    public long readLong() {
        long l = ((data[readPos + 7] & 0xFFL) << 0) + ((data[readPos + 6] & 0xFFL) << 8) + ((data[readPos + 5] & 0xFFL) << 16) + ((data[readPos + 4] & 0xFFL) << 24) + ((data[readPos + 3] & 0xFFL) << 32) + ((data[readPos + 2] & 0xFFL) << 40) + ((data[readPos + 1] & 0xFFL) << 48) + ((data[readPos + 0] & 0xFFL) << 56);
        readPos += 8;
        return l;
    }

    public byte[] readByteArray(int length) {
        if (length == -1 || readPos + length > data.length) {
            length = data.length - readPos;
        }
        byte[] temp = new byte[length];
        System.arraycopy(data, readPos, temp, 0, length);
        readPos += length;
        return temp;
    }

    public String readUTF() {
        int utflen = readUnsignedShort();
        if (utflen == -1) {
            System.err.println("Error!! ByteArray: readUTF()");
            return "ERROR";
        }
        byte[] bytearr = null;
        char[] chararr = null;

        bytearr = readByteArray(utflen);
        chararr = new char[utflen];

        int c, char2, char3;
        int count = 0;
        int chararr_count = 0;

        while (count < utflen) {
            c = (int) bytearr[count] & 0xff;
            if (c > 127) {
                break;
            }
            count++;
            chararr[chararr_count++] = (char) c;
        }

        while (count < utflen) {
            c = (int) bytearr[count] & 0xff;
            switch (c >> 4) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    /* 0xxxxxxx */
                    count++;
                    chararr[chararr_count++] = (char) c;
                    break;
                case 12:
                case 13:
                    /* 110x xxxx 10xx xxxx */
                    count += 2;
                    char2 = (int) bytearr[count - 1];
                    chararr[chararr_count++] = (char) (((c & 0x1F) << 6) | (char2 & 0x3F));
                    break;
                case 14:
                    /* 1110 xxxx 10xx xxxx 10xx xxxx */
                    count += 3;
                    char2 = (int) bytearr[count - 2];
                    char3 = (int) bytearr[count - 1];
                    chararr[chararr_count++] = (char) (((c & 0x0F) << 12) | ((char2 & 0x3F) << 6) | ((char3 & 0x3F) << 0));
                    break;
                default:
                    return null;
                //throw new Exception("CmdPkg readUTF error.");
                //break;
            }
        }
        String s = new String(chararr, 0, chararr_count);
        return s;
    }

    private int readUnsignedByte() {
        int i = data[readPos++] & 0x00FF;
        return i;
    }

    private int readUnsignedShort() {
        int ch1 = readUnsignedByte();
        int ch2 = readUnsignedByte();
        if ((ch1 | ch2) < 0) {
            return -1;
        }
        int i = (ch1 << 8) + (ch2 << 0);
        return i;
    }

    // ***********************************
    // * 写操作，用于发送命令+参数到client*
    // ***********************************
    /**
     * 检测data数组是否足够长
     *
     * @param length int
     */
    private void ensureCapacity(int length) {
        int newcount = writePos + length;
        if (newcount >= data.length) {
            newcount = Math.max(newcount, data.length << 1);
            byte[] tmp = new byte[newcount];
            System.arraycopy(data, 0, tmp, 0, data.length);
            data = tmp;
        }
    }

    public void writeBoolean(boolean val) {
        ensureCapacity(1);
        data[writePos++] = (byte) (val ? 1 : 0);
    }

    public void writeByte(byte val) {
        ensureCapacity(1);
        data[writePos++] = val;
    }

    public void writeByte(int val) {
        writeByte((byte) val);
    }

    public void writeChar(char c) {
        ensureCapacity(2);
        data[writePos + 1] = (byte) (c >>> 0);
        data[writePos + 0] = (byte) (c >>> 8);
        writePos += 2;
    }

    public void writeShort(short val) {
        ensureCapacity(2);
        data[writePos + 1] = (byte) (val >>> 0);
        data[writePos + 0] = (byte) (val >>> 8);
        writePos += 2;
    }

    public void writeShort(int val) {
        writeShort((short) val);
    }

    public void writeInt(int val) {
        ensureCapacity(4);
        data[writePos + 3] = (byte) (val >>> 0);
        data[writePos + 2] = (byte) (val >>> 8);
        data[writePos + 1] = (byte) (val >>> 16);
        data[writePos + 0] = (byte) (val >>> 24);
        writePos += 4;
    }

    public void writeLong(long val) {
        ensureCapacity(8);
        data[writePos + 7] = (byte) (val >>> 0);
        data[writePos + 6] = (byte) (val >>> 8);
        data[writePos + 5] = (byte) (val >>> 16);
        data[writePos + 4] = (byte) (val >>> 24);
        data[writePos + 3] = (byte) (val >>> 32);
        data[writePos + 2] = (byte) (val >>> 40);
        data[writePos + 1] = (byte) (val >>> 48);
        data[writePos + 0] = (byte) (val >>> 56);
        writePos += 8;
    }

    public void writeByteArray(byte[] src) {
        if (src == null) {
            return;
        }
        ensureCapacity(src.length);
        System.arraycopy(src, 0, data, writePos, src.length);
        writePos += src.length;
    }

    public void writeByteArray(byte[] src, int offset, int len) {
        if (src == null) {
            return;
        }
        ensureCapacity(len);
        System.arraycopy(src, offset, data, writePos, len);
        writePos += len;
    }

    public void writeUTF(String str) {
        writeByteArray(getByteArrFromUTF(str));
    }

    public static byte[] getByteArrFromUTF(String str) {
        int strlen = str.length();
        int utflen = 0;
        int c, count = 0;

        /* use charAt instead of copying String to char array */
        for (int i = 0; i < strlen; i++) {
            c = str.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F)) {
                utflen++;
            } else if (c > 0x07FF) {
                utflen += 3;
            } else {
                utflen += 2;
            }
        }

        byte[] bytearr = new byte[utflen + 2];

        bytearr[count++] = (byte) ((utflen >>> 8) & 0xFF);
        bytearr[count++] = (byte) ((utflen >>> 0) & 0xFF);

        int i = 0;
        for (i = 0; i < strlen; i++) {
            c = str.charAt(i);
            if (!((c >= 0x0001) && (c <= 0x007F))) {
                break;
            }
            bytearr[count++] = (byte) c;
        }

        for (; i < strlen; i++) {
            c = str.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F)) {
                bytearr[count++] = (byte) c;

            } else if (c > 0x07FF) {
                bytearr[count++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
                bytearr[count++] = (byte) (0x80 | ((c >> 6) & 0x3F));
                bytearr[count++] = (byte) (0x80 | ((c >> 0) & 0x3F));
            } else {
                bytearr[count++] = (byte) (0xC0 | ((c >> 6) & 0x1F));
                bytearr[count++] = (byte) (0x80 | ((c >> 0) & 0x3F));
            }
        }
        return bytearr;
    }

    private class Statistics implements Comparable<Statistics> {

        final int cmd;
        long pkg;
        long count;

        public Statistics(int cmd) {
            this.cmd = cmd;
        }

        /**
         *
         */
        public int compareTo(Statistics o) {
            if (this.pkg > o.pkg) {
                return -1;
            } else if (this.pkg == o.pkg) {
                if (this.count > o.count) {
                    return -1;
                } else {
                    return 1;
                }
            } else {
                return 1;
            }
        }

        @Override
        public String toString() {
            return  "(" + Integer.toHexString(cmd) + "):" + pkg / 1024 + "k :" + count;
        }
    }
}
