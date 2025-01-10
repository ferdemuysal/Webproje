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

@WebServlet(name = "DeleteThreadServlet", value = "/DeleteThreadServlet")
public class DeleteThreadServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (!"admin".equals(session.getAttribute("userRole"))) {
            request.setAttribute("errorMessage", "Bu işlemi yapmak için yetkiniz yok.");
            request.getRequestDispatcher("forum.jsp").forward(request, response);
            return;
        }

        int threadId = Integer.parseInt(request.getParameter("threadId"));

        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                // Konuyu silmeden önce, konuya bağlı yanıtları sil
                String deleteRepliesSql = "DELETE FROM replies WHERE thread_id = ?";
                try (PreparedStatement pstmtReplies = conn.prepareStatement(deleteRepliesSql)) {
                    pstmtReplies.setInt(1, threadId);
                    pstmtReplies.executeUpdate();
                }

                // Konuyu sil
                String deleteThreadSql = "DELETE FROM threads WHERE id = ?";
                try (PreparedStatement pstmtThread = conn.prepareStatement(deleteThreadSql)) {
                    pstmtThread.setInt(1, threadId);
                    pstmtThread.executeUpdate();
                }
            } else {
                request.setAttribute("errorMessage", "Veritabanına bağlanılamadı.");
                request.getRequestDispatcher("forum.jsp").forward(request, response);
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Konu silinirken bir hata oluştu: " + e.getMessage());
            request.getRequestDispatcher("forum.jsp").forward(request, response);
            return;
        }

        response.sendRedirect("forum.jsp");
    }
}