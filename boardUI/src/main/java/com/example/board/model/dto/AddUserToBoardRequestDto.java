package com.example.board.model.dto;

public class AddUserToBoardRequestDto {
    private final String board_id;
    private final String board_name;
    private final String username;

    public AddUserToBoardRequestDto(String board_id, String username, String board_name) {
        this.board_id = board_id;
        this.board_name = board_name;
        this.username = username;
    }

    public String getBoardId() {
        return board_id;
    }

    public String getUsername() {
        return username;
    }

    public String getBoardName() {
        return board_name;
    }
}
