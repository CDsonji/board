package com.example.board.controllers.auth;

import com.example.board.Main;
import com.example.board.model.dto.AuthRequestDto;
import com.example.board.model.dto.AuthResponseDto;
import com.example.board.model.message.Response;
import com.example.board.model.message.Request;
import com.example.board.utils.JSON;
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

public class SignupController implements Initializable {
    @FXML
    Button back;
    @FXML
    Button signUp;
    @FXML
    Button login;
    @FXML
    TextField username;
    @FXML
    TextField password;
    @FXML
    Label reaction;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        back.setOnAction(e -> onBackClick());
        signUp.setOnAction(e -> onSignUpClick());
        login.setOnAction(e -> onLoginClick());
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
        if (username.getText().isEmpty()) {
            reaction.setText("Username Cannot Be Empty");
            return;
        }
        if (password.getText().isEmpty()) {
            reaction.setText("Password Cannot Be Empty");
            return;
        }
        var request = new Request<AuthRequestDto>("register",new AuthRequestDto(username.getText(), password.getText()));
        Response<AuthResponseDto> response = Main.client.sendRequest(request, AuthResponseDto.class);
        System.out.println(JSON.Stringify(response));
        if (response.getStatus().equals("ok")) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(new File("./src/main/resources/com/example/board/login-view.fxml").toURI().toURL());
                Scene loginPage = new Scene(fxmlLoader.load());
                Main.primaryStage.setScene(loginPage);
                Main.currentController = fxmlLoader.getController();
                LoginController controller = fxmlLoader.getController();
                controller.reaction.setText(response.getMessage());
                controller.reaction.setStyle("-fx-background-color: lightgreen; -fx-label-padding: 5;");
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

    private void onLoginClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(new File("./src/main/resources/com/example/board/login-view.fxml").toURI().toURL());
            Scene loginPage = new Scene(fxmlLoader.load());
            Main.primaryStage.setScene(loginPage);
            Main.currentController = fxmlLoader.getController();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
