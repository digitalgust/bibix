package com.egls.client.netmgr;

import java.util.LinkedList;

public class SyncNetQueue<E> extends LinkedList<E> {
    /**
     * 
     */
    private static final long serialVersionUID = -8253955107724786414L;

    @Override
    public synchronized boolean add(E e) {
        return super.add(e);
    }

    @Override
    public synchronized E poll() {
        return super.poll();
    }

    @Override
    public synchronized int size() {
        return super.size();
    }

    @Override
    public synchronized boolean isEmpty() {
        return super.isEmpty();
    }
}
