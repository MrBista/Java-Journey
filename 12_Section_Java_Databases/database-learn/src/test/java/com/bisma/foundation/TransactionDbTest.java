package com.bisma.foundation;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TransactionDbTest {

    @Test
    void testManualTransaction() throws SQLException {
        String sqlInsertUser = """
                insert into users(username, password, name) values(?, ?, ?);
                """;

        Connection connection = ConnectionUtil.getDataSource().getConnection();
        connection.setAutoCommit(false);

        PreparedStatement preparedStatement = connection.prepareStatement(sqlInsertUser);


        preparedStatement.setString(1, "MrBistaGagah2308Abc");
        preparedStatement.setString(2, "Abc@1234");
        preparedStatement.setString(3, "Gusti Bisman Taka");

        preparedStatement.executeUpdate();

        connection.commit();
        preparedStatement.close();
        connection.close();
    }

}
