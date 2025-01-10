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

@WebServlet(name = "ListPeopleServlet", value = "/ListPeopleServlet")
public class ListPeopleServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    private static final int PEOPLE_PER_PAGE = 9; //

    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Sayfalama için mevcut sayfa numarasını al
        int currentPage = 1;
        if (request.getParameter("page") != null) {
            currentPage = Integer.parseInt(request.getParameter("page"));
        }

        List<User> people = new ArrayList<>();
        int totalPeople = 0;

        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                // Toplam kişi sayısını al
                String countSql = "SELECT COUNT(*) FROM users";
                try (PreparedStatement countStmt = conn.prepareStatement(countSql)) {
                    try (ResultSet countRs = countStmt.executeQuery()) {
                        if (countRs.next()) {
                            totalPeople = countRs.getInt(1);
                        }
                    }
                }

                // Sayfalama ile kişileri al
                String sql = "SELECT id, firstName, lastName, profilePicture FROM users ORDER BY firstName, lastName LIMIT ? OFFSET ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, PEOPLE_PER_PAGE);
                    pstmt.setInt(2, (currentPage - 1) * PEOPLE_PER_PAGE);

                    try (ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            User person = new User();
                            person.setId(rs.getInt("id"));
                            person.setFirstName(rs.getString("firstName"));
                            person.setLastName(rs.getString("lastName"));
                            person.setProfilePicture(rs.getString("profilePicture"));
                            people.add(person);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Hata mesajını ayarla ve people.jsp'ye yönlendir
            request.setAttribute("errorMessage", "Kişiler alınırken bir hata oluştu: " + e.getMessage());
            request.getRequestDispatcher("people.jsp").forward(request, response);
            return;
        }

        // Toplam sayfa sayısını hesapla
        int totalPages = (int) Math.ceil((double) totalPeople / PEOPLE_PER_PAGE);

        // Sayfalama bilgilerini ve kişi listesini request'e ekle
        request.setAttribute("people", people);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);

        // people.jsp'ye yönlendir (burada yönlendirme yapmıyoruz, çünkü jsp:include kullandık)
        // request.getRequestDispatcher("people.jsp").forward(request, response);
    }
}