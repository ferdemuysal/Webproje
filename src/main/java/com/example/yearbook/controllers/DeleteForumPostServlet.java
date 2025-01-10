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

@WebServlet(name = "DeleteForumPostServlet", value = "/DeleteForumPostServlet")
public class DeleteForumPostServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        String role = (String) session.getAttribute("userRole");
        if (role == null || !role.equals("admin")) {
            request.setAttribute("errorMessage", "Bu işlemi yapmak için yetkiniz yok.");
            request.getRequestDispatcher("forum.jsp").forward(request, response);
            return;
        }

        int postId = Integer.parseInt(request.getParameter("postId"));

        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                // forum_posts tablosunu threads tablosu ile değiştirdim
                String sql = "DELETE FROM threads WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, postId);
                    pstmt.executeUpdate();
                }
            } else {
                request.setAttribute("errorMessage", "Veritabanına bağlanılamadı.");
                request.getRequestDispatcher("forum.jsp").forward(request, response);
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Mesaj silinirken bir hata oluştu: " + e.getMessage());
            request.getRequestDispatcher("forum.jsp").forward(request, response);
            return;
        }

        response.sendRedirect("forum.jsp");
    }
}