package com.bismafoundation.thread_learn;

import org.junit.jupiter.api.Test;

public class ThreadBasicLearnTest {


    @Test
    void mainThread() {
        String threadName = Thread.currentThread().getName();
        System.out.println(threadName);
    }

    @Test
    void runableThread() {
        Runnable runnable = () -> {
            System.out.println("Hello Runable that run in thread: "+ Thread.currentThread().getName());

        };

        Thread threadNew = new Thread(runnable);

        threadNew.start();
    }

    @Test
    void testThreadConcruentRun() {
        Thread threadNew = new Thread(() -> System.out.println("Runable A"));
        Thread threadNew2 = new Thread(() -> System.out.println("Ruanble B"));
        Thread threadNew3 = new Thread(() -> System.out.println("Runable C"));

        // thread itu concruent task nya tidak nentu dikerjakan yang mana dulu
        threadNew.start();
        threadNew2.start();
        threadNew3.start();
    }

    @Test
    void threadSleep() throws InterruptedException {
        Runnable runnable = () -> {
            try {
                Thread.sleep(2_000);
                System.out.println("Hello from thread: " + Thread.currentThread().getName());
            } catch (InterruptedException e) {
//                throw new RuntimeException(e);

                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        };

        var thread = new Thread(runnable);
        thread.start();
        Thread.sleep(3_000);
    }

    @Test
    void threadJoinTest() throws InterruptedException {
        Runnable runnable = () -> {
            try {
                // some long proses
                Thread.sleep(2_000);
                System.out.println("Calling runable on thread: " + Thread.currentThread().getName());
            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
                e.printStackTrace();
                Thread.currentThread().interrupt();
                return;
            }
        };

        Thread thread = new Thread(runnable);
        System.out.println("Some proses here");
        thread.start();
        thread.join(); // join akan menunggu runable/ thread selesai menjalankan tasknya selesai
        System.out.println("Some proses also here");
        // join lebih op daripada sleep, karena kalau prosess busnis logicnya banyak kita ga tau akan berapa lama selesai
    }

    @Test
    void threadJoinProblem() throws InterruptedException{
        Runnable runnable = () -> {
            try {
                // some long proses
                Thread.sleep(10_000);
                System.out.println("Calling runable on thread: " + Thread.currentThread().getName());
            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
                e.printStackTrace();
                Thread.currentThread().interrupt();
                return;
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
        thread.join(5_000); // menambahkan timeout ketika thread hang maka kita kirim interupt tuk hentikan proses
        if (thread.isAlive()) {
            System.out.println("Thread masih runing selama 5 detik");
            thread.interrupt();
        }
    }

    @Test
    void threadInterputedTest() throws InterruptedException {
        Runnable runnable = () -> {
            while (!Thread.currentThread().isInterrupted()) { // tuk cek apakah thread di interupted tuk dihentikan
                System.out.println("Some Hard work that take time");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            System.out.println("Hello world gengs");
        };


        Thread thread1 = new Thread(runnable);
        thread1.start();

        System.out.println("Before Thread Run There is Program that run");
        Thread.sleep(10_000);

        // karena ga ada yg interput, program akan hang


        // setelah 10 detik interput tuk hentikan proses thread
        thread1.interrupt();


        thread1.join();


        System.out.println("Last one gengs");
    }
}
