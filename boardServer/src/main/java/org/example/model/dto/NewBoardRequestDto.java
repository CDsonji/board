package org.example.model.dto;

public class NewBoardRequestDto {
    private String name;
    private String ownerId;
    private String username;

    public NewBoardRequestDto(String name,String username, String ownerId) {
        this.name = name;
        this.ownerId = ownerId;
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getUsername() {
        return username;
    }
}
