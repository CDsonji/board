package org.example.model.dto;

public class AuthResponseDto {
    private final String username;
    private final String id;

    public AuthResponseDto(String username, String id) {
        this.username = username;
        this.id = id;
    }
}
