/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.client.chat;

import com.egls.client.netmgr.CmdPkg;

/**
 *
 * @author Gust
 */
public interface CmdCallback {

    void onBack(CmdPkg cmd);
}
