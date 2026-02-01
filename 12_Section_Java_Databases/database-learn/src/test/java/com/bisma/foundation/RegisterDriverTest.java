package com.bisma.foundation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class RegisterDriverTest {

    @Test
    void testRegisterDriver() {
        /*
            jadi jdbc itu memerlukan driver untuk koneksi ke database,
            setiap rdms itu memiliki driver nya masing-masing,
            driver itu basicly kaya penghubung yang mana driver ini hanya merupakan interface
            untuk implementasinya kita bisa install masing-masing sesuai rdms yg mau kita pakai, contoh kali ini pakai mysql
         */
        try {
            Driver  mysqlDriver = new com.mysql.cj.jdbc.Driver();
            DriverManager.registerDriver(mysqlDriver);
        }catch (SQLException e) {
            Assertions.fail(e);
        }
    }
}
