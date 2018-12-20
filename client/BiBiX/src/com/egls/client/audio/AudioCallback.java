/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.client.audio;

/**
 *
 * @author Gust
 */
public interface AudioCallback {

    void onCapture(int millSecond, byte[] data);
    
    void onPlay(int millSecond, byte[] data);

    void onStop();
}
