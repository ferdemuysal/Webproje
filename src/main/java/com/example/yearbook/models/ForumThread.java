package com.example.yearbook.models;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ForumThread {
    private int id;
    private int userId;
    private String userName;
    private String title;
    private String content;
    private LocalDateTime createdAt; // Tarih bilgisini tutmak için gerekli
    private String formattedDate;
    private boolean editing;

    // Constructor
    public ForumThread() {
    }

    // Getter ve Setter metotları...

    // ... Diğer getter ve setter metotları (id, userId, userName, title, content) ...
    
    // Getter ve setter metotları...

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    // Formatlanmış tarihi ayarlayan ve döndüren metot (Türkiye saati ile)
    public String getFormattedDate() {
        // Sunucu zamanını istemci zamanına donusturmek icin
        ZoneId serverZone = ZoneId.of("UTC"); // Sunucu zaman dilimi (örneğin UTC)
        ZoneId clientZone = ZoneId.of("Europe/Istanbul"); // İstemci zaman dilimi (Türkiye)

        ZonedDateTime serverDateTime = this.createdAt.atZone(serverZone);
        ZonedDateTime clientDateTime = serverDateTime.withZoneSameInstant(clientZone);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return clientDateTime.format(formatter);
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }
    
    public void setFormattedDate(LocalDateTime date) {
        // Sunucu zamanını istemci zamanına donusturmek icin
        ZoneId serverZone = ZoneId.of("UTC"); // Sunucu zaman dilimi (örneğin UTC)
        ZoneId clientZone = ZoneId.of("Europe/Istanbul"); // İstemci zaman dilimi (Türkiye)

        ZonedDateTime serverDateTime = date.atZone(serverZone);
        ZonedDateTime clientDateTime = serverDateTime.withZoneSameInstant(clientZone);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        this.formattedDate = clientDateTime.format(formatter);
    }

    public String getAuthorName() {
        return userName;
    }

    public void setAuthorName(String authorName) {
        this.userName = authorName;
    }
    
    // Düzenleme durumunu ayarlamak için
    public boolean isEditing() {
        return editing;
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
    }
}