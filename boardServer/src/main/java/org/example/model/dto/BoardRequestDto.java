package org.example.model.dto;

public class BoardRequestDto {
    private final String id;

    public BoardRequestDto(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
