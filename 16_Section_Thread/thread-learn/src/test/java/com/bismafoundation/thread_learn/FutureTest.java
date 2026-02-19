package com.bismafoundation.thread_learn;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.AssertionErrors;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

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

    @Test
    void futureInvokeAllTest() throws InterruptedException, ExecutionException {
        // ini untuk paralel menjalankan di thread yg beda dnegan waktu yang sama atau paralel sehingga ga perlu tunggu yg lain

        ExecutorService executorService = Executors.newFixedThreadPool(2);


        Callable<Integer> futureCepat = () -> {
            Thread.sleep(500);
            return 23;
        };


        Callable<Integer> futureLambat = () -> {
            Thread.sleep(2_000);
            return 40;
        };

        Future<String> futureString = executorService.submit(() -> {
           Thread.sleep(500);
            return "String submited";
        });


        List<Callable<Integer>> callables = Arrays.asList(futureLambat, futureCepat);

        List<Future<Integer>> getAllFuture = executorService.invokeAll(callables);

        for(Future<Integer> future: getAllFuture) {
            System.out.println("Each future value: " + future.get());
        }

        executorService.shutdown();
        executorService.awaitTermination(2, TimeUnit.SECONDS);


    }


}
