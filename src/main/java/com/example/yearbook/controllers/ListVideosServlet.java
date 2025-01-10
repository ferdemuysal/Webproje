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

import com.example.yearbook.models.Video;
import com.example.yearbook.utils.DBConnection;

@WebServlet(name = "ListVideosServlet", value = "/ListVideosServlet")
public class ListVideosServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    private static final int VIDEOS_PER_PAGE = 9; // Sayfa başına gösterilecek video sayısı

    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Sayfalama için mevcut sayfa numarasını al
        int currentPage = 1;
        if (request.getParameter("page") != null) {
            currentPage = Integer.parseInt(request.getParameter("page"));
        }

        List<Video> videos = new ArrayList<>();
        int totalVideos = 0;

        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                // Toplam video sayısını al
                String countSql = "SELECT COUNT(*) FROM videos";
                try (PreparedStatement countStmt = conn.prepareStatement(countSql)) {
                    try (ResultSet countRs = countStmt.executeQuery()) {
                        if (countRs.next()) {
                            totalVideos = countRs.getInt(1);
                        }
                    }
                }

                // Sayfalama ile videoları al
                String sql = "SELECT v.id, v.youtubeId, v.title, u.firstName, u.lastName FROM videos v INNER JOIN users u ON v.user_id = u.id ORDER BY v.id DESC LIMIT ? OFFSET ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, VIDEOS_PER_PAGE);
                    pstmt.setInt(2, (currentPage - 1) * VIDEOS_PER_PAGE);

                    try (ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            Video video = new Video();
                            video.setId(rs.getInt("id"));
                            video.setYoutubeId(rs.getString("youtubeId"));
                            video.setTitle(rs.getString("title"));
                            video.setUploaderName(rs.getString("firstName") + " " + rs.getString("lastName"));
                            // Youtube video thumbnail linki
                            video.setThumbnailUrl("https://img.youtube.com/vi/" + video.getYoutubeId() + "/0.jpg");
                            videos.add(video);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Hata mesajını ayarla ve videos.jsp'ye yönlendir
            request.setAttribute("errorMessage", "Videolar alınırken bir hata oluştu: " + e.getMessage());
            request.getRequestDispatcher("videos.jsp").forward(request, response);
            return;
        }

        // Toplam sayfa sayısını hesapla
        int totalPages = (int) Math.ceil((double) totalVideos / VIDEOS_PER_PAGE);

        // Sayfalama bilgilerini ve video listesini request'e ekle
        request.setAttribute("videos", videos);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);

        // Yönlendirmeyi videos.jsp içinde jsp:include ile yaptığımız için burada yönlendirme yapmıyoruz
        // request.getRequestDispatcher("videos.jsp").forward(request, response);
    }
}