package com.bismafoundation.thread_learn;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompletableFutureParalel {
    ExecutorService executorService = Executors.newFixedThreadPool(3);

    static String serviceA() throws InterruptedException {
        Thread.sleep(1_000);
        return "service-A";
    }

    static String serviceB() throws InterruptedException {
        Thread.sleep(1_000);
        return "service-B";
    }
    static String serviceC() throws InterruptedException {
        Thread.sleep(1_000);
        return "service-C";
    }

    @Test
    void testParalelCompletableFuture() throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();
        CompletableFuture<String> futureA = CompletableFuture.supplyAsync(() -> {
            try {
                return serviceA();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, executorService);

        CompletableFuture<String> futureB = CompletableFuture.supplyAsync(() -> {
            try {
                return serviceB();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, executorService);

        CompletableFuture<String> futureC = CompletableFuture.supplyAsync(() -> {
            try {
                return serviceC();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, executorService);


        CompletableFuture.allOf(futureA, futureB, futureC);

        String hasilA = futureA.get();
        String hasilB = futureB.get();
        String hasilC = futureC.get();

        long durasi = System.currentTimeMillis() - start;

        System.out.printf("Parallel: %s, %s, %s — durasi: %dms%n", hasilA, hasilB, hasilC, durasi);
    }
}
