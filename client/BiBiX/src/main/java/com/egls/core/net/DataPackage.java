/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.core.net;

/**
 *
 * @author Gust
 */
public interface DataPackage {

    public byte[] toByteArray();
    
    public void setByteArray(byte[] buf);
    
    void reset();
    
    public void setCmdid(short cmdid);
}
