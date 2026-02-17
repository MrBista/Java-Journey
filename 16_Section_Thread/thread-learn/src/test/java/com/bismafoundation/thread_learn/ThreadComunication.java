package com.bismafoundation.thread_learn;

import org.junit.jupiter.api.Test;

public class ThreadComunication {

    private String message;

    private final Object lock = new Object();

    @Test
    void testNotifiyAndWait() throws InterruptedException {

        Thread threadSender = new Thread(() -> {
            synchronized (lock) {
                message = "Hai I send this message to u mf";
                lock.notify();
            }
        });

        Thread threadReciver = new Thread(() -> {
            synchronized (lock) {
                try {
                    lock.wait();
                    System.out.println(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
//                    throw new RuntimeException(e);
                }
            }
        });

        threadReciver.start();
        threadSender.start();

        threadReciver.join();
        threadSender.join();

        // intinya yang .wait() dijalankan dulu, supaya ga deadlock


    }
}
