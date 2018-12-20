/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.core.util;

import com.egls.core.net.Loger;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author gust
 */
public class SimpLoger extends Thread implements Loger {

    /**
     * 双缓冲
     */
    StringBuilder buf = new StringBuilder(32768);
    StringBuilder buf1 = new StringBuilder(32768);
    String encode = "utf-8";
    String logPath = "";
    //
    boolean exit = false;
    Writer f_log = null;
    //
    Calendar lastc = Calendar.getInstance();
    Calendar curc = Calendar.getInstance();

    boolean console = true;

//
    public SimpLoger(String logPath) {
        this.logPath = logPath;
        setDaemon(true);
        openLog();
        start();
    }

    public void run() {
        long lastAt = 0, cur = 0;

        while (!exit) {
            try {
                //算是不是同一天
                cur = System.currentTimeMillis();
                curc.setTimeInMillis(cur);
                lastc.setTimeInMillis(lastAt);
                if (curc.get(Calendar.DAY_OF_MONTH) != lastc.get(Calendar.DAY_OF_MONTH)) {
                    turnNext();
                }
                Thread.sleep(1000);
                if (buf.length() > 0) {
                    push2File();
                }
                lastAt = cur;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public final void openLog() {
        turnNext();
    }

    public final void closeLog() {
        try {
            exit = true;
            if (f_log != null) {
                f_log.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void turnNext() {
        //关老文件
        try {
            if (f_log != null) {
                push2File();
                f_log.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //开新文件
        try {
            f_log = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(logPath + "." + todayStr(), true), encode));
            //info("Current work path:" + new File(".").getAbsolutePath());
        } catch (Exception ex) {
            //ex.printStackTrace();
        }

    }

    private String todayStr() {
        int m = curc.get(Calendar.MONTH) + 1;
        int d = curc.get(Calendar.DAY_OF_MONTH);
        return new StringBuilder().append(curc.get(Calendar.YEAR)).append("-").
                append(m < 10 ? "0" + m : m).append("-").
                append(d < 10 ? "0" + d : d).
                toString();
    }

    private String formatTime() {
        Calendar cal = Calendar.getInstance();
        int m = cal.get(Calendar.MONTH) + 1;
        int d = cal.get(Calendar.DAY_OF_MONTH);
        int h = cal.get(Calendar.HOUR_OF_DAY);
        int M = cal.get(Calendar.MINUTE);
        int s = cal.get(Calendar.SECOND);
        return new StringBuilder().append(curc.get(Calendar.YEAR)).append("-").
                append(m < 10 ? "0" + m : m).append("-").
                append(d < 10 ? "0" + d : d).append(" ").
                append(h < 10 ? "0" + h : h).append(":").
                append(M < 10 ? "0" + M : M).append(":").
                append(s < 10 ? "0" + s : s).
                toString();
    }

    private void push2File() {
        try {
            if (f_log != null) {
                StringBuilder tmp = buf;
                buf = buf1;
                buf1 = tmp;
                f_log.append(buf1.toString());
                f_log.flush();
                buf1.setLength(0);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 输出LOG
     *
     * @param s
     */
    public final void info(String s) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatTime());
        sb.append(" ");
        sb.append(s);
        sb.append("\n");
        if (console) {
            System.out.print(sb.toString());
        }
        buf.append(sb);
    }

    /**
     *
     * @param s
     * @param e
     */
    public void error(String s, Exception e) {
        info(s + "\n" + warpException(e));
    }

    /**
     *
     * @param ex
     * @return
     */
    public static String warpException(Exception ex) {
        if (ex == null) {
            return "";
        }
        StringBuilder es = new StringBuilder();
        es.append(ex.toString()).append("\n");
        StackTraceElement[] st = ex.getStackTrace();
        for (StackTraceElement s : st) {
            es.append("\t ").append(s).append("\n");
        }
        return es.toString();
    }
}
