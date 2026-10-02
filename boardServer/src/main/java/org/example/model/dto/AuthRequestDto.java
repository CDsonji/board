package org.example.model.dto;

public class AuthRequestDto {
    private final String username;
    private final String password;

    public AuthRequestDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
