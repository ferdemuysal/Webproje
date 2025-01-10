package com.example.yearbook.models;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ForumPost {
    private int id;
    private int userId;
    private String userName;
    private String message;
    private LocalDateTime date;
    private String formattedDate;

    // Constructor

    public ForumPost() {
    }

    public ForumPost(int id, int userId, String message, LocalDateTime date) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.date = date;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
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

        ZonedDateTime serverDateTime = this.date.atZone(serverZone);
        ZonedDateTime clientDateTime = serverDateTime.withZoneSameInstant(clientZone);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return clientDateTime.format(formatter);
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
}