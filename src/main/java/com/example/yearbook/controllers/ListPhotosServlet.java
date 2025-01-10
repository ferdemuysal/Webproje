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

import com.example.yearbook.models.Photo;
import com.example.yearbook.utils.DBConnection;

@WebServlet(name = "ListPhotosServlet", value = "/ListPhotosServlet")
public class ListPhotosServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    private static final int PHOTOS_PER_PAGE = 12;

    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       
        int currentPage = 1;
        if (request.getParameter("page") != null) {
            currentPage = Integer.parseInt(request.getParameter("page"));
        }

        List<Photo> photos = new ArrayList<>();
        int totalPhotos = 0;

        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                
            	String countSql = "SELECT COUNT(*) FROM photos WHERE filePath NOT IN (SELECT profilePicture FROM users WHERE profilePicture IS NOT NULL)";
                try (PreparedStatement countStmt = conn.prepareStatement(countSql)) {
                    try (ResultSet countRs = countStmt.executeQuery()) {
                        if (countRs.next()) {
                            totalPhotos = countRs.getInt(1);
                        }
                    }
                }

                
                String sql = "SELECT p.id, p.filePath, p.user_id, u.firstName, u.lastName " +
                        "FROM photos p " +
                        "INNER JOIN users u ON p.user_id = u.id " +
                        "WHERE p.filePath NOT IN (SELECT profilePicture FROM users WHERE profilePicture IS NOT NULL) " +
                        "ORDER BY p.id DESC LIMIT ? OFFSET ?";
           try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
               pstmt.setInt(1, PHOTOS_PER_PAGE);
               pstmt.setInt(2, (currentPage - 1) * PHOTOS_PER_PAGE);

               try (ResultSet rs = pstmt.executeQuery()) {
                   while (rs.next()) {
                       Photo photo = new Photo();
                       photo.setId(rs.getInt("id"));
                       photo.setFilePath(rs.getString("filePath"));
                       photo.setUserId(rs.getInt("user_id"));
                       photo.setUploaderName(rs.getString("firstName") + " " + rs.getString("lastName"));
                       
                       photo.setProfilePicture(false);
                       photos.add(photo);
                   }
               }
           }
       }
   } catch (SQLException e) {
       e.printStackTrace();
       
       request.setAttribute("errorMessage", "Fotoğraflar alınırken bir hata oluştu: " + e.getMessage());
       request.getRequestDispatcher("photos.jsp").forward(request, response);
       return;
   }

        // Toplam sayfa sayısını hesapla
        int totalPages = (int) Math.ceil((double) totalPhotos / PHOTOS_PER_PAGE);

        // Sayfalama bilgilerini ve fotoğraf listesini request'e ekle
        request.setAttribute("photos", photos);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);

        
    }
}