/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.client.util;

import com.egls.client.netmgr.CmdPkg;

/**
 *
 * @author gust
 */
public interface NetCmdHandler {

    public void processCmd(CmdPkg cmd);
}
