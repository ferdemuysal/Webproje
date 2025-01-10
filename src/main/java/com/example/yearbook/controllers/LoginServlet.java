package com.example.yearbook.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.mindrot.jbcrypt.BCrypt;

import com.example.yearbook.utils.DBConnection;

@WebServlet(name = "LoginServlet", value = "/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String errorMessage = null;

        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                String sql = "SELECT * FROM users WHERE email = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, email);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            String storedHashedPassword = rs.getString("password");
                            if (BCrypt.checkpw(password, storedHashedPassword)) {
                                // Giriş başarılı, oturum oluştur
                                HttpSession session = request.getSession();
                                session.setAttribute("userId", rs.getInt("id"));
                                session.setAttribute("userFirstName", rs.getString("firstName"));
                                session.setAttribute("userLastName", rs.getString("lastName"));
                                session.setAttribute("userEmail", rs.getString("email"));
                                session.setAttribute("userRole", rs.getString("role"));
                                session.setAttribute("loggedIn", true);

                                // Kullanıcıyı anasayfaya yönlendir
                                response.sendRedirect("index.jsp");
                                return;
                            } else {
                                errorMessage = "Hatalı şifre.";
                            }
                        } else {
                            errorMessage = "Kullanıcı bulunamadı.";
                        }
                    }
                }
            } else {
                errorMessage = "Veritabanına bağlanılamadı.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            errorMessage = "Veritabanı hatası: " + e.getMessage();
        }

        // Hata durumunda, hata mesajını request'e ekle ve login.jsp'ye geri gönder
        request.setAttribute("errorMessage", errorMessage);
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }
}