package com.student.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:firebirdsql://localhost:3050/C:/Windows/System32/DB1.FDB?encoding=UTF8";
    private static final String USER = "sysdba";
    private static final String PASSWORD = "masterkey";
    private static final String DRIVER = "org.firebirdsql.jdbc.FBDriver";


    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName(DRIVER);
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new SQLException("Не найден драйвер БД!", e);
            }
        }
        return connection;
    }
}

