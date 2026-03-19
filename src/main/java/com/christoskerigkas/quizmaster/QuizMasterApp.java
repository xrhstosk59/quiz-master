package com.christoskerigkas.quizmaster;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class QuizMasterApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Welcome.fxml"));
        Parent root = loader.load();

        stage.setTitle("quiz-master");
        stage.setMinWidth(750);
        stage.setMinHeight(400);
        stage.setScene(new Scene(root, 750, 400));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
