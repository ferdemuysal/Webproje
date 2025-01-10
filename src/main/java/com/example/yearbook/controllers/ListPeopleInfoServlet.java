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

import com.example.yearbook.models.User;
import com.example.yearbook.utils.DBConnection;

@WebServlet(name = "ListPeopleInfoServlet", value = "ListPeopleInfoServlet")
public class ListPeopleInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<User> users = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                String sql = "SELECT * FROM users"; // Tüm kullanıcı bilgilerini al
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    try (ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            User user = new User();
                            user.setId(rs.getInt("id"));
                            user.setFirstName(rs.getString("firstName"));
                            user.setLastName(rs.getString("lastName"));
                            user.setCity(rs.getString("city"));
                            user.setCurrentSchool(rs.getString("currentSchool"));
                            user.setCurrentJob(rs.getString("currentJob"));
                            user.setEmail(rs.getString("email"));
                            user.setWebsite(rs.getString("website"));
                            user.setFacebookId(rs.getString("facebookId"));
                            user.setTwitterId(rs.getString("twitterId"));
                            // İhtiyacınız olan diğer alanları da ekleyin
                            users.add(user);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Hata mesajını ayarla ve whoswhere.jsp'ye yönlendir
            request.setAttribute("errorMessage", "Kullanıcı bilgileri alınırken bir hata oluştu: " + e.getMessage());
            request.getRequestDispatcher("whoswhere.jsp").forward(request, response);
            return;
        }

        // Kullanıcı listesini request'e ekle
        request.setAttribute("users", users);

        // Yönlendirmeyi whoswhere.jsp içinde jsp:include ile yaptığımız için burada yönlendirme yapmıyoruz
        // request.getRequestDispatcher("whoswhere.jsp").forward(request, response);
    }
}