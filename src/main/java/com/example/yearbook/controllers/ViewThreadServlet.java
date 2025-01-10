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

import com.example.yearbook.models.ForumThread;
import com.example.yearbook.models.Reply;
import com.example.yearbook.utils.DBConnection;

@WebServlet(name = "ViewThreadServlet", value = "/ViewThreadServlet")
public class ViewThreadServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // ID parametresini al ve kontrol et
        String threadIdStr = request.getParameter("id");

        if (threadIdStr == null || threadIdStr.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Hata: Konu ID'si belirtilmemiş.");
            request.getRequestDispatcher("forum.jsp").forward(request, response);
            return;
        }

        int threadId;
        try {
            threadId = Integer.parseInt(threadIdStr);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Hata: Geçersiz konu ID'si.");
            request.getRequestDispatcher("forum.jsp").forward(request, response);
            return;
        }

        ForumThread thread = null;
        List<Reply> replies = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                // Konu bilgilerini al
                String threadSql = "SELECT t.*, u.firstName, u.lastName FROM threads t JOIN users u ON t.user_id = u.id WHERE t.id = ?";
                try (PreparedStatement threadStmt = conn.prepareStatement(threadSql)) {
                    threadStmt.setInt(1, threadId);
                    try (ResultSet threadRs = threadStmt.executeQuery()) {
                        if (threadRs.next()) {
                            thread = new ForumThread();
                            thread.setId(threadRs.getInt("id"));
                            thread.setUserId(threadRs.getInt("user_id"));
                            thread.setTitle(threadRs.getString("title"));
                            thread.setContent(threadRs.getString("content"));
                            thread.setCreatedAt(threadRs.getTimestamp("created_at").toLocalDateTime());
                            thread.setAuthorName(threadRs.getString("firstName") + " " + threadRs.getString("lastName"));
                            thread.setFormattedDate(threadRs.getTimestamp("created_at").toLocalDateTime());
                        }
                    }
                }

                // Eğer konu bulunamadıysa hata mesajı göster
                if (thread == null) {
                    request.setAttribute("errorMessage", "Hata: İlgili konu bulunamadı.");
                    request.getRequestDispatcher("forum.jsp").forward(request, response);
                    return;
                }

                // Yanıtları al
                String repliesSql = "SELECT r.*, u.firstName, u.lastName FROM replies r JOIN users u ON r.user_id = u.id WHERE r.thread_id = ? ORDER BY r.created_at ASC";
                try (PreparedStatement repliesStmt = conn.prepareStatement(repliesSql)) {
                    repliesStmt.setInt(1, threadId);
                    try (ResultSet repliesRs = repliesStmt.executeQuery()) {
                        while (repliesRs.next()) {
                            Reply reply = new Reply();
                            reply.setId(repliesRs.getInt("id"));
                            reply.setThreadId(repliesRs.getInt("thread_id"));
                            reply.setUserId(repliesRs.getInt("user_id"));
                            reply.setContent(repliesRs.getString("content"));
                            reply.setCreatedAt(repliesRs.getTimestamp("created_at").toLocalDateTime());
                            reply.setFormattedDate(repliesRs.getTimestamp("created_at").toLocalDateTime());
                            reply.setAuthorName(repliesRs.getString("firstName") + " " + repliesRs.getString("lastName"));
                            // Yanıtın düzenleme modunda olmadığını varsayılan olarak ayarla
                            reply.setEditing(false);
                            replies.add(reply);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Konu görüntülenirken bir hata oluştu: " + e.getMessage());
            request.getRequestDispatcher("forum.jsp").forward(request, response);
            return;
        }

        // thread ve replies'i request scope'una ekle
        request.setAttribute("thread", thread);
        request.setAttribute("replies", replies);

        // Yönlendirme yapma, çünkü zaten jsp:include ile çağrılıyorsun
        // request.getRequestDispatcher("thread.jsp").forward(request, response);
    }
}
