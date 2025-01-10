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

@WebServlet(name = "DeleteReplyServlet", value = "/DeleteReplyServlet")
public class DeleteReplyServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (!"admin".equals(session.getAttribute("userRole"))) {
            request.setAttribute("errorMessage", "Bu işlemi yapmak için yetkiniz yok.");
            request.getRequestDispatcher("forum.jsp").forward(request, response);
            return;
        }

        int replyId = Integer.parseInt(request.getParameter("replyId"));
        int threadId = Integer.parseInt(request.getParameter("threadId")); // Konu ID'sini al

        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                String sql = "DELETE FROM replies WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, replyId);
                    pstmt.executeUpdate();
                }
            } else {
                request.setAttribute("errorMessage", "Veritabanına bağlanılamadı.");
                request.getRequestDispatcher("thread.jsp?id=" + threadId).forward(request, response); // thread.jsp'ye yönlendir
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Yanıt silinirken bir hata oluştu: " + e.getMessage());
            request.getRequestDispatcher("thread.jsp?id=" + threadId).forward(request, response); // thread.jsp'ye yönlendir
            return;
        }

        // Yanıt başarıyla silindi, ilgili konuya yönlendir
        response.sendRedirect("thread.jsp?id=" + threadId);
    }
}