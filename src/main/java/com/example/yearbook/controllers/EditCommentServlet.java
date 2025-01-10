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

import com.example.yearbook.models.Comment;
import com.example.yearbook.models.Video;
import com.example.yearbook.utils.DBConnection;

@WebServlet(name = "EditCommentServlet", value = "/EditCommentServlet")
public class EditCommentServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (!"admin".equals(session.getAttribute("userRole"))) {
            request.setAttribute("errorMessage", "Bu işlemi yapmak için yetkiniz yok.");
            request.getRequestDispatcher("index.jsp").forward(request, response); // Yetkisiz erişimde ana sayfaya yönlendir
            return;
        }

        request.setCharacterEncoding("UTF-8");

        int commentId = Integer.parseInt(request.getParameter("commentId"));
        String content = request.getParameter("content");
        String commentType = request.getParameter("commentType"); // Yorum türünü al
        int personId = 0;
        int photoId = 0;
        int videoId = 0;

        // Yorum türüne göre yönlendirme URL'sini ve ilgili ID'yi belirle
        String redirectUrl = "index.jsp"; // Varsayılan yönlendirme URL'si
        if ("person".equals(commentType)) {
            personId = Integer.parseInt(request.getParameter("personId"));
            redirectUrl = "person.jsp?id=" + personId;
        } else if ("photo".equals(commentType)) {
            photoId = Integer.parseInt(request.getParameter("photoId"));
            redirectUrl = "photo.jsp?id=" + photoId;
        } else if ("video".equals(commentType)) {
            videoId = Integer.parseInt(request.getParameter("videoId"));
            redirectUrl = "video.jsp?id=" + videoId;
        }

        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                String sql = "UPDATE comments SET commentText = ? WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, content);
                    pstmt.setInt(2, commentId);
                    pstmt.executeUpdate();
                }
            } else {
                request.setAttribute("errorMessage", "Veritabanına bağlanılamadı.");
                response.sendRedirect(redirectUrl);
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Yorum güncellenirken bir hata oluştu: " + e.getMessage());
            response.sendRedirect(redirectUrl);
            return;
        }

        // Yorum başarıyla güncellendi, ilgili sayfaya yönlendir
        response.sendRedirect(redirectUrl);
    }

    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (!"admin".equals(session.getAttribute("userRole"))) {
            request.setAttribute("errorMessage", "Bu işlemi yapmak için yetkiniz yok.");
            request.getRequestDispatcher("index.jsp").forward(request, response); // Yetkisiz erişimde ana sayfaya yönlendir
            return;
        }

        int commentId = Integer.parseInt(request.getParameter("commentId"));
        String commentType = request.getParameter("commentType");
        String action = request.getParameter("action");
        int personId = 0;
        int photoId = 0;
        int videoId = 0;
        String redirectUrl = "index.jsp";

        // Yorum türüne göre ilgili ID'yi ve yönlendirme URL'sini al
        if ("person".equals(commentType)) {
            personId = Integer.parseInt(request.getParameter("personId"));
            redirectUrl = "person.jsp?id=" + personId;
        } else if ("photo".equals(commentType)) {
            photoId = Integer.parseInt(request.getParameter("photoId"));
            redirectUrl = "photo.jsp?id=" + photoId;
        } else if ("video".equals(commentType)) {
            videoId = Integer.parseInt(request.getParameter("videoId"));
            redirectUrl = "video.jsp?id=" + videoId;
        }

        if ("edit".equals(action)) {
            // Yorum düzenleme modunu belirtmek için request'e bir attribute ekle
            request.setAttribute("editingCommentId", commentId);
        }

        // Düzenlenecek yorumun bilgilerini al ve request'e ekle
        Comment commentToEdit = getCommentById(commentId, request, response);
        request.setAttribute("commentToEdit", commentToEdit);

        // Yorumun türüne göre bilgileri al ve request'e ekle
        if ("person".equals(commentType)) {
            request.setAttribute("personId", personId);
            request.setAttribute("person", getPersonById(personId, request, response));
            request.setAttribute("comments", getCommentsByTypeAndId("person", personId, request, response));
        } else if ("photo".equals(commentType)) {
            request.setAttribute("photoId", photoId);
            request.setAttribute("photo", getPhotoById(photoId, request, response));
            request.setAttribute("comments", getCommentsByTypeAndId("photo", photoId, request, response));
        } else if ("video".equals(commentType)) {
            request.setAttribute("videoId", videoId);
            request.setAttribute("video", getVideoById(videoId, request, response));
            request.setAttribute("comments", getCommentsByTypeAndId("video", videoId, request, response));
        }

        request.getRequestDispatcher(redirectUrl).forward(request, response);
    }

    private Comment getCommentById(int commentId, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Comment comment = null;
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                String sql = "SELECT c.*, u.firstName, u.lastName FROM comments c JOIN users u ON c.commenter_id = u.id WHERE c.id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, commentId);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            comment = new Comment();
                            comment.setId(rs.getInt("id"));
                            comment.setCommentText(rs.getString("commentText"));
                            comment.setFormattedDate(rs.getTimestamp("date").toLocalDateTime());
                            comment.setCommenterName(rs.getString("firstName") + " " + rs.getString("lastName"));
                            comment.setEditing(true); // Düzenleme modunu aktif et
                            // İhtiyaca göre diğer alanları da doldurun
                            comment.setType(rs.getString("type"));
                            comment.setUserId(rs.getInt("user_id"));
                            if(comment.getType().equals("photo")){
                                comment.setPhotoId(rs.getInt("photo_id"));
                            }
                            if(comment.getType().equals("video")){
                                comment.setVideoId(rs.getInt("video_id"));
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Yorum alınırken bir hata oluştu: " + e.getMessage());
            request.getRequestDispatcher("index.jsp").forward(request, response); // Hata durumunda ana sayfaya yönlendir
        }
        return comment;
    }

    private List<Comment> getCommentsByTypeAndId(String type, int id, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Comment> comments = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                String sql = "SELECT c.*, u.firstName, u.lastName FROM comments c JOIN users u ON c.commenter_id = u.id WHERE c.type = ? AND c." + type + "_id = ? ORDER BY c.date DESC";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, type);
                    pstmt.setInt(2, id);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            Comment comment = new Comment();
                            comment.setId(rs.getInt("id"));
                            comment.setCommentText(rs.getString("commentText"));
                            comment.setFormattedDate(rs.getTimestamp("date").toLocalDateTime());
                            comment.setCommenterName(rs.getString("firstName") + " " + rs.getString("lastName"));
                            comment.setType(rs.getString("type"));
                            comment.setUserId(rs.getInt("user_id"));
                            if(comment.getType().equals("photo")){
                                comment.setPhotoId(rs.getInt("photo_id"));
                            }
                            if(comment.getType().equals("video")){
                                comment.setVideoId(rs.getInt("video_id"));
                            }
                            comments.add(comment);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Yorumlar alınırken bir hata oluştu: " + e.getMessage());
            request.getRequestDispatcher("index.jsp").forward(request, response); // Hata durumunda ana sayfaya yönlendir
        }
        return comments;
    }

    private Object getPersonById(int personId, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        com.example.yearbook.models.User person = null;
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                String personSql = "SELECT * FROM users WHERE id = ?";
                try (PreparedStatement personStmt = conn.prepareStatement(personSql)) {
                    personStmt.setInt(1, personId);
                    try (ResultSet personRs = personStmt.executeQuery()) {
                        if (personRs.next()) {
                            person = new com.example.yearbook.models.User();
                            person.setId(personRs.getInt("id"));
                            person.setFirstName(personRs.getString("firstName"));
                            person.setLastName(personRs.getString("lastName"));
                            person.setCity(personRs.getString("city"));
                            person.setCurrentSchool(personRs.getString("currentSchool"));
                            person.setCurrentJob(personRs.getString("currentJob"));
                            person.setEmail(personRs.getString("email"));
                            person.setWebsite(personRs.getString("website"));
                            person.setFacebookId(personRs.getString("facebookId"));
                            person.setTwitterId(personRs.getString("twitterId"));
                            person.setProfilePicture(personRs.getString("profilePicture"));
                            // Diğer alanlar da buraya eklenebilir
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Hata mesajını ayarla ve person.jsp'ye yönlendir
            request.setAttribute("errorMessage", "Kişi bilgileri alınırken bir hata oluştu: " + e.getMessage());
            request.getRequestDispatcher("person.jsp").forward(request, response);
            return null;
        }

        return person;
    }

    private Object getPhotoById(int photoId, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        com.example.yearbook.models.Photo photo = null;
        String uploaderName = null;

        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                String photoSql = "SELECT p.filePath, p.user_id, u.firstName, u.lastName FROM photos p INNER JOIN users u ON p.user_id = u.id WHERE p.id = ?";
                try (PreparedStatement photoStmt = conn.prepareStatement(photoSql)) {
                    photoStmt.setInt(1, photoId);
                    try (ResultSet photoRs = photoStmt.executeQuery()) {
                        if (photoRs.next()) {
                            photo = new com.example.yearbook.models.Photo();
                            photo.setId(photoId);
                            photo.setFilePath(photoRs.getString("filePath"));
                            photo.setUserId(photoRs.getInt("user_id"));
                            uploaderName = photoRs.getString("firstName") + " " + photoRs.getString("lastName");
                            photo.setUploaderName(uploaderName);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Hata mesajını ayarla ve ilgili sayfaya yönlendir
            request.setAttribute("errorMessage", "Fotoğraf bilgileri alınırken bir hata oluştu: " + e.getMessage());
            request.getRequestDispatcher("photo.jsp").forward(request, response); // Hata durumunda photo.jsp'ye yönlendir
            return null;
        }
        return photo;
    }

    private Object getVideoById(int videoId, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException  {
        Video video = null;
        String uploaderName = null;

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
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Hata mesajını ayarla ve video.jsp'ye yönlendir
            request.setAttribute("errorMessage", "Video bilgileri alınırken bir hata oluştu: " + e.getMessage());
            request.getRequestDispatcher("video.jsp").forward(request, response);
            return null;
        }

        return video;
    }
}