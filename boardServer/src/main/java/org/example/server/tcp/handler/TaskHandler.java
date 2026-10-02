package org.example.server.tcp.handler;

import org.example.database.Database;
import org.example.model.Task;
import org.example.model.dto.*;
import org.example.model.message.Request;
import org.example.model.message.Response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.UUID;

public class TaskHandler {
    public synchronized static Response<?> handleAddTask(Request<AddTaskRequestDto> request) throws Exception {
        String title = request.getPayload().getTitle();
        String description = request.getPayload().getDescription();
        String board_id = request.getPayload().getBoardId();
        String board_name = request.getPayload().getBoardName();
        String priority = request.getPayload().getPriority();
        long due_date = request.getPayload().getDue_date();
        String id = UUID.randomUUID().toString();

        Connection conn = Database.getConnection();
        String query = "INSERT INTO tasks(id, board_id, title, description, priority, due_date, updated_at) VALUES(?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, id);
            stmt.setString(2, board_id);
            stmt.setString(3, title);
            stmt.setString(4, description);
            stmt.setString(5, priority);
            stmt.setLong(6, due_date);
            stmt.setLong(7, System.currentTimeMillis());
            stmt.executeUpdate();
        } catch (Exception e) {
            return new Response<>("error", null, e.getMessage());
        }
        NotificationHandler.sendNotifications(board_id,"Task "+title+" Added To Board "+board_name);
        return new Response<>("ok", null, "Task " + title + " Added To Board");
    }

    public synchronized static Response<BoardTasksResponseDto> handleTasksList(Request<BoardRequestDto> request) throws Exception {
        String board_id = request.getPayload().getId();

        Connection conn = Database.getConnection();
        String query = "SELECT id, title, status, priority FROM tasks WHERE board_id = ? ORDER BY created_at DESC";
        ArrayList<String[]> tasks = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, board_id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String id = rs.getString("id");
                String title = rs.getString("title");
                String status = rs.getString("status");
                String priority = rs.getString("priority");
                tasks.add(new String[]{title, id, status, priority});
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Response<>("error", null, e.getMessage());
        }

        return new Response<>("ok", new BoardTasksResponseDto(tasks), "Tasks Loaded Successfully");

    }

    public synchronized static Response<TaskResponseDto> handleViewTask(Request<TaskRequestDto> request) throws Exception {
        String task_id = request.getPayload().getTask_id();

        String query = "SELECT title, description, status, priority, due_date FROM tasks WHERE id = ? ";
        Connection conn = Database.getConnection();
        TaskResponseDto dto = null;
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, task_id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String title = rs.getString("title");
                String description = rs.getString("description");
                String status = rs.getString("status");
                String priority = rs.getString("priority");
                long due_date = rs.getLong("due_date");
                dto = new TaskResponseDto(task_id, title, description, status, priority, due_date);

            } else {
                return new Response<>("error", null, "Task Not Found");
            }
        } catch (Exception e) {
            return new Response<>("error", null, e.getMessage());
        }
        return new Response<>("ok", dto, "Task Loaded Successfully");
    }

    public synchronized static Response<?> handleUpdateTask(Request<UpdateTaskRequest> request) throws Exception {

        Task task = new Task(request.getPayload().getTask_id(),
                request.getPayload().getTitle(),
                request.getPayload().getDescription(),
                request.getPayload().getStatus(),
                request.getPayload().getPriority(),
                request.getPayload().getDue_date());

        ArrayList<String> updates = new ArrayList<>();
        ArrayList<String> parameters = new ArrayList<>();
        boolean contains_due_date = false;
        if (!task.getTitle().isEmpty()) {
            updates.add("title = ? ");
            parameters.add(task.getTitle());
        }
        if (!task.getDescription().isEmpty()) {
            updates.add("description = ? ");
            parameters.add(task.getDescription());
        }
        if (!task.getStatus().isEmpty()) {
            updates.add("status = ? ");
            parameters.add(task.getStatus());
        }
        if (!task.getPriority().isEmpty()) {
            updates.add("priority = ? ");
            parameters.add(task.getPriority());
        }
        if (task.getDue_date() != 0) {
            updates.add("due_date = ?");
            contains_due_date = true;
        }
        String query = "UPDATE tasks SET " + String.join(", ", updates) + ", updated_at = ? " + "WHERE id = ? ";

        Connection conn = Database.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            int j = 0;
            for (String parameter : parameters) {
                j++;
                stmt.setString(j, parameter);
            }
            if (contains_due_date) {
                j++;
                stmt.setLong(j, task.getDue_date());
            }
            j++;
            stmt.setLong(j, System.currentTimeMillis());
            j++;
            stmt.setString(j, task.getId());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return new Response<>("error", null, e.getMessage());
        }
        NotificationHandler.sendNotifications(request.getPayload().getBoardId(), "Task " + request.getPayload().getOriginalTitle() + " From Board " + request.getPayload().getBoardName() + " Updated");
        return new Response<>("ok", null, "Task " + task.getTitle() + " Edited");
    }

    public synchronized static Response<BoardTasksResponseDto> handleDeleteTask(Request<TaskRequestDto> request) throws Exception {

        String task_id = request.getPayload().getTask_id();

        Connection conn = Database.getConnection();
        String query = "DELETE FROM tasks WHERE id = ?";
        ArrayList<String[]> tasks = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, task_id);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return new Response<>("error", null, e.getMessage());
        }
        NotificationHandler.sendNotifications(request.getPayload().getBoardId(), "Task " + request.getPayload().getTaskTitle() + " Deleted From Board " + request.getPayload().getBoardName() );
        return new Response<>("ok", new BoardTasksResponseDto(tasks), "Task Deleted Successfully");

    }
}
