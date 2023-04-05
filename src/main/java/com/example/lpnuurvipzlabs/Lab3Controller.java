package com.example.lpnuurvipzlabs;

import com.example.lpnuurvipzlabs.service.Lab3ServiceImpl;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

public class Lab3Controller implements Initializable {
    @FXML private ScrollPane scrollPane1;
    @FXML private ScrollPane scrollPane2;
    @FXML private TextArea resultArea;

    private Lab3ServiceImpl lab3Service;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resultArea.setEditable(false);

        lab3Service = new Lab3ServiceImpl(scrollPane1, scrollPane2);
    }

    @FXML
    protected void onCalculateButtonClick() {
        try {
            resultArea.setText(
                    lab3Service.calculate()
            );
        } catch (RuntimeException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Exception");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }
}