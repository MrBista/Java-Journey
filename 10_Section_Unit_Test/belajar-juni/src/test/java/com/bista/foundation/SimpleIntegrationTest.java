package com.bista.foundation;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

// commandnya mvn test -Dgroups=tag1,tag2,tag3...
@Tag("integration-test")
public class SimpleIntegrationTest {
    @Test
    public void integrationTest1() {
        System.out.println("Integration test simple");
    }

    @Test
    public void integrationTestSimple() {
        System.out.println("Integration test simple");
    }


    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    public void toTest() throws InterruptedException {
        Thread.sleep(10_000);
    }
}
