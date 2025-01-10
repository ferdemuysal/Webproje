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

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.mindrot.jbcrypt.BCrypt;

import com.example.yearbook.models.User;
import com.example.yearbook.utils.DBConnection;

@WebServlet(name = "RegisterServlet", value = "/RegisterServlet")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1 MB
        maxFileSize = 1024 * 1024 * 10,      // 10 MB
        maxRequestSize = 1024 * 1024 * 100   // 100 MB
)
public class RegisterServlet extends HttpServlet {

    private static final String UPLOAD_DIRECTORY = "/uploads";
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Hata mesajını saklamak için bir değişken
        String errorMessage = null;

        // Apache Commons FileUpload ile dosya yükleme işlemleri için
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);

        try {
            // Formdaki tüm alanları al (dosyalar dahil)
            List<FileItem> items = upload.parseRequest(request);
            User user = new User();
            String uploadedFilePath = null;

            for (FileItem item : items) {
                if (item.isFormField()) {
                    // Normal form alanı (dosya değil)
                    String fieldName = item.getFieldName();
                    String fieldValue = item.getString("UTF-8");

                    // Form alanlarını User nesnesine ata
                    switch (fieldName) {
                        case "firstName":
                            user.setFirstName(fieldValue);
                            break;
                        case "lastName":
                            user.setLastName(fieldValue);
                            break;
                        case "email":
                            user.setEmail(fieldValue);
                            break;
                        case "password":
                            // Şifreyi hashle
                            String hashedPassword = BCrypt.hashpw(fieldValue, BCrypt.gensalt());
                            user.setPassword(hashedPassword);
                            break;
                        case "city":
                            user.setCity(fieldValue);
                            break;
                        case "gender":
                            user.setGender(fieldValue);
                            break;
                        case "hobbies":
                            // Önceki hobileri temizle ve yenilerini ekle
                            if(user.getHobbies() != null){
                                user.setHobbies(user.getHobbies() + "," + fieldValue);
                            } else {
                                user.setHobbies(fieldValue);
                            }
                            break;
                        case "currentSchool":
                            user.setCurrentSchool(fieldValue);
                            break;
                        case "currentJob":
                            user.setCurrentJob(fieldValue);
                            break;
                        case "website":
                            user.setWebsite(fieldValue);
                            break;
                        case "facebookId":
                            user.setFacebookId(fieldValue);
                            break;
                        case "twitterId":
                            user.setTwitterId(fieldValue);
                            break;
                    }
                } else {
                    // Dosya alanı
                    String fieldName = item.getFieldName();
                    if (fieldName.equals("profilePicture")) {
                        // Dosya adını benzersiz yap (UUID kullanabilirsiniz)
                        String fileName = System.currentTimeMillis() + "_" + item.getName();
                        // Yükleme dizininin tam yolunu al
                        String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIRECTORY;
                        File uploadDir = new File(uploadPath);
                        if (!uploadDir.exists()) {
                            uploadDir.mkdir();
                        }
                        // Dosyayı sunucuya kaydet
                        String filePath = uploadPath + File.separator + fileName;
                        File storeFile = new File(filePath);
                        item.write(storeFile);

                        uploadedFilePath = UPLOAD_DIRECTORY + File.separator + fileName; // Veritabanına kaydetmek için göreli yol
                    }
                }
            }

            // Veritabanına kaydet
            try (Connection conn = DBConnection.getConnection()) {
                if (conn != null) {
                    String sql = "INSERT INTO users (firstName, lastName, email, password, city, gender, hobbies, profilePicture, currentSchool, currentJob, website, facebookId, twitterId) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, user.getFirstName());
                    pstmt.setString(2, user.getLastName());
                    pstmt.setString(3, user.getEmail());
                    pstmt.setString(4, user.getPassword());
                    pstmt.setString(5, user.getCity());
                    pstmt.setString(6, user.getGender());
                    pstmt.setString(7, user.getHobbies());
                    pstmt.setString(8, uploadedFilePath);
                    pstmt.setString(9, user.getCurrentSchool());
                    pstmt.setString(10, user.getCurrentJob());
                    pstmt.setString(11, user.getWebsite());
                    pstmt.setString(12, user.getFacebookId());
                    pstmt.setString(13, user.getTwitterId());

                    pstmt.executeUpdate();

                    // Kayıt başarılı, anasayfaya yönlendir
                    response.sendRedirect("index.jsp");
                    return; // Yönlendirmeden sonra servlet'in çalışmasını durdur
                } else {
                    errorMessage = "Veritabanına bağlanılamadı.";
                }
            } catch (SQLException e) {
                e.printStackTrace();
                errorMessage = "Veritabanı hatası: " + e.getMessage();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            errorMessage = "Dosya yükleme hatası: " + ex.getMessage();
        }

        // Hata durumunda, hata mesajını request'e ekle ve register.jsp'ye geri gönder
        request.setAttribute("errorMessage", errorMessage);
        request.getRequestDispatcher("register.jsp").forward(request, response);
    }
}