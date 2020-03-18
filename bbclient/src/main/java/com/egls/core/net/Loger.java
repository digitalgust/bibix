/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.core.net;

/**
 *
 * @author gust
 */
public interface Loger {

    public void info(String s);

    public void error(String s, Exception e);
}
