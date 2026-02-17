package com.bismafoundation.thread_learn;

public class Counter {
    private Long count = 0L;


    public synchronized void increment() {
        count ++; // ini ada 3 operasi di level cpu
    }

    public synchronized Long getCount() {
        return count;
    }
}
