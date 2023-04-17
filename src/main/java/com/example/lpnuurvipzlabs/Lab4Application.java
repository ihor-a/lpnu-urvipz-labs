package com.example.lpnuurvipzlabs;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Lab4Application extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Lab4Application.class.getResource("lab4-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1580, 975);

        stage.setTitle("MPZIP-11 :: UR v IPZ Lab 4 :: Variant 1");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}