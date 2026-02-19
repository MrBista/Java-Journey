package com.bismafoundation.thread_learn;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.AssertionErrors;

import java.util.concurrent.*;

public class FutureTest {

    @Test
    void basicCallableTest() throws ExecutionException, InterruptedException {
        Callable<String> getNameCallable = () -> {
            Thread.sleep(5_000);
            return "Bisma Bratha";
        };

        ExecutorService executors = Executors.newFixedThreadPool(2);
        Future<String> future = executors.submit(getNameCallable);

        String resultFuture = future.get();
        System.out.println("Result Future: "+ resultFuture);

        executors.shutdown();
    }

    @Test
    void cancelCallableTest() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);


        Future<String> futureRes = executorService.submit(() -> {
            return "Callable mengembalikan future";
        });

        Thread.sleep(2_000);
        futureRes.cancel(true);
        System.out.println(futureRes.get());

    }

    @Test
    void futureTimeoutTest() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);


        Future<Integer> futureCepat = executorService.submit(() -> {
            Thread.sleep(500);
            return 23;
        });


        Future<Integer> futureLambat = executorService.submit(() -> {
            Thread.sleep(2_000);
            return 40;
        });


        try {
            Integer val = futureCepat.get(1, TimeUnit.SECONDS);
            Assertions.assertEquals(23, val);
        }catch (ExecutionException | InterruptedException | TimeoutException e) {
            System.out.println("Task normal kok kena timeout harusnya nggak karena hanya 500 ms, sedangkan to 1 detik");
            AssertionErrors.fail("Unexpected to happend");
        }

        try {
            Integer val = futureLambat.get(1, TimeUnit.SECONDS);
            AssertionErrors.fail("Harusnya timeout karena operasi berjalan 2 detik");
        }catch (ExecutionException | InterruptedException | TimeoutException e) {
            futureLambat.cancel(true);
            Assertions.assertTrue(futureLambat.isCancelled());
            System.out.println("Kena timeout dia dengan message: " + e.getMessage());
        }



        executorService.shutdown();
        executorService.awaitTermination(2, TimeUnit.SECONDS);
    }

}
