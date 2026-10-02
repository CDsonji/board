package com.example.board.controllers;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import com.example.board.Main;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {

    @FXML
    private Button login;
    @FXML
    private Button signUp;

    private void onLoginClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(new File("./src/main/resources/com/example/board/login-view.fxml").toURI().toURL());
            Scene LoginPage = new Scene(fxmlLoader.load());
            Main.primaryStage.setScene(LoginPage);
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
        login.setOnAction(e -> onLoginClick());
        signUp.setOnAction(e -> onSignUpClick());
    }

}