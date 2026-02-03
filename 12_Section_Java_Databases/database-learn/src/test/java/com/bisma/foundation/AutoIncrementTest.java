package com.bisma.foundation;

import org.junit.jupiter.api.Test;

import java.sql.*;

public class AutoIncrementTest {

    @Test
    void testAutoIncrementGetValueId() throws SQLException {
        Connection connection = ConnectionUtil.getDataSource().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO users (username, password, name) values(?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, "MrBoyBista2308");
        preparedStatement.setString(2, "BoiBOyBoy");
        preparedStatement.setString(3, "MAMAYAGA");

        preparedStatement.executeUpdate();

        ResultSet resultSet = preparedStatement.getGeneratedKeys();
        if (resultSet.next()) {
            Integer id = resultSet.getInt(1);
            System.out.println("Id inserted: " + id);
        }
        preparedStatement.close();
        connection.close();
    }
}
