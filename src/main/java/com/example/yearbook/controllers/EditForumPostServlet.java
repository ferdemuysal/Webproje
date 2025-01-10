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
import javax.servlet.http.HttpSession;

import com.example.yearbook.models.ForumThread;
import com.example.yearbook.utils.DBConnection;

@WebServlet(name = "EditForumPostServlet", value = "/EditForumPostServlet")
public class EditForumPostServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final int THREADS_PER_PAGE = 10;

    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (!"admin".equals(session.getAttribute("userRole"))) {
            request.setAttribute("errorMessage", "Bu işlemi yapmak için yetkiniz yok.");
            request.getRequestDispatcher("forum.jsp").forward(request, response);
            return;
        }

        int threadId = Integer.parseInt(request.getParameter("threadId"));
        String title = request.getParameter("title");
        String content = request.getParameter("content");

        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                String sql = "UPDATE threads SET title = ?, content = ? WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, title);
                    pstmt.setString(2, content);
                    pstmt.setInt(3, threadId);
                    pstmt.executeUpdate();
                }
            } else {
                request.setAttribute("errorMessage", "Veritabanına bağlanılamadı.");
                request.getRequestDispatcher("forum.jsp").forward(request, response);
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Konu güncellenirken bir hata oluştu: " + e.getMessage());
            request.getRequestDispatcher("forum.jsp").forward(request, response);
            return;
        }

        // Konu başarıyla güncellendi, forum sayfasına yönlendir
        response.sendRedirect("forum.jsp");
    }

    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (!"admin".equals(session.getAttribute("userRole"))) {
            request.setAttribute("errorMessage", "Bu işlemi yapmak için yetkiniz yok.");
            request.getRequestDispatcher("forum.jsp").forward(request, response);
            return;
        }

        int threadId = Integer.parseInt(request.getParameter("threadId"));
        String action = request.getParameter("action");

        if ("edit".equals(action)) {
            // Düzenleme modunu belirtmek için bir flag ekle
            request.setAttribute("editingThreadId", threadId);
        }

        // Düzenlenecek konunun bilgilerini al ve request'e ekle
        ForumThread threadToEdit = getThreadById(threadId, request, response);
        request.setAttribute("threadToEdit", threadToEdit);

        // Düzenleme yapıldıktan sonra konuların güncel halini çek
        List<ForumThread> updatedThreads = getThreads(request, response);
        request.setAttribute("threads", updatedThreads);

        int totalThreads = 0;
        int totalPages = 0;
        // Veritabanından toplam konu sayısını al
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                String countSql = "SELECT COUNT(*) FROM threads";
                try (PreparedStatement countStmt = conn.prepareStatement(countSql)) {
                    try (ResultSet countRs = countStmt.executeQuery()) {
                        if (countRs.next()) {
                            totalThreads = countRs.getInt(1);
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

        // Sayfalama için toplam sayfa sayısını hesapla
        totalPages = (int) Math.ceil((double) totalThreads / THREADS_PER_PAGE);

        // Sayfalama bilgilerini ve konu listesini request'e ekle
        request.setAttribute("currentPage", 1); // veya mevcut sayfa numarasını al
        request.setAttribute("totalPages", totalPages);

        request.getRequestDispatcher("forum.jsp").forward(request, response);
    }

    private ForumThread getThreadById(int threadId, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ForumThread thread = null;
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                String sql = "SELECT t.id, t.user_id, t.title, t.content, t.created_at, u.firstName, u.lastName FROM threads t INNER JOIN users u ON t.user_id = u.id WHERE t.id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, threadId);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            thread = new ForumThread();
                            thread.setId(rs.getInt("id"));
                            thread.setUserId(rs.getInt("user_id"));
                            thread.setTitle(rs.getString("title"));
                            thread.setContent(rs.getString("content"));
                            thread.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                            thread.setAuthorName(rs.getString("firstName") + " " + rs.getString("lastName"));
                            thread.setFormattedDate(rs.getTimestamp("created_at").toLocalDateTime());
                            thread.setEditing(true); // Düzenleme modunu aktif et
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Konu alınırken bir hata oluştu: " + e.getMessage());
            request.getRequestDispatcher("forum.jsp").forward(request, response);
        }
        return thread;
    }

    private List<ForumThread> getThreads(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Sayfalama için mevcut sayfa numarasını al
        int currentPage = 1;
        if (request.getParameter("page") != null) {
            currentPage = Integer.parseInt(request.getParameter("page"));
        }

        List<ForumThread> threads = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
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
                            thread.setFormattedDate(rs.getTimestamp("created_at").toLocalDateTime());

                            // Eğer threadId, düzenlenmekte olan thread'in ID'sine eşitse, bu thread için düzenleme modunu aktif et
                            if (request.getAttribute("editingThreadId") != null && rs.getInt("id") == (int) request.getAttribute("editingThreadId")) {
                                thread.setEditing(true);
                            }

                            threads.add(thread);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Hata mesajını ayarla ve forum.jsp'ye yönlendir
            request.setAttribute("errorMessage", "Konular alınırken bir hata oluştu: " + e.getMessage());
            request.getRequestDispatcher("forum.jsp").forward(request, response);
        }

        return threads;
    }
}