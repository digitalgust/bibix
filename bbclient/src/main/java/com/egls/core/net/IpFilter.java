/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.egls.core.net;

/**
 *
 * @author gust
 */
public interface IpFilter {
    public boolean isValid(String ip);
    public boolean isGmValid(String ip);
}
