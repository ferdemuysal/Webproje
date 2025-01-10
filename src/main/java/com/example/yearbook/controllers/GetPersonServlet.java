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
import com.example.yearbook.models.User;
import com.example.yearbook.utils.DBConnection;

@WebServlet(name = "GetPersonServlet", value = "/GetPersonServlet")
public class GetPersonServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int personId = Integer.parseInt(request.getParameter("id"));

        User person = null;
        List<Comment> comments = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                // Kişi bilgilerini al
                String personSql = "SELECT * FROM users WHERE id = ?";
                try (PreparedStatement personStmt = conn.prepareStatement(personSql)) {
                    personStmt.setInt(1, personId);
                    try (ResultSet personRs = personStmt.executeQuery()) {
                        if (personRs.next()) {
                            person = new User();
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

                // Kişiye ait yorumları al
                String commentsSql = "SELECT c.commentText, c.type, c.date, u.firstName, u.lastName, c.id FROM comments c INNER JOIN users u ON c.commenter_id = u.id WHERE c.user_id = ? AND c.type = 'person' ORDER BY c.date DESC";
                try (PreparedStatement commentsStmt = conn.prepareStatement(commentsSql)) {
                    commentsStmt.setInt(1, personId);
                    try (ResultSet commentsRs = commentsStmt.executeQuery()) {
                        while (commentsRs.next()) {
                            Comment comment = new Comment();
                            comment.setId(commentsRs.getInt("id"));
                            comment.setCommentText(commentsRs.getString("commentText"));
                            comment.setFormattedDate(commentsRs.getTimestamp("date").toLocalDateTime());
                            comment.setCommenterName(commentsRs.getString("firstName") + " " + commentsRs.getString("lastName"));
                            comment.setType(commentsRs.getString("type"));
                            comment.setDate(commentsRs.getTimestamp("date").toLocalDateTime());
                            comment.setFormattedDate(commentsRs.getTimestamp("date").toLocalDateTime());
                            comments.add(comment);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Hata mesajını ayarla ve person.jsp'ye yönlendir
            request.setAttribute("errorMessage", "Kişi bilgileri alınırken bir hata oluştu: " + e.getMessage());
            request.getRequestDispatcher("person.jsp").forward(request, response);
            return;
        }

        // Kişi ve yorum bilgilerini request'e ekle
        request.setAttribute("person", person);
        request.setAttribute("comments", comments);

        // Yönlendirmeyi person.jsp içinde jsp:include ile yaptığımız için burada yönlendirme yapmıyoruz
        // request.getRequestDispatcher("person.jsp").forward(request, response);
    }
}