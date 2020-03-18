package com.egls.core.net;

import com.egls.core.net.impl.Client;

/**
 * 网络监听器

 *
 * @author gust
 */
public interface AcceptedHandler {

    /**
     * 当第一次接收accept到一个客户端时，周用监听器
     *
     * @param client
     */
    public void onAccept(Client client);
}
