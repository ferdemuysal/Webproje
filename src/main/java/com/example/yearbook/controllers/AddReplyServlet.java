package com.example.yearbook.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.example.yearbook.utils.DBConnection;

@WebServlet(name = "AddReplyServlet", value = "/AddReplyServlet")
public class AddReplyServlet extends HttpServlet {

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

        int threadId = Integer.parseInt(request.getParameter("threadId"));
        String content = request.getParameter("content");

        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                String sql = "INSERT INTO replies (thread_id, user_id, content, created_at) VALUES (?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, threadId);
                    pstmt.setInt(2, userId);
                    pstmt.setString(3, content);
                    pstmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));

                    pstmt.executeUpdate();
                }
            } else {
                request.setAttribute("errorMessage", "Veritabanına bağlanılamadı.");
                request.getRequestDispatcher("thread.jsp?id=" + threadId).forward(request, response);
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Yanıt eklenirken bir hata oluştu: " + e.getMessage());
            request.getRequestDispatcher("thread.jsp?id=" + threadId).forward(request, response);
            return;
        }

        response.sendRedirect("thread.jsp?id=" + threadId);
    }
}