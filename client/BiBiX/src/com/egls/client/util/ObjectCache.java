/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egls.client.util;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 以E为键的桶形缓存
 *
 * @author gust
 */
public class ObjectCache<E> {
//
//    Map<E, Queue<T>> map = new ConcurrentHashMap();
//
//    public ObjectCache() {
//    }
//
//    public T get(E e) {
//        Queue<T> set = getBucket(e);
//        T t = set.poll();
//        return t;
//    }
//
//    public void put(E e, T o) {
//        Queue<T> set = getBucket(e);
//
//        set.add(o);
//    }
//
//    private Queue<T> getBucket(E e) {
//        Queue<T> set = map.get(e);
//        if (set == null) {
//            set = new LinkedList<T>();
//            map.put(e, set);
//        }
//        return set;
//    }

    Queue<E> coll = new LinkedList<E>();

    synchronized public void put(E e) {
        coll.add(e);
    }

    synchronized public E get() {
        return coll.poll();
    }
}
