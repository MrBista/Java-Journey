package com.bismafoundation.thread_learn;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

public class CompatableFutureExceptionalyTest {
    ExecutorService executorService = Executors.newFixedThreadPool(2);

    @Test
    void testCompletableFutureExceptional() throws ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<String> fetchUserData = CompletableFuture
        .supplyAsync(() -> {
            if (Math.random() > 0) {
                throw new RuntimeException("Failed Fetch From Api");
            }
            return "Some Data From Api";
        }, executorService)
        .thenApplyAsync(String::toUpperCase)
        .exceptionally(throwable -> {
            System.out.println("Caused of error: " + throwable.getMessage());
            return "Data Default";
        });

        String result = fetchUserData.get(2, TimeUnit.SECONDS);

        System.out.println("Result: " + result);
        assertEquals("Data Default", result, "Fallback data default");

    }

    static String serviceACalled() throws InterruptedException {
        Thread.sleep(1_000);
        return "service-A";
    }


    static String serviceBCalled() throws InterruptedException {
        Thread.sleep(1_000);
        return "service-B";
    }

    @Test
    void testCompletableFutureHandle() throws ExecutionException, InterruptedException {

        CompletableFuture<String> futureSuksess = CompletableFuture.supplyAsync(() -> {
            try {
                return serviceACalled();
            } catch (InterruptedException e) {
                throw new CompletionException(e);
            }
        } , executorService)
        .handle((res, excp) -> {
            if (excp != null) {
                return "fallback error karena: " + excp.getMessage();
            }

            return res.toUpperCase();
        });

        String result = futureSuksess.get();
        System.out.println("Result: " + result);
        assertEquals("SERVICE-A",result);
    }
}
