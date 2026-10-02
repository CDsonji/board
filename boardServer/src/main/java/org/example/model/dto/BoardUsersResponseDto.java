package org.example.model.dto;

import java.util.ArrayList;

public class BoardUsersResponseDto {
    private final String owner_username;
    private final String owner_id;
    private final ArrayList<String[]> associated_users;


    public BoardUsersResponseDto(String owner_username, String owner_id, ArrayList<String[]> associated_users) {
        this.owner_username = owner_username;
        this.owner_id = owner_id;
        this.associated_users = associated_users;

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

}
