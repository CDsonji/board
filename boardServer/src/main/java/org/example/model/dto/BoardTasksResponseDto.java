package org.example.model.dto;

import java.util.ArrayList;

public class BoardTasksResponseDto {
    private final ArrayList<String[]> tasks;

    public BoardTasksResponseDto(ArrayList<String[]> tasks) {
        this.tasks = tasks;
    }


    public ArrayList<String[]> getTasks() {
        return tasks;
    }
}
