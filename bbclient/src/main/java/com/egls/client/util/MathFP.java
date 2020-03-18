/*

MathFP.java 2007 Jan 11.

EGL is a pure java 3D Graphics API which was designed for J2ME mobile devices
and based on CLDC 1.0 only, which provided OpenGL-like interface and supports
basic 3D pipeline and texture.

Copyright (C) 2007 Jiayi Wu (wujiyish@msn.com).

This program is free software; you can redistribute it and/or modify it under
the terms of the GNU General Public License as published by the Free Software
Foundation.

This program is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with
this program; if not, write to the Free Software Foundation, Inc., 51 Franklin
Street, Fifth Floor, Boston, MA 02110-1301 USA.

 */

/* MathFP - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.egls.client.util;

/**
Fixed point class which use 16bit as precision.
 */
public class MathFP {

    public static final int EGL_PRECISION = 16;// number of fractional bits
    public static final int EGL_ONE = (1 << EGL_PRECISION);// representation of 1



    /**
    Convert FP to int
    @param i FP to be converted
     */
    public static int toInt(int i) {

        return i >> EGL_PRECISION;
    }

    /**
    Convert int to FP
    @param i int to be converted
     */
    public static int toFP(int i) {
        return i << EGL_PRECISION;
    }

    /**
    Get the String of the FP
    @param i FP
     */
    public static String toString(int i) {
        return toInt(i) + "";//TODO
    }

    /**
    Return a*b
     */
    public final static int mul(long a, long b) {
        return (int) ((a * b) >> EGL_PRECISION);
    }

    /**
    Return a/b
     */
    public final static long div(long a, long b) {
        return ((a << EGL_PRECISION) / b);
    }


    /**
    Return sqrt(a)
     */
    public final static long sqrt(long a) {
        long s;
        int i;
        s = (a + EGL_ONE) >> 1;
        for (i = 0; i < 6; i++) {
            s = (s + div(a, s)) >> 1;
        }
        return s;
    }

//    public final static int sqrt(long a) {
//        return sqrt((int) a);
//    }
  

    public final static int sinx(int a){
        while(a < 0){
            a+= 360;
        }
        a %= 360;
        return sinTable[a];
    }
    public final static int cosx(int a){
        a +=90;
        while(a < 0){
            a+= 360;
        }
        a %= 360;
        return sinTable[a];
    }

  
    private static final int[] sinTable = {
        0,
        1143,
        2287,
        3429,
        4571,
        5711,
        6850,
        7986,
        9120,
        10252,
        11380,
        12504,
        13625,
        14742,
        15854,
        16961,
        18064,
        19160,
        20251,
        21336,
        22414,
        23486,
        24550,
        25606,
        26655,
        27696,
        28729,
        29752,
        30767,
        31772,
        32768,
        33753,
        34728,
        35693,
        36647,
        37589,
        38521,
        39440,
        40347,
        41243,
        42125,
        42995,
        43852,
        44695,
        45525,
        46340,
        47142,
        47929,
        48702,
        49460,
        50203,
        50931,
        51643,
        52339,
        53019,
        53683,
        54331,
        54963,
        55577,
        56175,
        56755,
        57319,
        57864,
        58393,
        58903,
        59395,
        59870,
        60326,
        60763,
        61183,
        61583,
        61965,
        62328,
        62672,
        62997,
        63302,
        63589,
        63856,
        64103,
        64331,
        64540,
        64729,
        64898,
        65047,
        65176,
        65286,
        65376,
        65446,
        65496,
        65526,
        65536,
        65526,
        65496,
        65446,
        65376,
        65286,
        65176,
        65047,
        64898,
        64729,
        64540,
        64331,
        64103,
        63856,
        63589,
        63302,
        62997,
        62672,
        62328,
        61965,
        61583,
        61183,
        60763,
        60326,
        59870,
        59395,
        58903,
        58393,
        57864,
        57319,
        56755,
        56175,
        55577,
        54963,
        54331,
        53683,
        53019,
        52339,
        51643,
        50931,
        50203,
        49460,
        48702,
        47929,
        47142,
        46340,
        45525,
        44695,
        43852,
        42995,
        42125,
        41243,
        40347,
        39440,
        38521,
        37589,
        36647,
        35693,
        34728,
        33753,
        32768,
        31772,
        30767,
        29752,
        28729,
        27696,
        26655,
        25606,
        24550,
        23486,
        22414,
        21336,
        20251,
        19160,
        18064,
        16961,
        15854,
        14742,
        13625,
        12504,
        11380,
        10252,
        9120,
        7986,
        6850,
        5711,
        4571,
        3429,
        2287,
        1143,
        0,
        -1143,
        -2287,
        -3429,
        -4571,
        -5711,
        -6850,
        -7986,
        -9120,
        -10252,
        -11380,
        -12504,
        -13625,
        -14742,
        -15854,
        -16961,
        -18064,
        -19160,
        -20251,
        -21336,
        -22414,
        -23486,
        -24550,
        -25606,
        -26655,
        -27696,
        -28729,
        -29752,
        -30767,
        -31772,
        -32767,
        -33753,
        -34728,
        -35693,
        -36647,
        -37589,
        -38521,
        -39440,
        -40347,
        -41243,
        -42125,
        -42995,
        -43852,
        -44695,
        -45525,
        -46340,
        -47142,
        -47929,
        -48702,
        -49460,
        -50203,
        -50931,
        -51643,
        -52339,
        -53019,
        -53683,
        -54331,
        -54963,
        -55577,
        -56175,
        -56755,
        -57319,
        -57864,
        -58393,
        -58903,
        -59395,
        -59870,
        -60326,
        -60763,
        -61183,
        -61583,
        -61965,
        -62328,
        -62672,
        -62997,
        -63302,
        -63589,
        -63856,
        -64103,
        -64331,
        -64540,
        -64729,
        -64898,
        -65047,
        -65176,
        -65286,
        -65376,
        -65446,
        -65496,
        -65526,
        -65536,
        -65526,
        -65496,
        -65446,
        -65376,
        -65286,
        -65176,
        -65047,
        -64898,
        -64729,
        -64540,
        -64331,
        -64103,
        -63856,
        -63589,
        -63302,
        -62997,
        -62672,
        -62328,
        -61965,
        -61583,
        -61183,
        -60763,
        -60326,
        -59870,
        -59395,
        -58903,
        -58393,
        -57864,
        -57319,
        -56755,
        -56175,
        -55577,
        -54963,
        -54331,
        -53683,
        -53019,
        -52339,
        -51643,
        -50931,
        -50203,
        -49460,
        -48702,
        -47929,
        -47142,
        -46340,
        -45525,
        -44695,
        -43852,
        -42995,
        -42125,
        -41243,
        -40347,
        -39440,
        -38521,
        -37589,
        -36647,
        -35693,
        -34728,
        -33753,
        -32768,
        -31772,
        -30767,
        -29752,
        -28729,
        -27696,
        -26655,
        -25606,
        -24550,
        -23486,
        -22414,
        -21336,
        -20251,
        -19160,
        -18064,
        -16961,
        -15854,
        -14742,
        -13625,
        -12504,
        -11380,
        -10252,
        -9120,
        -7986,
        -6850,
        -5711,
        -4571,
        -3429,
        -2287,
        -1143,
        0
    };
}
