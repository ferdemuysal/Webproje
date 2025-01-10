package com.example.yearbook.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/proje?characterEncoding=UTF-8"; // Veritabanı adınızı buraya yazın
    private static final String DB_USER = "postgres"; // PostgreSQL kullanıcı adınız
    private static final String DB_PASSWORD = "postgres"; // PostgreSQL şifreniz

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            // Yapılandırma burada yapılıyor
            Statement stmt = conn.createStatement();
            stmt.execute("SET search_path TO public"); // Şema adınızı buraya yazın
            stmt.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
}