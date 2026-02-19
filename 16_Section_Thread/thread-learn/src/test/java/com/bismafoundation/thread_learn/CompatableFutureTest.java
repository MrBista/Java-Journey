package com.bismafoundation.thread_learn;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

public class CompatableFutureTest {


    CompletableFuture<String> basicCompatableFuture() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

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
}
