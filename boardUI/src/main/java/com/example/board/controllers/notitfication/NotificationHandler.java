package com.example.board.controllers.notitfication;

import com.example.board.Main;
import com.example.board.controllers.board.BoardController;
import com.example.board.controllers.board.ProfileController;
import com.example.board.controllers.task.TaskController;
import com.example.board.model.dto.NotificationDto;
import javafx.application.Platform;

public class NotificationHandler {


    public void onNotification(NotificationDto data) {
        if (Main.currentController instanceof ProfileController controller) {
            Platform.runLater(() -> {
                controller.notification.setStyle("-fx-background-color: lightgreen; -fx-label-padding: 4;");
                controller.notification.setText(data.getNotification());
                controller.reloadBoards();
            });
        } else if (Main.currentController instanceof BoardController controller) {
            Platform.runLater(() -> {
                controller.notification.setStyle("-fx-background-color: lightgreen; -fx-label-padding: 4;");
                controller.notification.setText(data.getNotification());
                controller.reloadBoardTasks();
                controller.reloadBoardUsers();
            });
        } else if (Main.currentController instanceof TaskController controller) {
            Platform.runLater(() -> {
                controller.notification.setStyle("-fx-background-color: lightgreen; -fx-label-padding: 4;");
                controller.notification.setText(data.getNotification());
                controller.loadTask();
            });
        }
    }
}
