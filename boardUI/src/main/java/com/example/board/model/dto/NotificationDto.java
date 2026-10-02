package com.example.board.model.dto;

public class NotificationDto {
    private final String notification;

    public NotificationDto(String notification) {
        this.notification = notification;
    }

    public String getNotification() {
        return notification;
    }
}
