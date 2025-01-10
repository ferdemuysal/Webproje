package com.example.yearbook.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.example.yearbook.utils.DBConnection;

@WebServlet(name = "AddPhotoCommentServlet", value = "/AddPhotoCommentServlet")
public class AddPhotoCommentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer commenterId = (Integer) session.getAttribute("userId");

        // Kullanıcı oturum açmamışsa, hata mesajı gönder veya giriş sayfasına yönlendir
        if (commenterId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int photoId = Integer.parseInt(request.getParameter("photoId"));
        String commentText = request.getParameter("comment");

        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                String sql = "INSERT INTO comments (photo_id, commenter_id, commentText, date) VALUES (?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, photoId);
                    pstmt.setInt(2, commenterId);
                    pstmt.setString(3, commentText);
                    pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

                    pstmt.executeUpdate();
                }
            } else {
                // Veritabanı bağlantı hatası
                request.setAttribute("errorMessage", "Veritabanına bağlanılamadı.");
                request.getRequestDispatcher("photo.jsp?id=" + photoId).forward(request, response);
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Veritabanı hatası
            request.setAttribute("errorMessage", "Yorum eklenirken bir hata oluştu: " + e.getMessage());
            request.getRequestDispatcher("photo.jsp?id=" + photoId).forward(request, response);
            return;
        }

        // Yorum başarıyla eklendi, fotoğraf sayfasına yönlendir
        response.sendRedirect("photo.jsp?id=" + photoId);
    }
}