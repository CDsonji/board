package com.example.board;


import com.example.board.client.TcpClient;
import com.example.board.controllers.notitfication.NotificationHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class Main extends Application {
    public static Stage primaryStage;
    public static TcpClient client;

    public static Initializable currentController;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        client = new TcpClient("localhost", 9000, new NotificationHandler());
        FXMLLoader fxmlLoader = new FXMLLoader(new File("./src/main/resources/com/example/board/menu-view.fxml").toURI().toURL());
        Scene Menu = new Scene(fxmlLoader.load());
        currentController = fxmlLoader.getController();
        stage.setTitle("MyBoard");
        stage.setScene(Menu);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}