package org.example.model.dto;

public class BoardsRequestDto {
    private String user_id;

    public BoardsRequestDto(String user_id) {
        this.user_id = user_id;
    }

    public String getUserId() {
        return user_id;
    }
}
