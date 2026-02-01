package com.bisma.foundation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDriverTest {

    @BeforeAll
    static void setupDriver() {
        try{
            Driver maDriver = new com.mysql.cj.jdbc.Driver();
            DriverManager.registerDriver(maDriver);

        } catch (SQLException e) {
            Assertions.fail(e);
        }
    }

    @Test
    void testConnection() {
        String jdbcUrl = "jdbc:mysql://localhost:9000/app_db";
        String username = "app_user";
        String password = "app_password";

        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

            System.out.println("Sukses membuat connection ke database");
            connection.close();
        } catch (SQLException e) {
            Assertions.fail(e);
        }

    }
}
