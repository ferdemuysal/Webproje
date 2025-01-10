
package com.example.yearbook.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.example.yearbook.models.Reply;
import com.example.yearbook.utils.DBConnection;

@WebServlet(name = "EditReplyServlet", value = "/EditReplyServlet")
public class EditReplyServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String ADMIN_ROLE = "admin";
    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String THREAD_JSP = "thread.jsp?id=";
    private static final String FORUM_JSP = "forum.jsp";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String userRole = (String) session.getAttribute("userRole");
        if (!ADMIN_ROLE.equals(userRole)) {
            request.setAttribute(ERROR_MESSAGE, "Bu işlemi yapmak için yetkiniz yok.");
            request.getRequestDispatcher(THREAD_JSP + request.getParameter("threadId")).forward(request, response);
            return;
        }

        request.setCharacterEncoding("UTF-8"); // Türkçe karakterler için

        int replyId = Integer.parseInt(request.getParameter("replyId"));
        String content = request.getParameter("content");
        int threadId = Integer.parseInt(request.getParameter("threadId"));

        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                String sql = "UPDATE replies SET content = ? WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, content);
                    pstmt.setInt(2, replyId);
                    pstmt.executeUpdate();
                }
            } else {
                request.setAttribute(ERROR_MESSAGE, "Veritabanına bağlanılamadı.");
                request.getRequestDispatcher(THREAD_JSP + threadId).forward(request, response);
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute(ERROR_MESSAGE, "Yanıt güncellenirken bir hata oluştu: " + e.getMessage());
            request.getRequestDispatcher(THREAD_JSP + threadId).forward(request, response);
            return;
        }

        // Yanıt başarıyla güncellendi, ilgili konuya yönlendir
        response.sendRedirect(THREAD_JSP + threadId);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String userRole = (String) session.getAttribute("userRole");
        if (!ADMIN_ROLE.equals(userRole)) {
            request.setAttribute(ERROR_MESSAGE, "Bu işlemi yapmak için yetkiniz yok.");
            request.getRequestDispatcher(FORUM_JSP).forward(request, response);
            return;
        }

        int replyId = Integer.parseInt(request.getParameter("replyId"));
        int threadId = Integer.parseInt(request.getParameter("threadId"));
        String action = request.getParameter("action");

        if ("edit".equals(action)) {
            // Yanıt düzenleme modunu belirtmek için request'e bir attribute ekle
            request.setAttribute("editingReplyId", replyId);
            // İlgili yanıtı veritabanından çek
            Reply replyToEdit = getReplyById(replyId, request, response);

            if (replyToEdit == null) {
                // Hata mesajı zaten getReplyById içinde ayarlandı, sadece yönlendir
                request.getRequestDispatcher(FORUM_JSP).forward(request, response);
                return;
            }

            // Yanıtı request scope'una ekle
            request.setAttribute("replyToEdit", replyToEdit);
            // threadId'yi request scope'una ekle
            request.setAttribute("threadId", threadId);

            // thread.jsp'ye yönlendir
            request.getRequestDispatcher(THREAD_JSP + threadId).forward(request, response);
            return;
        } else {
            // Geçersiz action parametresi
            request.setAttribute(ERROR_MESSAGE, "Geçersiz işlem.");
            request.getRequestDispatcher(FORUM_JSP).forward(request, response);
        }
    }

    private Reply getReplyById(int replyId, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Reply reply = null;
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                String sql = "SELECT r.id, r.content, r.created_at, r.thread_id, r.user_id, u.firstName, u.lastName " +
                        "FROM replies r INNER JOIN users u ON r.user_id = u.id WHERE r.id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, replyId);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            reply = new Reply();
                            reply.setId(rs.getInt("id"));
                            reply.setContent(rs.getString("content"));
                            reply.setFormattedDate(rs.getTimestamp("created_at").toLocalDateTime());
                            reply.setAuthorName(rs.getString("firstName") + " " + rs.getString("lastName"));
                            reply.setEditing(true); // Düzenleme modunu aktif et
                            reply.setThreadId(rs.getInt("thread_id"));
                            reply.setUserId(rs.getInt("user_id"));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute(ERROR_MESSAGE, "Yanıt alınırken bir hata oluştu: " + e.getMessage());
            request.getRequestDispatcher(FORUM_JSP).forward(request, response);
            return null;
        }
        return reply;
    }
}
