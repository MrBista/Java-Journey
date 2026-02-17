package com.bismafoundation.thread_learn;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceTest {

        @Test
        void testSingleThreadExecutor() throws InterruptedException {
            ExecutorService executor = Executors.newSingleThreadExecutor();

            for (int i = 0; i < 100; i ++) {
                int finalI = i;
                executor.execute(() -> {
                    try {
                        Thread.sleep(100);
                        System.out.println("Hallo dunia ke : " + finalI + " with thread pool: " + Thread.currentThread().getName());
                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
                        e.printStackTrace();
                    }

                });
            }

            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.MINUTES);
        }

    @Test
    void testNThreadExecutor() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(5);

        for (int i = 0; i < 100; i ++) {
            int finalI = i;
            executor.execute(() -> {
                try {
                    Thread.sleep(100);
                    System.out.println("Hallo dunia ke : " + finalI + " with thread pool: " + Thread.currentThread().getName());
                } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
                    e.printStackTrace();
                }

            });
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.MINUTES);
    }


}
