package com.egls.core.util;

import com.egls.core.net.Loger;

public class Log {

    static final int INFO = 0;
    static final int DEBUG = 1;
    static final int WARNING = 2;
    static final int ERROR = 3;
    private static final String[] LOG_NAME = new String[]{"INFO", "DEBUG", "WARNING", "ERROR"};
    public static String logPath = "log.txt";
    private static SimpLoger logfile;
    static public int level = ERROR;

    static public Loger getLoger(String savePath) {
        if (logfile == null) {
            logfile = new SimpLoger(savePath + "/" + logPath);
        }
        return logfile;
    }

    /**
     * Debug消息
     *
     * @param s 消息内容
     */
    public static void debug(String s) {
        if (logfile != null && level >= DEBUG) {
            logfile.info("|" + LOG_NAME[DEBUG] + "|" + s);
        }
    }

    /**
     * Info消息
     *
     * @param s 消息内容
     */
    public static void info(String s) {
        if (logfile != null && level >= INFO) {
            logfile.info("|" + LOG_NAME[INFO] + "|" + s);
        }
    }

    /**
     * warnning 消息
     *
     * @param s 消息内容
     */
    public static void warning(String s) {
        if (logfile != null && level >= WARNING) {
            logfile.info("|" + LOG_NAME[WARNING] + "|" + s);
        }
    }

    /**
     * error 消息
     *
     * @param s 消息内容
     */
    public static void error(String s) {
        if (logfile != null && level >= ERROR) {
            logfile.error("|" + LOG_NAME[ERROR] + "|" + s, null);
        }
    }

    /**
     * error 消息
     *
     * @param s  消息内容
     * @param ex Exception
     */
    public static void error(String s, Exception ex) {
        if (logfile != null && level >= ERROR) {
            logfile.error("|" + LOG_NAME[ERROR] + "|" + s, ex);
        }
    }
}
