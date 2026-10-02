package com.example.board.model;

public class User {
    private final String username;
    private final String id;

    public static User currentUser;

    public User(String username, String id) {
        this.username = username;
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getId() {
        return id;
    }
}
