package com.example.board.controllers.board;

import com.example.board.Main;
import com.example.board.controllers.auth.LoginController;
import com.example.board.model.Board;
import com.example.board.model.User;
import com.example.board.model.dto.*;
import com.example.board.model.message.Request;
import com.example.board.model.message.Response;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {
    @FXML
    private Button logout;
    @FXML
    private Button add;
    @FXML
    public Label notification;
    @FXML
    private TextField board;
    @FXML
    private VBox boards;
    @FXML
    public Label username;
    @FXML
    public Label reaction;
    @FXML
    public Label id;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logout.setOnAction(e -> onLogoutClick());
        add.setOnAction(e -> onAddClick());
        loadBoards();
    }

    private void onLogoutClick() {
        var request = new Request<LogoutRequestDto>("logout", new LogoutRequestDto(User.currentUser.getId()));
        Response<AuthResponseDto> response = Main.client.sendRequest(request, AuthResponseDto.class);
        if (response.getStatus().equals("ok")) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(new File("./src/main/resources/com/example/board/login-view.fxml").toURI().toURL());
                Scene loginPage = new Scene(fxmlLoader.load());
                Main.primaryStage.setScene(loginPage);
                Main.currentController = fxmlLoader.getController();
                LoginController controller = fxmlLoader.getController();
                controller.reaction.setStyle("-fx-background-color: lightgreen; -fx-label-padding: 5;");
                controller.reaction.setText("Logged Out From " + User.currentUser.getUsername());
                User.currentUser = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (response.getStatus().equals("error")) {
            notification.setStyle("-fx-background-color: red; -fx-label-padding: 5;");
            notification.setText(response.getMessage());
        }
    }

    private void onAddClick() {
        if (board.getText().isEmpty()) {
            reaction.setText("Board Name Cannot Be Empty");
            reaction.setStyle("-fx-background-color: red; -fx-label-padding: 4;");
        } else {
            var request = new Request<NewBoardRequestDto>("create_board", new NewBoardRequestDto(board.getText(),User.currentUser.getUsername(), User.currentUser.getId()));
            Response<NewBoardResponseDto> response = Main.client.sendRequest(request, NewBoardResponseDto.class);
            if (response.getStatus().equals("ok")) {
                reaction.setStyle("-fx-background-color: lightgreen; -fx-label-padding: 4;");
                reaction.setText(response.getMessage());
                reloadBoards();
            } else if (response.getStatus().equals("error")) {
                reaction.setStyle("-fx-background-color: red; -fx-label-padding: 4;");
                reaction.setText(response.getMessage());
            }
        }
    }

    private void loadBoards() {
        Request<BoardsRequestDto> request = new Request<>("boards_list", new BoardsRequestDto(User.currentUser.getId()));
        Response<BoardsResponseDto> response = Main.client.sendRequest(request, BoardsResponseDto.class);
        if (response.getStatus().equals("ok")) {
            for (String[] data : response.getData().getBoards()) {
                Label board = new Label(data[0] + "   >");
                board.setStyle("-fx-padding: 3;");
                board.setOnMouseClicked(e -> onBoardClick(data[0], data[1]));
                boards.getChildren().add(board);
            }
        } else if (response.getStatus().equals("error")) {
            System.out.println(response.getMessage());
            reaction.setStyle("-fx-background-color: red; -fx-label-padding: 4;");
            reaction.setText("Failed To Load Your Boards");
        }
    }

    public void reloadBoards() {
        boards.getChildren().clear();
        loadBoards();
    }

    private void onBoardClick(String board_name, String board_id) {
        Board.currentBoard = new Board(board_id,board_name);
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(new File("./src/main/resources/com/example/board/board-view.fxml").toURI().toURL());
                Scene board = new Scene(fxmlLoader.load());
                BoardController controller = fxmlLoader.getController();
                controller.name.setText(board_name);
                Main.primaryStage.setScene(board);
                Main.currentController = controller;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
}


