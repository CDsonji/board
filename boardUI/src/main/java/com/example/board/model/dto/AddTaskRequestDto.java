package com.example.board.model.dto;

public class AddTaskRequestDto {
    private final String title;
    private final String description;
    private final String priority;
    private final String board_id;
    private final String board_name;
    private final long due_date;

    public AddTaskRequestDto(String title, String description, String priority, String board_id,String board_name, long due_date) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.board_id = board_id;
        this.board_name = board_name;
        this.due_date = due_date;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPriority() {
        return priority;
    }

    public String getBoardId() {
        return board_id;
    }

    public String getBoardName() {
        return board_name;
    }

    public long getDue_date() {
        return due_date;
    }
}
