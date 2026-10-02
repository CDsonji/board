package com.example.board.model.dto;

import java.util.ArrayList;

public class BoardUsersResponseDto {
    private final String owner_username;
    private final String owner_id;
    private final ArrayList<String[]> associated_users;
    private final ArrayList<String[]> tasks;

    public BoardUsersResponseDto(String owner_username, String owner_id, ArrayList<String[]> associated_users, ArrayList<String[]> tasks) {
        this.owner_username = owner_username;
        this.owner_id = owner_id;
        this.associated_users = associated_users;
        this.tasks = tasks;
    }

    public String getOwnerUsername() {
        return owner_username;
    }

    public String getOwnerId() {
        return owner_id;
    }

    public ArrayList<String[]> getAssociatedUsers() {
        return associated_users;
    }

    public ArrayList<String[]> getTasks() {
        return tasks;
    }
}
