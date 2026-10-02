package com.example.board.model.dto;

public class TaskRequestDto {
    private final String task_id;
    private final String task_title;
    private final String board_id;

    private final String board_name;

    public TaskRequestDto(String task_id, String task_title, String board_id, String board_name) {
        this.task_id = task_id;
        this.task_title = task_title;
        this.board_id = board_id;
        this.board_name = board_name;
    }

    public String getTask_id() {
        return task_id;
    }

    public String getBoardId() {
        return board_id;
    }

    public String getBoardName() {
        return board_name;
    }

    public String getTaskTitle() {
        return task_title;
    }
}
