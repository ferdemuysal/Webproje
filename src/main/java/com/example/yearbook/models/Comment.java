package com.example.yearbook.models;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Comment {
    private int id;
    private int userId;
    private int commenterId;
    private String commentText;
    private LocalDateTime date;
    private String commenterName; // Yorumu yapan kullanıcının adı
    private String formattedDate;
    private int videoId; // Yorumun hangi videoya ait olduğunu belirtmek için eklendi
    private int photoId; // Yorumun hangi fotografa ait olduğunu belirtmek için
    private String type; // Yorumun türü: 'photo', 'video', ya da 'person'
    private boolean editing; // Yorumun düzenleme modunda olup olmadığını belirten flag

    // Constructor
    public Comment() {
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCommenterId() {
        return commenterId;
    }

    public void setCommenterId(int commenterId) {
        this.commenterId = commenterId;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getCommenterName() {
        return commenterName;
    }

    public void setCommenterName(String commenterName) {
        this.commenterName = commenterName;
    }

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isEditing() {
        return editing;
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
    }

    // Diğer getter ve setter metotları...

    // Türkiye saatine göre formatlanmış tarihi ayarlayan ve döndüren metot
    public String getFormattedDate() {
        // Eğer date null ise, varsayılan bir değer döndür
        if (this.date == null) {
            return "Tarih Bilgisi Yok"; // veya uygun bir varsayılan değer
        }

        ZoneId serverZone = ZoneId.of("UTC"); // Sunucu zaman dilimi (örneğin UTC)
        ZoneId clientZone = ZoneId.of("Europe/Istanbul"); // İstemci zaman dilimi (Türkiye)

        ZonedDateTime serverDateTime = this.date.atZone(serverZone);
        ZonedDateTime clientDateTime = serverDateTime.withZoneSameInstant(clientZone);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return clientDateTime.format(formatter);
    }

    public void setFormattedDate(LocalDateTime date) {
        ZoneId serverZone = ZoneId.of("UTC");
        ZoneId clientZone = ZoneId.of("Europe/Istanbul");

        ZonedDateTime serverDateTime = date.atZone(serverZone);
        ZonedDateTime clientDateTime = serverDateTime.withZoneSameInstant(clientZone);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        this.formattedDate = clientDateTime.format(formatter); // formattedDate değişkenini doldur
    }
}