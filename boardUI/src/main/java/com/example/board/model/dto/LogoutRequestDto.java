package com.example.board.model.dto;

public class LogoutRequestDto {
    private final String id;

    public LogoutRequestDto(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
