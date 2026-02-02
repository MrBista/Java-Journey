package com.bisma.foundation;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BatchProsesTest {

    @Test
    void testBatchInsertPrepartstament() throws SQLException {
        String sqlInsertUser = """
                insert into users(username, password, name) values(?, ?, ?);
                """;

        Connection connection = ConnectionUtil.getDataSource().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sqlInsertUser);

        for (int i = 0; i < 1000; i ++ ) {
            int numNow = i + 1;
            preparedStatement.clearParameters();
            preparedStatement.setString(1,"MrBista2308-" + numNow);
            preparedStatement.setString(2, "Abc@1234");
            preparedStatement.setString(3, "GustiBismanTaka");
            preparedStatement.addBatch();

        }
        preparedStatement.executeBatch();

        preparedStatement.close();
        connection.close();
    }

    @Test
    void testGetAllUser() throws SQLException {
        String sqlSelect = """
                select username, name, password from users
                """;

        Connection connection = ConnectionUtil.getDataSource().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sqlSelect);

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            String username = resultSet.getString("username");
            String name = resultSet.getString("name");
            String password = resultSet.getString("password");

            System.out.println("username : " + username + " name: " + name + "password: " + password);
        }

        preparedStatement.close();
        connection.close();
    }
}
