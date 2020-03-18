package com.egls.core.net;

import com.egls.core.net.impl.Client;
import java.io.IOException;
import java.util.Queue;

/**
 * 网络传输接口
 * 
 * @author 
 * 
 */
public interface Session{

    /**
     * 接收数据
     * 
     * @return if there is any data, return a byte array,else return null
     */
    public byte[] receive();

    /**
     * 发送数据，将要发送的数据将根据压缩设置自动进行压缩
     * 
     * @param buf
     */

    public void send(DataPackage pkg);


    /**
     * 发送数据，强制不进行压缩
     */

     void sendWithoutCompress(DataPackage pkg);

    /**
     * 关闭连接
     * 
     */
    public void close();

    /**
     * 是否已关闭
     * @return
     */
    public boolean isClosed();
    /**
     * 返回协议
     * @return
     */
    public PkgProtocol getPkgProtocol();

    /**
     *
     * @return
     */
    public Client getClient();

    /**
     * @return the rpool
     */
    public Queue<byte[]> getRpool() ;

    /**
     * @return the spool
     */
    public Queue<byte[]> getSpool() ;
    
    public boolean checkValid() throws IOException;
}
