package com.example.board.model;

public class Board {
    private final String id;
    private final String name;
    public static Board currentBoard;
    public Board(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
