package com.example.yearbook.controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "LogoutServlet", value = "/LogoutServlet")
public class LogoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false); // Mevcut oturumu al, yoksa yeni bir tane oluşturma
        if (session != null) {
            session.invalidate(); // Oturumu geçersiz kıl
        }
        response.sendRedirect("index.jsp"); // Anasayfaya yönlendir
    }
}