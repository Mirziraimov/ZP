package com.example.DB;
import com.example.Config;
import java.sql.*;


public class DB_Connection extends Config{
    Connection DBConnection;

    public Connection getDBConnection() throws ClassNotFoundException, SQLException{
        String connectionString = "jdbc:mysql://" + DBHost + ":" + DBPort +"/"+DBName+"?serverTimezone=UTC";

        Class.forName("com.mysql.cj.jdbc.Driver");
        DBConnection = DriverManager.getConnection(connectionString, DBUser, DBPassword);
        return DBConnection;
    }

}
