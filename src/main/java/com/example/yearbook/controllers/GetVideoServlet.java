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

import com.example.yearbook.models.Comment;
import com.example.yearbook.models.Video;
import com.example.yearbook.utils.DBConnection;

@WebServlet(name = "GetVideoServlet", value = "/GetVideoServlet")
public class GetVideoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int videoId = Integer.parseInt(request.getParameter("id"));

        Video video = null;
        String uploaderName = null;
        List<Comment> comments = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                // Video bilgilerini al
                String videoSql = "SELECT v.youtubeId, v.title, v.user_id, u.firstName, u.lastName FROM videos v INNER JOIN users u ON v.user_id = u.id WHERE v.id = ?";
                try (PreparedStatement videoStmt = conn.prepareStatement(videoSql)) {
                    videoStmt.setInt(1, videoId);
                    try (ResultSet videoRs = videoStmt.executeQuery()) {
                        if (videoRs.next()) {
                            video = new Video();
                            video.setId(videoId);
                            video.setYoutubeId(videoRs.getString("youtubeId"));
                            video.setTitle(videoRs.getString("title"));
                            video.setUserId(videoRs.getInt("user_id"));
                            uploaderName = videoRs.getString("firstName") + " " + videoRs.getString("lastName");
                            video.setUploaderName(uploaderName);
                        }
                    }
                }

                // Videoya ait yorumları al
                String commentsSql = "SELECT c.commentText, c.type, c.date, u.firstName, u.lastName FROM comments c INNER JOIN users u ON c.commenter_id = u.id WHERE c.video_id = ? AND c.type = 'video' ORDER BY c.date DESC";
                try (PreparedStatement commentsStmt = conn.prepareStatement(commentsSql)) {
                    commentsStmt.setInt(1, videoId);
                    try (ResultSet commentsRs = commentsStmt.executeQuery()) {
                        while (commentsRs.next()) {
                            Comment comment = new Comment();
                            comment.setCommentText(commentsRs.getString("commentText"));
                            comment.setFormattedDate(commentsRs.getTimestamp("date").toLocalDateTime());
                            comment.setCommenterName(commentsRs.getString("firstName") + " " + commentsRs.getString("lastName"));
                            comment.setType(commentsRs.getString("type"));
                            comment.setVideoId(videoId);
                            comments.add(comment);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Hata mesajını ayarla ve video.jsp'ye yönlendir
            request.setAttribute("errorMessage", "Video bilgileri alınırken bir hata oluştu: " + e.getMessage());
            request.getRequestDispatcher("video.jsp").forward(request, response);
            return;
        }

        // Video, yükleyen adı ve yorum bilgilerini request'e ekle
        request.setAttribute("video", video);
        request.setAttribute("uploaderName", uploaderName);
        request.setAttribute("comments", comments);

        // Yönlendirmeyi video.jsp içinde jsp:include ile yaptığımız için burada yönlendirme yapmıyoruz
        // request.getRequestDispatcher("video.jsp").forward(request, response);
    }
}