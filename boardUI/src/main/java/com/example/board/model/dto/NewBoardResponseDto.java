package com.example.board.model.dto;

public class NewBoardResponseDto {
    private final String name;
    private final String id;

    public NewBoardResponseDto(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
