package org.example.model.dto;

import java.util.ArrayList;

public class BoardsResponseDto {
    private final ArrayList<String[]> boards;

    public BoardsResponseDto(ArrayList<String[]> boards) {
        this.boards = boards;
    }

    public ArrayList<String[]> getBoards() {
        return boards;
    }
}
