package com.example.board.controllers.task;

import com.example.board.Main;
import com.example.board.controllers.board.BoardController;
import com.example.board.model.Board;
import com.example.board.model.Task;
import com.example.board.model.dto.TaskRequestDto;
import com.example.board.model.dto.TaskResponseDto;
import com.example.board.model.dto.UpdateTaskRequestDto;
import com.example.board.model.message.Request;
import com.example.board.model.message.Response;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.io.File;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ResourceBundle;

public class TaskController implements Initializable {
    @FXML
    Button view_tasks;
    @FXML
    Button delete;
    @FXML
    Button edit_description;
    @FXML
    Button edit_title;
    @FXML
    Label title;
    @FXML
    Label board;
    @FXML
    TextArea description;
    @FXML
    RadioButton low_priority;
    @FXML
    RadioButton medium_priority;
    @FXML
    RadioButton high_priority;
    @FXML
    RadioButton todo_status;
    @FXML
    RadioButton in_progress_status;
    @FXML
    RadioButton done_status;
    @FXML
    DatePicker due_date;
    @FXML
    public Label notification;
    @FXML
    Label reaction;
    @FXML
    Pane pane;
    private ToggleGroup priority_group;
    private ToggleGroup status_group;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        priority_group = new ToggleGroup();
        low_priority.setToggleGroup(priority_group);
        low_priority.setOnAction(e -> onPriorityButtonClick("low"));
        medium_priority.setToggleGroup(priority_group);
        medium_priority.setOnAction(e -> onPriorityButtonClick("medium"));
        high_priority.setToggleGroup(priority_group);
        high_priority.setOnAction(e -> onPriorityButtonClick("high"));

        status_group = new ToggleGroup();
        todo_status.setToggleGroup(status_group);
        todo_status.setOnAction(e -> onStatusButtonClick("todo"));
        in_progress_status.setToggleGroup(status_group);
        in_progress_status.setOnAction(e -> onStatusButtonClick("in_progress"));
        done_status.setToggleGroup(status_group);
        done_status.setOnAction(e -> onStatusButtonClick("done"));

        loadTask();

        view_tasks.setOnAction(e -> onViewTasksClick());
        delete.setOnAction(e -> onDeleteTaskClick());
        edit_description.setOnAction(e -> onEditDescriptionClick());
        edit_title.setOnAction(e -> onEditTitleClick());

        due_date.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                onPickedDueDate();
            }
        });

    }

    public void loadTask() {
        Request<TaskRequestDto> request = new Request<>("view_task", new TaskRequestDto(Task.currentTask.getId(),null,null,null));
        Response<TaskResponseDto> response = Main.client.sendRequest(request, TaskResponseDto.class);
        if (response.getStatus().equals("ok")) {
            Task.currentTask = new Task(response.getData().getId(),
                    response.getData().getTitle(),
                    response.getData().getDescription(),
                    response.getData().getStatus(),
                    response.getData().getPriority(),
                    response.getData().getDue_date());
            board.setText(Board.currentBoard.getName());
            description.setText(Task.currentTask.getDescription());
            title.setText(Task.currentTask.getTitle());
            switch (Task.currentTask.getStatus()) {
                case "todo" -> todo_status.setSelected(true);
                case "in_progress" -> in_progress_status.setSelected(true);
                case "done" -> done_status.setSelected(true);
            }
            switch (Task.currentTask.getPriority()) {
                case "low" -> low_priority.setSelected(true);
                case "medium" -> medium_priority.setSelected(true);
                case "high" -> high_priority.setSelected(true);
            }
            if (Task.currentTask.getDue_date() != 0) {
                Instant instant = Instant.ofEpochMilli(Task.currentTask.getDue_date());
                LocalDate date = instant.atZone(ZoneId.systemDefault()).toLocalDate();
                due_date.setValue(date);
            } else {
                due_date.setValue(null);
            }

        } else if (response.getStatus().equals("error")) {

        }
    }

    private void onViewTasksClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(new File("./src/main/resources/com/example/board/board-view.fxml").toURI().toURL());
            Scene Menu = new Scene(fxmlLoader.load());
            BoardController controller = fxmlLoader.getController();
            controller.name.setText(Board.currentBoard.getName());
            Main.primaryStage.setScene(Menu);
            Main.currentController = fxmlLoader.getController();
            Task.currentTask = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onDeleteTaskClick() {
        Request<TaskRequestDto> request = new Request<>("delete_task", new TaskRequestDto(Task.currentTask.getId(),Task.currentTask.getTitle(), Board.currentBoard.getId(), Board.currentBoard.getName()));
        Response<Object> response = Main.client.sendRequest(request, Object.class);
        if (response.getStatus().equals("ok")) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(new File("./src/main/resources/com/example/board/board-view.fxml").toURI().toURL());
                Scene Menu = new Scene(fxmlLoader.load());
                BoardController controller = fxmlLoader.getController();
                controller.name.setText(Board.currentBoard.getName());
                Main.primaryStage.setScene(Menu);
                Main.currentController = fxmlLoader.getController();
                controller.task_reaction.setStyle("-fx-background-color: lightgreen; -fx-label-padding: 4;");
                controller.task_reaction.setText("Task " + Task.currentTask.getTitle() + " Deleted Successfully");
                Task.currentTask = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (response.getStatus().equals("error")) {
            reaction.setStyle("-fx-background-color: red; -fx-label-padding: 4;");
            reaction.setText(response.getMessage());
        }
    }

    private void onEditDescriptionClick() {
        if (description.getText().isEmpty()) {
            reaction.setStyle("-fx-background-color: red; -fx-label-padding: 4;");
            reaction.setText("Description Cannot Be Empty");
            return;
        }
        if (description.getText().equals(Task.currentTask.getDescription())) {
            reaction.setStyle("-fx-background-color: red; -fx-label-padding: 4;");
            reaction.setText("Description Hasn't Changed To Be Edited");
            return;
        }
        Request<UpdateTaskRequestDto> request = new Request<>("update_task", new UpdateTaskRequestDto(Board.currentBoard.getName(),Board.currentBoard.getId(), Task.currentTask.getId(), "", Task.currentTask.getTitle(),description.getText(), "", "", 0));        Response<Object> response = Main.client.sendRequest(request, Object.class);
        if (response.getStatus().equals("ok")) {
            loadTask();
            reaction.setStyle("-fx-background-color: lightgreen; -fx-label-padding: 4;");
            reaction.setText("Description Edited Successfully");
        } else if (response.getStatus().equals("error")) {
            loadTask();
            reaction.setStyle("-fx-background-color: lightgreen; -fx-label-padding: 4;");
            reaction.setText(response.getMessage());
        }
    }

    private void onEditTitleClick() {
        if (title.getText().isEmpty()) {
            reaction.setStyle("-fx-background-color: red; -fx-label-padding: 4;");
            reaction.setText("Description Cannot Be Empty");
            return;
        }
        if (title.getText().equals(Task.currentTask.getTitle())) {
            reaction.setStyle("-fx-background-color: red; -fx-label-padding: 4;");
            reaction.setText("Title Hasn't Changed To Be Edited");
            return;
        }
        Request<UpdateTaskRequestDto> request = new Request<>("update_task", new UpdateTaskRequestDto(Board.currentBoard.getName(),Board.currentBoard.getId(), Task.currentTask.getId(), title.getText(), Task.currentTask.getTitle(), "", "", "", 0));
        Response<Object> response = Main.client.sendRequest(request, Object.class);
        if (response.getStatus().equals("ok")) {
            loadTask();
            reaction.setStyle("-fx-background-color: lightgreen; -fx-label-padding: 4;");
            reaction.setText("Title Changed Successfully");
        } else if (response.getStatus().equals("error")) {
            loadTask();
            reaction.setStyle("-fx-background-color: lightgreen; -fx-label-padding: 4;");
            reaction.setText(response.getMessage());
        }
    }

    private void onStatusButtonClick(String status) {
        Request<UpdateTaskRequestDto> request = new Request<>("update_task", new UpdateTaskRequestDto(Board.currentBoard.getName(),Board.currentBoard.getId(), Task.currentTask.getId(), "", Task.currentTask.getTitle(), "", status, "", 0));
        Response<Object> response = Main.client.sendRequest(request, Object.class);
        if (response.getStatus().equals("ok")) {
            reaction.setStyle("-fx-background-color: lightgreen; -fx-label-padding: 4;");
        } else if (response.getStatus().equals("error")) {
            reaction.setStyle("-fx-background-color: red; -fx-label-padding: 4;");
        }
        loadTask();
        reaction.setText(response.getMessage());
    }

    private void onPriorityButtonClick(String priority) {
        Request<UpdateTaskRequestDto> request = new Request<>("update_task", new UpdateTaskRequestDto(Board.currentBoard.getName(),Board.currentBoard.getId(), Task.currentTask.getId(), "", Task.currentTask.getTitle(), "", "", priority, 0));
        Response<Object> response = Main.client.sendRequest(request, Object.class);
        if (response.getStatus().equals("ok")) {
            reaction.setStyle("-fx-background-color: lightgreen; -fx-label-padding: 4;");
        } else if (response.getStatus().equals("error")) {
            reaction.setStyle("-fx-background-color: red; -fx-label-padding: 4;");
        }
        loadTask();
        reaction.setText(response.getMessage());
    }

    private void onPickedDueDate() {
        LocalDate localDate = due_date.getValue();
        long millis = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        if (millis == Task.currentTask.getDue_date()) {
            reaction.setStyle("-fx-background-color: red; -fx-label-padding: 4;");
            reaction.setText("Due Date Hasn't Changed To Be Edited");
        } else {
            Request<UpdateTaskRequestDto> request = new Request<>("update_task", new UpdateTaskRequestDto(Board.currentBoard.getName(),Board.currentBoard.getId(), Task.currentTask.getId(), "", Task.currentTask.getTitle(), "", "", "", millis));
            Response<Object> response = Main.client.sendRequest(request, Object.class);
            if (response.getStatus().equals("ok")) {
                reaction.setStyle("-fx-background-color: lightgreen; -fx-label-padding: 4;");
                reaction.setText("Due Date Changed Successfully");
            } else if (response.getStatus().equals("error")) {
                reaction.setStyle("-fx-background-color: red; -fx-label-padding: 4;");
                reaction.setText(response.getMessage());
            }
            loadTask();
        }
    }


}

