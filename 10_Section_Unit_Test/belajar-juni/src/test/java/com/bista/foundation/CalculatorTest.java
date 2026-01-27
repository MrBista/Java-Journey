package com.bista.foundation;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

import org.junit.jupiter.api.*;
import org.opentest4j.TestAbortedException;

@DisplayName("Test Calculator")
public class CalculatorTest {
    Calcalutor calcalutor = new Calcalutor(); // composistion

    @Test // anotation tuk menunjukan unit test
    @DisplayName("Test Function Calculator.add(int, int)")
    public void testAddSuccess() {
        int val = calcalutor.add(1, 3);
        assertEquals(4,val);
    }

    @Test
    public void testAddBigAmount() {
        int val = calcalutor.add(100_000, 100_000);
        assertEquals(200_000, val);

    }


    // assert devide by zero
    @Test
    public void testDevidedWithZero() {
        assertThrows(IllegalArgumentException.class, () -> {
            System.out.println("unit test");
            calcalutor.divided(3, 0);
        });
    }


    @Test
    @Disabled // tuk mendisbled method test case
    public void testCommintSoon() {

    }


    @BeforeEach // akan jalan setiap sebelum @Test jalan
    public void setUp() {
        System.out.println("Before unit test");
    }

    @AfterEach // akan jalan setiap @Test selesai di call
    public void tearDown() {
        System.out.println("After unit test");
    }


    @BeforeAll // akan dijalankan sekali sebelum semua unit test jalan
    public static void beforeAll() {
        System.out.println("Before all runing");
    }


    @AfterAll
    public static void afterAll() {
        System.out.println("After all runing");
    }

    @Test
    public void testAborted() {
        // throw new TestAbortedException(); digunakan untuk membatalakan menjalankan test case yang sekarang sedang berjalan
        String envValue = "PRODUCTION";

        if (envValue.equals("PRODUCTION"))  {
            throw new TestAbortedException("Ga berlaku tuk production");
        }
    }

    @Test
    public void testAssumption() throws Exception {
        // versi pendek dari TestAbortedException
        // akan jalan jika value tidak sama yg nanti akan membatalkan unit testnya
        assumeTrue(false, "berhenti");
        throw new Exception("Salah geng");

    }
}
