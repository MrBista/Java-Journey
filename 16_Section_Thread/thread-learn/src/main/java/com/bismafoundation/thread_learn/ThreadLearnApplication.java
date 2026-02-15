package com.bismafoundation.thread_learn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

public class ThreadLearnApplication {

	public static void main(String[] args){
		System.out.println("Hello thread");

		Thread thread1 = new Thread(() -> {
			System.out.println("Some proses thread: " + Thread.currentThread().getName());
		});

		thread1.setDaemon(true); // untuk menjalankan thread dibelakang background
		thread1.start();

	}

}
