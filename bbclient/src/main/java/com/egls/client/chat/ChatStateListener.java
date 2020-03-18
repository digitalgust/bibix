/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.client.chat;

import com.egls.client.chat.bean.ChatGroupInfo;
import com.egls.client.chat.bean.MemberInfo;

/**
 * for monitor chat state change
 *
 * @author Gust
 */
public interface ChatStateListener {

    void onGroupAdd(ChatGroupInfo sd, long lastMsgAt);

    void onGroupRemove(ChatGroupInfo sd);

    void onGroupUpdate(ChatGroupInfo sd);

    void onGroupDetail(ChatGroupInfo sd);

    void onFriendAdd(MemberInfo mi, long lastMsgAt);

    void onFriendRequest(long friendid, String nick);

    void onFriendRemove(MemberInfo mi);

    void onFriendUpdated(MemberInfo mi);

    void onReceiveMsg(MsgItem mi);

    void onReceiveAttachment(String resourceid, byte[] attachment);

    void OnReceiveHead(long roleid);
}
