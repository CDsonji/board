package org.example.model.dto;

public class UpdateTaskRequest {

    private final String board_name;
    private final String board_id;
    private final String task_id;
    private final String title;
    private final String original_title;
    private final String description;
    private final String status;
    private final String priority;
    private final long due_date;

    public UpdateTaskRequest(String board_name, String board_id, String task_id, String title, String original_title, String description, String status, String priority, long due_date) {
        this.board_name = board_name;
        this.board_id = board_id;
        this.task_id = task_id;
        this.title = title;
        this.original_title = original_title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.due_date = due_date;
    }

    public String getTask_id() {
        return task_id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getPriority() {
        return priority;
    }

    public long getDue_date() {
        return due_date;
    }

    public String getOriginalTitle() {
        return original_title;
    }

    public String getBoardName() {
        return board_name;
    }

    public String getBoardId() {
        return board_id;
    }
}
