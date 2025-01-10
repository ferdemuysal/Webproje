package com.example.yearbook.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.example.yearbook.utils.DBConnection;

@WebServlet(name = "DeleteCommentServlet", value = "/DeleteCommentServlet")
public class DeleteCommentServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (!"admin".equals(session.getAttribute("userRole"))) {
            request.setAttribute("errorMessage", "Bu işlemi yapmak için yetkiniz yok.");
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return;
        }

        int commentId = Integer.parseInt(request.getParameter("commentId"));
        // Yorumun yapıldığı sayfa türünü al (person, photo, video)
        String commentType = request.getParameter("commentType");

        // İlgili sayfaya yönlendirme için kullanılacak URL'yi ve ID'yi sakla
        String redirectUrl = "index.jsp"; // Varsayılan olarak ana sayfaya yönlendir
        int id = 0; // İlgili ID (personId, photoId veya videoId)

        // Yorum türüne göre yönlendirme URL'sini ve ID'yi ayarla
        if ("person".equals(commentType)) {
            id = Integer.parseInt(request.getParameter("personId"));
            redirectUrl = "person.jsp?id=" + id;
        } else if ("photo".equals(commentType)) {
            id = Integer.parseInt(request.getParameter("photoId"));
            redirectUrl = "photo.jsp?id=" + id;
        } else if ("video".equals(commentType)) {
            id = Integer.parseInt(request.getParameter("videoId"));
            redirectUrl = "video.jsp?id=" + id;
        }

        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                String sql = "DELETE FROM comments WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, commentId);
                    pstmt.executeUpdate();
                }
            } else {
                request.setAttribute("errorMessage", "Veritabanına bağlanılamadı.");
                response.sendRedirect(redirectUrl);
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Yorum silinirken bir hata oluştu: " + e.getMessage());
            response.sendRedirect(redirectUrl);
            return;
        }

        // Yorum başarıyla silindi, ilgili sayfaya yönlendir
        response.sendRedirect(redirectUrl);
    }
}