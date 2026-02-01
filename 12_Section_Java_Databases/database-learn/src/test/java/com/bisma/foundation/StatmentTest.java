package com.bisma.foundation;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class StatmentTest {

    @Test
    void testBasicStatment() throws SQLException {
        Connection connection = ConnectionUtil.getDataSource().getConnection();
        Statement statement = connection.createStatement();

        statement.close();
        connection.close();
    }


    @Test
    void testDMLStatment() throws SQLException {
        /*
            ExecuteUpdate bascily untuk inser, update, delete row (Data manipulation language)
            dia akan mengembalikan integer row affactednya
         */
        Connection connection = ConnectionUtil.getDataSource().getConnection();
        Statement statement = connection.createStatement();

        String insertUser = """
                insert into users (username, password, name) values
                ('MrBista2308', 'Abc@1234', 'Bisma Bratha');
                """;


        int rowAffacted = statement.executeUpdate(insertUser);

        System.out.println("Row Affacted: "+ rowAffacted);
        statement.close();
        connection.close();
    }

    @Test
    void testDQLStatment() throws SQLException {
        /*
            ExecuteQuery berbeda dengan ExecuteUpdate
            ExecuteQuery mengembalikan ResultSet yang merupakan set data row dari database yang di query
            ResultSet mirip iterator namun bukan iterator maka dari itu ga bisa pakai For each
         */
        Connection connection = ConnectionUtil.getDataSource().getConnection();
        Statement statement = connection.createStatement();

        String usersQuery = """
                select * from users;
                """;


        ResultSet rows = statement.executeQuery(usersQuery);

        while (rows.next()) {
            // disini biasanya maper tuk memetakan ke domain atau model java nya
            String username = rows.getString("username");
            String password = rows.getString("password");
            String name = rows.getString("name");
            int id = rows.getInt("id");
            System.out.println(String.join(", ",Integer.toString(id), username, name, password));
        }
        statement.close();
        connection.close();
    }
}
