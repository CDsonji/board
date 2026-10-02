package com.example.board.controllers.auth;

import com.example.board.Main;
import com.example.board.controllers.board.ProfileController;
import com.example.board.model.User;
import com.example.board.model.dto.AuthRequestDto;
import com.example.board.model.dto.AuthResponseDto;
import com.example.board.model.message.Response;
import com.example.board.model.message.Request;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    TextField username;
    @FXML
    TextField password;
    @FXML
    Button back;
    @FXML
    Button login;
    @FXML
    Button signUp;
    @FXML
    public Label reaction;
    @FXML
    Pane pane;

    private void onLoginClick() {
        if (username.getText().isEmpty()) {
            reaction.setText("Username Cannot Be Empty");
            return;
        }
        if (password.getText().isEmpty()) {
            reaction.setText("Password Cannot Be Empty");
            return;
        }
        var request = new Request<AuthRequestDto>("login",new AuthRequestDto(username.getText(), password.getText()));
        Response<AuthResponseDto> response = Main.client.sendRequest(request, AuthResponseDto.class);
        if (response.getStatus().equals("ok")) {
            try {
                User.currentUser = new User(response.getData().getUsername(),response.getData().getId());
                FXMLLoader fxmlLoader = new FXMLLoader(new File("./src/main/resources/com/example/board/profile-view.fxml").toURI().toURL());
                Scene profilePage = new Scene(fxmlLoader.load());
                Main.primaryStage.setScene(profilePage);
                Main.currentController = fxmlLoader.getController();
                ProfileController controller = fxmlLoader.getController();
                controller.username.setText(response.getData().getUsername());
                controller.id.setText(response.getData().getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (response.getStatus().equals("error")) {
            username.setText("");
            password.setText("");
            reaction.setText(response.getMessage());
            reaction.setStyle("-fx-background-color: red; -fx-label-padding: 5;");
        }

    }

    private void onBackClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(new File("./src/main/resources/com/example/board/menu-view.fxml").toURI().toURL());
            Scene Menu = new Scene(fxmlLoader.load());
            Main.primaryStage.setScene(Menu);
            Main.currentController = fxmlLoader.getController();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void onSignUpClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(new File("./src/main/resources/com/example/board/signup-view.fxml").toURI().toURL());
            Scene SignUpPage = new Scene(fxmlLoader.load());
            Main.primaryStage.setScene(SignUpPage);
            Main.currentController = fxmlLoader.getController();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        back.setOnAction(e -> onBackClick());
        signUp.setOnAction(e -> onSignUpClick());
        login.setOnAction(e -> onLoginClick());

    }
}
