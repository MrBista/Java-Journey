package com.bismafoundation.thread_learn;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolTest {

    @Test
    void testCreateThreadPoolManual() throws InterruptedException {
        int minThread = 10;
        int maxThread = 100;
        int alive = 1;
        TimeUnit time = TimeUnit.MINUTES;
        ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(100);

        var executor = new ThreadPoolExecutor(minThread, maxThread, alive, time, queue);
        executor.execute(() -> {
            try {
                Thread.sleep(5000);
                System.out.println("Hello from Threadpool : " + Thread.currentThread().getName());

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.DAYS);

    }

    private static class LogRejectedExecutionHandler implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {
            System.out.println("Task : " + runnable.toString() + " is rejected");
        }
    }

    @Test
    void testCreateThreadPoolManualWithTaskRejected() throws InterruptedException {
        int minThread = 10;
        int maxThread = 100;
        int alive = 1;
        TimeUnit time = TimeUnit.MINUTES;
        ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(100);
        var logHandler = new ThreadPoolTest.LogRejectedExecutionHandler();
        var executor = new ThreadPoolExecutor(minThread, maxThread, alive, time, queue, logHandler);
        executor.execute(() -> {
            try {
                Thread.sleep(5000);
                System.out.println("Hello from Threadpool : " + Thread.currentThread().getName());

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.DAYS);

    }

}
