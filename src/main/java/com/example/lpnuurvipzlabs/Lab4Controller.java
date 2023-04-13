package com.example.lpnuurvipzlabs;

import com.example.lpnuurvipzlabs.service.Lab4ServiceImpl;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

public class Lab4Controller implements Initializable {
    @FXML private ScrollPane scrollPane1;
    @FXML private ScrollPane scrollPane2;
    @FXML private ScrollPane scrollPane3;
    @FXML private ScrollPane scrollPane4;
    @FXML private ScrollPane scrollPane5;
    @FXML private ScrollPane scrollPane6;
    @FXML private ScrollPane resultPane1;
    @FXML private ScrollPane resultPane2;
    @FXML private TextArea resultArea;

    private Lab4ServiceImpl lab4Service;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resultArea.setEditable(false);
        resultArea.setStyle("-fx-font-family: 'monospaced';");

        lab4Service = new Lab4ServiceImpl(scrollPane1, scrollPane2, scrollPane3, scrollPane4, scrollPane5, resultPane1, resultPane2);
    }

    @FXML
    protected void onCalculateButtonClick() {
        try {
            resultArea.setText(
                    lab4Service.calculate()
            );
        } catch (RuntimeException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Exception");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }
}