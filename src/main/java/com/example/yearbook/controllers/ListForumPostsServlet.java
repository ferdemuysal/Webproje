package com.example.yearbook.controllers;

import java.io.IOException;
import java.io.Serializable;
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

import com.example.yearbook.models.ForumThread;
import com.example.yearbook.utils.DBConnection;

@WebServlet(name = "ListThreadsServlet", value = "/ListThreadsServlet")
public class ListForumPostsServlet extends HttpServlet implements Serializable { private static final long serialVersionUID = 1L;

    private static final int THREADS_PER_PAGE = 10;

    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Sayfalama için mevcut sayfa numarasını al
        int currentPage = 1;
        if (request.getParameter("page") != null) {
            currentPage = Integer.parseInt(request.getParameter("page"));
        }

        List<ForumThread> threads = new ArrayList<>();
        int totalThreads = 0;

        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                // Toplam konu sayısını al
                String countSql = "SELECT COUNT(*) FROM threads";
                try (PreparedStatement countStmt = conn.prepareStatement(countSql)) {
                    try (ResultSet countRs = countStmt.executeQuery()) {
                        if (countRs.next()) {
                            totalThreads = countRs.getInt(1);
                        }
                    }
                }

                // Sayfalama ile konuları al
                String sql = "SELECT t.id, t.user_id, t.title, t.content, t.created_at, u.firstName, u.lastName FROM threads t INNER JOIN users u ON t.user_id = u.id ORDER BY t.created_at DESC LIMIT ? OFFSET ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, THREADS_PER_PAGE);
                    pstmt.setInt(2, (currentPage - 1) * THREADS_PER_PAGE);

                    try (ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            ForumThread thread = new ForumThread();
                            thread.setId(rs.getInt("id"));
                            thread.setUserId(rs.getInt("user_id"));
                            thread.setTitle(rs.getString("title"));
                            thread.setContent(rs.getString("content"));
                            thread.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                            thread.setAuthorName(rs.getString("firstName") + " " + rs.getString("lastName"));
                            // Tarih formatlama
                            thread.setFormattedDate(rs.getTimestamp("created_at").toLocalDateTime());
                            threads.add(thread);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Konular alınırken bir hata oluştu: " + e.getMessage());
            request.getRequestDispatcher("forum.jsp").forward(request, response);
            return;
        }

        int totalPages = (int) Math.ceil((double) totalThreads / THREADS_PER_PAGE);

        // Konu listesini ve sayfalama bilgilerini request'e ekle
        request.setAttribute("threads", threads);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);

        request.getRequestDispatcher("forum.jsp").forward(request, response);
    }
}