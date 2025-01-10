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

@WebServlet(name = "AddCommentServlet", value = "/AddCommentServlet")
public class AddCommentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        Integer commenterId = (Integer) session.getAttribute("userId");

        // Kullanıcı oturum açmamışsa, hata mesajı gönder veya giriş sayfasına yönlendir
        if (commenterId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Yorum yapılan personId'yi al (person.jsp'den geliyor)
        String personIdStr = request.getParameter("personId");
        if (personIdStr == null || personIdStr.isEmpty()) {
            request.setAttribute("errorMessage", "Person ID is missing.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }
        int userId = Integer.parseInt(personIdStr);

        String commentText = request.getParameter("comment");
        String commentType = request.getParameter("commentType"); // Yorum türünü al (person, photo, video)

        // Yorumun hangi fotoğrafa veya videoya ait olduğunu belirlemek için photoId ve videoId al
        Integer photoId = null;
        String photoIdStr = request.getParameter("photoId");
        if (photoIdStr != null && !photoIdStr.isEmpty()) {
            photoId = Integer.parseInt(photoIdStr);
        }

        Integer videoId = null;
        String videoIdStr = request.getParameter("videoId");
        if (videoIdStr != null && !videoIdStr.isEmpty()) {
            videoId = Integer.parseInt(videoIdStr);
        }

        // Yorum türüne göre ilgili ID'yi ayarla (photoId veya videoId)
        // person için kontrol etmeye gerek yok, photoId ve videoId null olabilir.
        if ("photo".equals(commentType) && photoId == null) {
            request.setAttribute("errorMessage", "Fotoğraf ID'si eksik.");
            request.getRequestDispatcher("person.jsp?id=" + userId).forward(request, response); // Hata durumunda yönlendir
            return;
        }
        if ("video".equals(commentType) && videoId == null) {
            request.setAttribute("errorMessage", "Video ID'si eksik.");
            request.getRequestDispatcher("person.jsp?id=" + userId).forward(request, response); // Hata durumunda yönlendir
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                String sql = "INSERT INTO comments (user_id, commenter_id, commentText, date, photo_id, video_id, type) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, userId);
                    pstmt.setInt(2, commenterId);
                    pstmt.setString(3, commentText);
                    pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

                    // Yorum türüne göre photoId veya videoId'yi ayarla
                    if ("photo".equals(commentType)) {
                        pstmt.setInt(5, photoId);
                        pstmt.setNull(6, java.sql.Types.INTEGER);
                    } else if ("video".equals(commentType)) {
                        pstmt.setNull(5, java.sql.Types.INTEGER);
                        pstmt.setInt(6, videoId);
                    } else { // type = person ise
                        pstmt.setNull(5, java.sql.Types.INTEGER); // photo_id null olarak ayarlanmalı
                        pstmt.setNull(6, java.sql.Types.INTEGER); // video_id null olarak ayarlanmalı
                    }

                    pstmt.setString(7, commentType); // Yorum türünü kaydet

                    pstmt.executeUpdate();
                }
            } else {
                // Veritabanı bağlantı hatası
                request.setAttribute("errorMessage", "Veritabanına bağlanılamadı.");
                request.getRequestDispatcher("person.jsp?id=" + userId).forward(request, response);
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Veritabanı hatası
            request.setAttribute("errorMessage", "Yorum eklenirken bir hata oluştu: " + e.getMessage());
            request.getRequestDispatcher("person.jsp?id=" + userId).forward(request, response);
            return;
        }

        // Yorum başarıyla eklendi, kişi sayfasına yönlendir
        response.sendRedirect("person.jsp?id=" + userId);
    }
}