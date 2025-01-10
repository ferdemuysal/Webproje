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

@WebServlet(name = "AddForumPostServlet", value = "/AddForumPostServlet")
public class AddForumPostServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // "message" yerine "content", "forum_posts" yerine "threads" kullanacağız
        String title = request.getParameter("title");
        String content = request.getParameter("content");

        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                // forum_posts tablosunu threads tablosu ile değiştirdim
                String sql = "INSERT INTO threads (user_id, title, content, created_at) VALUES (?, ?, ?, NOW())";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, userId);
                    pstmt.setString(2, title);
                    pstmt.setString(3, content);

                    pstmt.executeUpdate();
                }
            } else {
                request.setAttribute("errorMessage", "Veritabanına bağlanılamadı.");
                request.getRequestDispatcher("forum.jsp").forward(request, response);
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Konu eklenirken bir hata oluştu: " + e.getMessage());
            request.getRequestDispatcher("forum.jsp").forward(request, response);
            return;
        }

        response.sendRedirect("forum.jsp");
    }
}