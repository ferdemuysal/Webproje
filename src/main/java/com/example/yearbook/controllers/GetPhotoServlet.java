package com.example.yearbook.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.yearbook.models.Comment;
import com.example.yearbook.models.Photo;
import com.example.yearbook.utils.DBConnection;

@WebServlet(name = "GetPhotoServlet", value = "/GetPhotoServlet")
public class GetPhotoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int photoId = Integer.parseInt(request.getParameter("id"));

        Photo photo = null;
        String uploaderName = null;
        List<Comment> comments = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                // Fotoğraf bilgilerini al
                String photoSql = "SELECT p.filePath, p.user_id, u.firstName, u.lastName FROM photos p INNER JOIN users u ON p.user_id = u.id WHERE p.id = ?";
                try (PreparedStatement photoStmt = conn.prepareStatement(photoSql)) {
                    photoStmt.setInt(1, photoId);
                    try (ResultSet photoRs = photoStmt.executeQuery()) {
                        if (photoRs.next()) {
                            photo = new Photo();
                            photo.setId(photoId);
                            photo.setFilePath(photoRs.getString("filePath"));
                            photo.setUserId(photoRs.getInt("user_id"));
                            uploaderName = photoRs.getString("firstName") + " " + photoRs.getString("lastName");
                            photo.setUploaderName(uploaderName);
                        }
                    }
                }

                // Fotoğrafa ait yorumları al
                String commentsSql = "SELECT c.id, c.commentText, c.type, c.date, u.firstName, u.lastName, c.commenter_id, c.user_id FROM comments c INNER JOIN users u ON c.commenter_id = u.id WHERE c.photo_id = ? AND c.type = 'photo' ORDER BY c.date DESC";
                try (PreparedStatement commentsStmt = conn.prepareStatement(commentsSql)) {
                    commentsStmt.setInt(1, photoId);
                    try (ResultSet commentsRs = commentsStmt.executeQuery()) {
                        while (commentsRs.next()) {
                            Comment comment = new Comment();
                            comment.setId(commentsRs.getInt("id")); // id bilgisini ekle
                            comment.setCommentText(commentsRs.getString("commentText"));
                            comment.setFormattedDate(commentsRs.getTimestamp("date").toLocalDateTime());
                            comment.setCommenterName(commentsRs.getString("firstName") + " " + commentsRs.getString("lastName"));
                            comment.setType(commentsRs.getString("type"));
                            comment.setPhotoId(photoId);
                            comment.setCommenterId(commentsRs.getInt("commenter_id"));
                            comment.setUserId(commentsRs.getInt("user_id"));
                            comments.add(comment);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Hata mesajını ayarla ve photo.jsp'ye yönlendir
            request.setAttribute("errorMessage", "Fotoğraf bilgileri alınırken bir hata oluştu: " + e.getMessage());
            request.getRequestDispatcher("photo.jsp").forward(request, response);
            return;
        }

        // Fotoğraf, yükleyen adı ve yorum bilgilerini request'e ekle
        request.setAttribute("photo", photo);
        request.setAttribute("uploaderName", uploaderName);
        request.setAttribute("comments", comments);
        request.setAttribute("photoId", photoId); // photoId'yi ekle

        // Yönlendirmeyi photo.jsp içinde jsp:include ile yaptığımız için burada yönlendirme yapmıyoruz
        // request.getRequestDispatcher("photo.jsp").forward(request, response);
    }
}