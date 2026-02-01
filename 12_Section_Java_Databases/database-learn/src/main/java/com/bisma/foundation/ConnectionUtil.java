package com.bisma.foundation;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;

public class ConnectionUtil {

    private static HikariDataSource dataSource;

    static  {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setJdbcUrl("jdbc:mysql://localhost:9000/app_db");
        config.setUsername("app_user");
        config.setPassword("app_password");

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setIdleTimeout(60_000);
        config.setMaxLifetime(10 * 60_000);


        try {
            dataSource = new HikariDataSource(config);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static HikariDataSource getDataSource() {
        return dataSource;
    }
}
