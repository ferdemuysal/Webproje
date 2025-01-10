package com.example.yearbook.controllers;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.example.yearbook.utils.DBConnection;

@WebServlet(name = "UploadPhotoServlet", value = "/UploadPhotoServlet")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1 MB
        maxFileSize = 1024 * 1024 * 10,      // 10 MB
        maxRequestSize = 1024 * 1024 * 50   // 50 MB
)
public class UploadPhotoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String UPLOAD_DIRECTORY = "uploads";

    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");

        // Kullanıcı oturum açmamışsa, hata mesajı gönder veya giriş sayfasına yönlendir
        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Apache Commons FileUpload ile dosya yükleme işlemleri için
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);

        try {
             //ServletContext context = getServletContext();
             String contextPath = request.getContextPath();
            // Formdaki tüm alanları al (dosyalar dahil)
            List<FileItem> items = upload.parseRequest(request);
            String uploadedFilePath = null;

            for (FileItem item : items) {
                if (!item.isFormField()) {
                    // Dosya alanı
                    String fileName = new File(item.getName()).getName();
                    // Dosya adını benzersiz yap (UUID kullanabilirsiniz)
                    fileName = System.currentTimeMillis() + "_" + fileName;
                    
                    // Sunucu tarafında dosyanın kaydedileceği tam yolu al
                    String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIRECTORY;
                    
                    // Yükleme dizini oluştur
                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) {
                        uploadDir.mkdir();
                    }

                    // Dosyayı sunucuya kaydet
                    String filePath = uploadPath + File.separator + fileName;
                    File storeFile = new File(filePath);
                    item.write(storeFile);

                    // Veritabanına kaydedilecek göreli yol
                    uploadedFilePath = UPLOAD_DIRECTORY + "/" + fileName; // Sadece uploads klasörü ve dosya adı

                    // Debug için dosyanın kaydedildiği yolu yazdır
                    System.out.println("Dosya kaydedildi: " + storeFile.getAbsolutePath());
                    System.out.println("Veritabanına kaydedilecek yol: " + uploadedFilePath);
                    System.out.println("Context Path: " + contextPath);
                }
            }

            // Veritabanına kaydet
            try (Connection conn = DBConnection.getConnection()) {
                if (conn != null) {
                    String sql = "INSERT INTO photos (filePath, user_id) VALUES (?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, uploadedFilePath);
                        pstmt.setInt(2, userId);

                        pstmt.executeUpdate();
                    }
                } else {
                    // Veritabanı bağlantı hatası
                    request.setAttribute("errorMessage", "Veritabanına bağlanılamadı.");
                    request.getRequestDispatcher("photos.jsp").forward(request, response);
                    return;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                // Veritabanı hatası
                request.setAttribute("errorMessage", "Fotoğraf yüklenirken bir hata oluştu: " + e.getMessage());
                request.getRequestDispatcher("photos.jsp").forward(request, response);
                return;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            // Dosya yükleme hatası
            request.setAttribute("errorMessage", "Fotoğraf yüklenirken bir hata oluştu: " + ex.getMessage());
            request.getRequestDispatcher("photos.jsp").forward(request, response);
            return;
        }

        // Fotoğraf başarıyla yüklendi, fotoğraf listesi sayfasına yönlendir
        response.sendRedirect("photos.jsp");
    }
}