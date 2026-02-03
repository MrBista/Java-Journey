package com.bisma.foundation;

import org.junit.jupiter.api.Test;

import java.sql.*;


public class DateTimeTest {

    @Test
    void testInsertDateTime()throws SQLException {
        String sql = """
                insert into sample_date (sample_date, sample_time, sample_timestamp) values (?, ?, ?)
                """;
        Connection connection = ConnectionUtil.getDataSource().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        preparedStatement.setDate(1, new Date(System.currentTimeMillis()));
        preparedStatement.setTime(2, new Time(System.currentTimeMillis()));
        preparedStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));


        preparedStatement.executeUpdate();

        ResultSet resultSet = preparedStatement.getGeneratedKeys();
        if (resultSet.next()) {
            System.out.println("Successfully insert to db with id: " + resultSet.getInt(1));
        }

        preparedStatement.close();
        connection.close();
    }

    @Test
    void testGetAllDateTimeTable() throws SQLException{
        String sql = """
                select * from sample_date
                """;


        Connection connection = ConnectionUtil.getDataSource().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
             java.util.Date timestamp = resultSet.getTimestamp("sample_timestamp");
            java.util.Date time = resultSet.getTime("sample_time");
            java.util.Date date = resultSet.getDate("sample_date");

            System.out.println("Time: " + time + " date: " + date + " timestamp: " + timestamp);
        }

        preparedStatement.close();
        connection.close();

    }
}
