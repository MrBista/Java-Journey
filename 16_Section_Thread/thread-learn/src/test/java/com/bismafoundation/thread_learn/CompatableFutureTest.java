package com.bismafoundation.thread_learn;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
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


    static String fetchUser() throws InterruptedException {
        Thread.sleep(5_00);
        return "user-123";
    }

    static String fetchUsername(String userId) throws InterruptedException {
        Thread.sleep(5_00);
        return "Bisboy with user id=(" + userId + ")";
    }

    static String fetchEmail(String username) {
        return username.toLowerCase().replace(" ", ".") + "@mail.com";
    }





    @Test
    void testCompletableFutureChaining() throws ExecutionException, InterruptedException {
        CompletableFuture<String> getUser = CompletableFuture.supplyAsync(() -> {
            try{
                return fetchUser();
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executorService);

       String user =  getUser.get();
       Assertions.assertEquals("user-123", user);


       CompletableFuture<String> getUserChain = CompletableFuture
           .supplyAsync(() -> {
                try {
                    return fetchUser();
                }catch (Exception e) {
                    throw new CompletionException(e);
                }
           }, executorService)
           .thenApplyAsync((userId)-> {
               try{
                   return fetchUsername(userId);

               } catch (Exception e) {
                   throw new CompletionException(e);
               }
           }, executorService);

       String getUserChainName = getUserChain.get();
        System.out.println("result user chain: " + getUserChainName);
        Assertions.assertEquals("Bisboy with user id=(user-123)", getUserChainName);


    }



    @Test
    void testCompletableFutureChainingImediateResultAndTo() throws ExecutionException, InterruptedException, TimeoutException {
        long start = System.currentTimeMillis();
         String result = CompletableFuture.supplyAsync(() -> {
            try{
                return fetchUser();
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executorService)
         .thenApplyAsync((userDetail) -> {
             try {
                 return fetchUsername(userDetail);
             }catch (Exception e) {
                 throw new CompletionException(e);
             }
         })
         .thenApplyAsync((userNameDetail) -> {
             try{
                 return fetchEmail(userNameDetail);

             }catch (Exception e) {
                 throw new CompletionException(e);
             }
         })
         .get(5, TimeUnit.SECONDS);


         long finish = System.currentTimeMillis() - start;

        System.out.println("Berjalan selama: " + finish + " millisecond");
        System.out.println("result: " + result);
        assertTrue(result.contains("@mail.com"), "Harus berisi kan @mail.com");
        assertTrue(finish < 5000, "harus selesai timeout");
    }


    CompletableFuture<String> fetchAsync(String input)  {
        return CompletableFuture.supplyAsync(() -> "result-of-" + input, executorService);
    }

    @Test
    void testCompletableFutureThenCompose() throws ExecutionException, InterruptedException {
        // kalau pakai thenApply/thenApplyAsync ketika digunakan di function yg mengembalikan complateablefuture dia akan nested
        // lebih baik pakai thenCompose/thenComposeAsync

        CompletableFuture<CompletableFuture<String>> getUserAsync = CompletableFuture.supplyAsync(() -> {
            return "input";
        }, executorService)
        .thenApplyAsync(this::fetchAsync);

        // unuk get nya harus get dua kali karena nested
        // get yang pertama yg didalam(yang dari function fetchAsync) get kedua baru resultnya
        System.out.println("Result user async: " + getUserAsync.get().get());


        // sebaiknya pakai .thenCompose / .thenComposeAsync
        // dia akan membuatnya jadi flat jadi ga perlu nested
        CompletableFuture<String> getUserAsyncCompose = CompletableFuture
                .supplyAsync(() -> "input2", executorService)
                .thenComposeAsync(this::fetchAsync);

        System.out.println("Result terbaru: " + getUserAsyncCompose.get());
    }






}
