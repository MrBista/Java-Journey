package com.bisma.foundation;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PrepareStatmentTest {

    @Test
    void testInsertPrepareStatment() throws SQLException {
        String sqlInsertUser = """
                insert into users(username, password, name) values(?, ?, ?);
                """;

        Connection connection = ConnectionUtil.getDataSource().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sqlInsertUser);

        preparedStatement.setString(1, "MrBistaGagah2308");
        preparedStatement.setString(2, "Abc@1234");
        preparedStatement.setString(3, "Gusti Bisman Taka");

        preparedStatement.executeUpdate();

        preparedStatement.close();
        connection.close();
    }

    @Test
    void testSelectAllPreapareStatment() throws SQLException {
        String sqlSelect = """
                select username, name, password from users where username = ?
                """;

        Connection connection = ConnectionUtil.getDataSource().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sqlSelect);

        preparedStatement.setString(1, "MrBistaGagah2308");

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            String username = resultSet.getString("username");
            String name = resultSet.getString("name");
            String password = resultSet.getString("password");

            System.out.println("username : " + username + " name: " + name + "password: " + password);
        }


    }
}
