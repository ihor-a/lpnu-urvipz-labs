package com.example.lpnuurvipzlabs;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Lab1Application extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Lab1Application.class.getResource("lab1-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1024, 800);

        stage.setTitle("MPZIP-11 :: UR v IPZ Lab 1 :: Variant 1");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}