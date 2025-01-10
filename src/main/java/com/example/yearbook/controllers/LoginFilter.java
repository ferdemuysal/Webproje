package com.example.yearbook.controllers;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter(filterName = "LoginFilter", urlPatterns = {"/forum.jsp", "/people.jsp", "/photos.jsp", "/videos.jsp", "/whoswhere.jsp", "/map.jsp", "/person.jsp", "/photo.jsp", "/video.jsp"})
public class LoginFilter implements Filter {

    @Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loggedIn") == null || !((Boolean) session.getAttribute("loggedIn"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp"); // Giriş sayfasına yönlendir
        } else {
            chain.doFilter(req, res); // İsteği işleme devam et
        }
    }

    @Override
	public void init(FilterConfig config) throws ServletException {
        // Filtre başlatıldığında yapılacak işlemler (gerekirse)
    }

    @Override
	public void destroy() {
        // Filtre yok edildiğinde yapılacak işlemler (gerekirse)
    }
}