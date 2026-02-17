package com.bismafoundation.thread_learn;

import org.junit.jupiter.api.Test;

public class RaceConditionOnCuncurent {


    @Test
    void testCounter() throws InterruptedException {
        Counter counter = new Counter();

        Runnable runnable = () -> {
            for (int i = 0; i < 1_000_000; i ++) {
                counter.increment();
            }
        };

        Thread thread1 = new Thread(runnable);
        Thread thread2 = new Thread(runnable);
        Thread thread3 = new Thread(runnable);

        thread1.start();
        thread2.start();
        thread3.start();

        thread1.join();
        thread2.join();
        thread3.join();

        System.out.println("jumlah counter: " + counter.getCount());
    }


    @Test
    void testCounterSyncStatment() throws InterruptedException {
        SynchronizeCounter counterSync = new SynchronizeCounter();

        Runnable runnable = () -> {
            for (int i = 0; i < 1_000; i ++) {
                counterSync.increment();
            }
        };

        Thread thread1 = new Thread(runnable);
        Thread thread2 = new Thread(runnable);
        Thread thread3 = new Thread(runnable);


        thread1.start();
        thread2.start();
        thread3.start();

        thread1.join();
        thread2.join();
        thread3.join();


        System.out.println("Curent counter : " + counterSync.getCounter());




    }


}
