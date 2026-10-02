package com.example.board.model;

import java.util.Date;

public class Task {

    private final String id;
    private final String title;
    private final String description;
    private final String status;
    private final String priority;
    private final Long due_date;

    public static Task currentTask;

    public Task(String id, String title, String description, String status, String priority, long due_date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.due_date = due_date;
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

    public String getId() {
        return id;
    }

}
