/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.core.net;


/**
 *
 * @author gust
 */
public interface PkgProtocol {

    /**
     * 发送
     */
    public void sndAndEncode()throws Exception;

    /**
     * 接收数据
     * @throws java.lang.Exception
     */
    public void rcvAndDecode()throws Exception;

    /**
     *
     * @param conn
     */
    public void setNetConn(Session conn);

    /**
     * 设最大包长度
     * @param size
     */
    public void setMaxPkgSize(int size);

    /**
     * 取最大包长度
     * @return
     */
    public int getMaxPkgSize();
}
