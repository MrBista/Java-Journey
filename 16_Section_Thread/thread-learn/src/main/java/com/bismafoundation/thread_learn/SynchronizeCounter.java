package com.bismafoundation.thread_learn;

public class SynchronizeCounter {
    private Long counter = 0L;


    public void increment() {
        synchronized (this) {
            counter ++;
        }
        // disini code yang ga perlu menunggu atau bisa jalan di berbagai thread yang berbeda
        System.out.println("Runing in different thread: " + Thread.currentThread().getName());
    }

    public Long getCounter() {
        return counter;
    }
}
