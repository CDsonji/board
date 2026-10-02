package com.example.board.model.dto;

public class AuthResponseDto {
    private final String username;
    private final String id;

    public AuthResponseDto(String username, String id) {
        this.username = username;
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getId() {
        return id;
    }
}
