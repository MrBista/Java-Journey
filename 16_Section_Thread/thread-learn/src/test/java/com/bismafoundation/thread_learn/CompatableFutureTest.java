package com.bismafoundation.thread_learn;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

public class CompatableFutureTest {
    ExecutorService executorService = Executors.newFixedThreadPool(2);


    CompletableFuture<String> basicCompatableFuture() throws InterruptedException {

        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            return "C Cumber isi";
        }, executorService);
        executorService.shutdown();
        executorService.awaitTermination(3, TimeUnit.SECONDS);
        return future;


    }

    @Test
    void basicCompatableFutureTest() throws InterruptedException, ExecutionException {
        String resultComplateableFuture = basicCompatableFuture().get();

        Assertions.assertEquals("C Cumber isi", resultComplateableFuture);
    }

    public Future<String> getValue() {
        CompletableFuture<String> future = new CompletableFuture<>();

        executorService.submit(() -> {
            try {
                Thread.sleep(2_000);
                future.complete("Success");

            } catch (InterruptedException e) {
                future.completeExceptionally(e);
            };
        });

        return future;
    }

    public Future<String> getValueExceptaion() {
        CompletableFuture<String> future = new CompletableFuture<>();

        executorService.submit(() -> {
            try {
                Thread.sleep(2_000);
                throw new InterruptedException("interupted error exception");

            } catch (InterruptedException e) {
                future.completeExceptionally(e);
            };
        });

        return future;
    }

    @Test
    void testGetValueCompleteableFuture() throws ExecutionException, InterruptedException {
        String result = getValue().get();

        Assertions.assertEquals("Success", result);
    }
    @Test
    void testGetValueExceptionCompleteableFuture() throws ExecutionException, InterruptedException {
        String result = getValueExceptaion().get();

        System.out.println(result);
    }


}
