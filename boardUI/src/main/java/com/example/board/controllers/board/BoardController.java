package com.example.board.controllers.board;

import com.example.board.Main;
import com.example.board.model.Board;
import com.example.board.model.Task;
import com.example.board.model.User;
import com.example.board.model.dto.*;
import com.example.board.model.message.Request;
import com.example.board.model.message.Response;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class BoardController implements Initializable {
    @FXML
    Pane pane;
    @FXML
    Button back;
    @FXML
    public Label name;
    @FXML
    Label owner_username;
    @FXML
    Label owner_id;
    @FXML
    public Label notification;
    @FXML
    Button add_user;
    @FXML
    Button add_task;
    @FXML
    TextField new_user;
    @FXML
    Label user_reaction;
    @FXML
    public Label task_reaction;
    @FXML
    VBox tasks;
    @FXML
    VBox users;
    @FXML
    TextField title;
    @FXML
    TextArea description;
    @FXML
    DatePicker due_date;
    @FXML
    RadioButton low_priority;
    @FXML
    RadioButton medium_priority;
    @FXML
    RadioButton high_priority;

    private ToggleGroup priority;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        back.setOnAction(e -> onBackClick());
        add_user.setOnAction(e -> onAddUserClick());
        add_task.setOnAction(e -> onAddTaskClick());
        priority = new ToggleGroup();
        low_priority.setToggleGroup(priority);
        high_priority.setToggleGroup(priority);
        medium_priority.setToggleGroup(priority);
        tasks.setSpacing(10);
        users.setSpacing(10);
        loadBoardTasks();
        loadBoardUsers();
    }

    private void onBackClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(new File("./src/main/resources/com/example/board/profile-view.fxml").toURI().toURL());
            Scene profilePage = new Scene(fxmlLoader.load());
            Main.primaryStage.setScene(profilePage);
            Main.currentController = fxmlLoader.getController();
            ProfileController controller = fxmlLoader.getController();
            controller.username.setText(User.currentUser.getUsername());
            controller.id.setText(User.currentUser.getId());
            Board.currentBoard = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadBoardTasks() {
        Request<BoardRequestDto> request = new Request<>("load_board_tasks", new BoardRequestDto(Board.currentBoard.getId()));
        Response<BoardTasksResponseDto> response = Main.client.sendRequest(request, BoardTasksResponseDto.class);
        if (response.getStatus().equals("ok")) {
            for (String[] task_data : response.getData().getTasks()) {

                String title = task_data[0];
                String task_id = task_data[1];
                String task_priority = task_data[3];
                // Task title
                Label t = new Label(title + "   >");
                t.setOnMouseClicked(e -> onTaskClick(task_id));

                // Create radio buttons
                RadioButton todoBtn = new RadioButton("todo");
                RadioButton doneBtn = new RadioButton("done");
                RadioButton inProgressBtn = new RadioButton("in progress");

                Button delete = new Button("delete");
                delete.setOnAction(e -> onDeleteTaskClick(task_id, title));

                // ToggleGroup for exclusivity
                ToggleGroup group = new ToggleGroup();
                todoBtn.setToggleGroup(group);
                todoBtn.setOnAction(e -> onStatusButtonClick("todo", task_id, title));
                inProgressBtn.setToggleGroup(group);
                inProgressBtn.setOnAction(e -> onStatusButtonClick("in_progress", task_id, title));
                doneBtn.setToggleGroup(group);
                doneBtn.setOnAction(e -> onStatusButtonClick("done", task_id, title));

                switch (task_data[2]) {
                    case "todo" -> todoBtn.setSelected(true);
                    case "in_progress" -> inProgressBtn.setSelected(true);
                    case "done" -> doneBtn.setSelected(true);
                }

                // Layout: radio buttons in HBox
                HBox statusBox = new HBox(10, new Label("priority: " + task_priority), todoBtn, doneBtn, inProgressBtn, delete);

                // Wrap task label + actionBox vertically
                VBox taskBox = new VBox(t, statusBox);

                // Add to parent container (example: VBox called tasksContainer)
                tasks.getChildren().add(taskBox);
            }

        } else if (response.getStatus().equals("error")) {
            System.out.println(response.getMessage());
            task_reaction.setStyle("-fx-background-color: red; -fx-label-padding: 4;");
            task_reaction.setText(response.getMessage());
        }
    }

    public void reloadBoardTasks() {
        tasks.getChildren().clear();
        loadBoardTasks();
    }

    private void onTaskClick(String task_id) {
        Task.currentTask = new Task(task_id, null, null, null, null, 0);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(new File("./src/main/resources/com/example/board/task-view.fxml").toURI().toURL());
            Scene taskPage = new Scene(fxmlLoader.load());
            Main.primaryStage.setScene(taskPage);
            Main.currentController = fxmlLoader.getController();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadBoardUsers() {
        Request<BoardRequestDto> request = new Request<>("load_board_users", new BoardRequestDto(Board.currentBoard.getId()));
        Response<BoardUsersResponseDto> response = Main.client.sendRequest(request, BoardUsersResponseDto.class);
        if (response.getStatus().equals("ok")) {
            owner_username.setText(response.getData().getOwnerUsername());
            owner_id.setText(response.getData().getOwnerId());
            for (String[] user_data : response.getData().getAssociatedUsers()) {
                Label username = new Label("Username: " + user_data[0]);
                Label user_id = new Label("ID: " + user_data[1]);
                VBox user = new VBox(username, user_id);
                user.setStyle("-fx-vbox-padding:4;");
                users.getChildren().add(user);
            }

        } else if (response.getStatus().equals("error")) {
            user_reaction.setStyle("-fx-background-color: red; -fx-label-padding: 4;");
            user_reaction.setText(response.getMessage());
        }
        if (!User.currentUser.getId().equals(response.getData().getOwnerId())) {
            pane.getChildren().removeAll(add_user, new_user, user_reaction);
        }
    }

    public void reloadBoardUsers() {
        users.getChildren().clear();
        loadBoardUsers();
    }

    private void onAddUserClick() {
        if (new_user.getText().isEmpty()) {
            user_reaction.setStyle("-fx-background-color: red; -fx-label-padding: 4;");
            user_reaction.setText("Username Cannot Be Empty");
        } else {
            Request<AddUserToBoardRequestDto> request = new Request<>("add_user_to_board", new AddUserToBoardRequestDto(Board.currentBoard.getId(), new_user.getText(), Board.currentBoard.getName()));
            Response<Object> response = Main.client.sendRequest(request, Object.class);
            if (response.getStatus().equals("ok")) {
                user_reaction.setStyle("-fx-background-color: lightgreen; -fx-label-padding: 4;");
                user_reaction.setText(response.getMessage());
                reloadBoardUsers();
            } else if (response.getStatus().equals("error")) {
                user_reaction.setStyle("-fx-background-color: red; -fx-label-padding: 4;");
                user_reaction.setText(response.getMessage());
            }
        }
    }

    private void onAddTaskClick() {
        ArrayList<String> errors = new ArrayList<>();

        if (title.getText().isEmpty()) errors.add("Title");
        if (description.getText().isEmpty()) errors.add("Description");
        if (due_date.getValue() == null) errors.add("Due Date");
        if (priority.getSelectedToggle() == null) errors.add("Priority");

        if (!errors.isEmpty()) {
            task_reaction.setStyle("-fx-background-color: red; -fx-label-padding: 4;");
            task_reaction.setText(String.join(", ", errors) + " Cannot Be Empty");
            return;
        }

        LocalDate localDate = due_date.getValue();
        long millis = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();

        String selectedPriority = ((RadioButton) priority.getSelectedToggle()).getText();

        Request<AddTaskRequestDto> request = new Request<>(
                "add_task_to_board",
                new AddTaskRequestDto(
                        title.getText(),
                        description.getText(),
                        selectedPriority,
                        Board.currentBoard.getId(),
                        Board.currentBoard.getName(),
                        millis
                )
        );

        Response<Object> response = Main.client.sendRequest(request, Object.class);
        if (response.getStatus().equals("ok")) {
            task_reaction.setStyle("-fx-background-color: lightgreen; -fx-label-padding: 4;");
            reloadBoardTasks();
        } else if (response.getStatus().equals("error")) {
            task_reaction.setStyle("-fx-background-color: red; -fx-label-padding: 4;");
        }
        task_reaction.setText(response.getMessage());
        title.setText("");
        description.setText("");
        priority.selectToggle(null);
        due_date.setValue(null);
    }

    private void onDeleteTaskClick(String task_id, String task_title) {
        Request<TaskRequestDto> request = new Request<>("delete_task", new TaskRequestDto(task_id,task_title,Board.currentBoard.getId(), Board.currentBoard.getName()));
        Response<Object> response = Main.client.sendRequest(request, Object.class);
        if (response.getStatus().equals("ok")) {
            task_reaction.setStyle("-fx-background-color: lightgreen; -fx-label-padding: 4;");
            task_reaction.setText("Task " + task_title + " Deleted Successfully");
            reloadBoardTasks();
        } else if (response.getStatus().equals("error")) {
            task_reaction.setStyle("-fx-background-color: red; -fx-label-padding: 4;");
            task_reaction.setText(response.getMessage());
        }
    }

    private void onStatusButtonClick(String status, String task_id, String task_name) {
        Request<UpdateTaskRequestDto> request = new Request<>("update_task", new UpdateTaskRequestDto(Board.currentBoard.getName(),Board.currentBoard.getId(), task_id, "", task_name, "", status, "", 0));
        Response<Object> response = Main.client.sendRequest(request, Object.class);
        if (response.getStatus().equals("ok")) {
            task_reaction.setStyle("-fx-background-color: lightgreen; -fx-label-padding: 4;");
        } else if (response.getStatus().equals("error")) {
            task_reaction.setStyle("-fx-background-color: red; -fx-label-padding: 4;");
        }
        reloadBoardTasks();
        task_reaction.setText(response.getMessage());
    }
}
