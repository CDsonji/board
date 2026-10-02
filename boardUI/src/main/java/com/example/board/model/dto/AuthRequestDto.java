package com.example.board.model.dto;

public class AuthRequestDto {
    private final String username;
    private final String password;

    public AuthRequestDto(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
