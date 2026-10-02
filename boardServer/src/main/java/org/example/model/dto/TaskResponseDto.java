package org.example.model.dto;

public class TaskResponseDto {
    private final String id;
    private final String title;
    private final String description;
    private final String status;
    private final String priority;
    private final Long due_date;

    public TaskResponseDto(String id, String title, String description, String status, String priority, Long due_date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.due_date = due_date;
    }

    public String getId() {
        return id;
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

    public Long getDue_date() {
        return due_date;
    }
}
