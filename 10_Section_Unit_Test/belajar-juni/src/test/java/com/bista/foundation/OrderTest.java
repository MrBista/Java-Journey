package com.bista.foundation;

import org.junit.jupiter.api.*;


@TestInstance(value = TestInstance.Lifecycle.PER_CLASS) // default nya permethod , kalau permethod artinya setiap @Test akan dibuat baru test
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class) // digunakan untuk @Order supaya jalan
public class OrderTest {
    private int count = 0;

    @Test
    @Order(1)
    public void test2() {
        count ++;
        System.out.println("hai ini order 1 - " + count);
    }

    @Test
    @Order(2)
    public void test1() {
        count ++;
        System.out.println("hai ini order 2 - " + count);
    }

    // dengan perclass maka ga perlu buat static untuk beforeall dan afterall
    @BeforeAll
    public void sebelumSemuaTerjadi() {
        System.out.println("count init " + count);
    }

    @AfterAll
    public void setelahSemuaTerjadi() {
        System.out.println("count all - " + count );
    }
}
