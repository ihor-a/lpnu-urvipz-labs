package com.example.lpnuurvipzlabs;

import com.example.lpnuurvipzlabs.service.Lab1Service;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class Lab1Controller {
    @FXML private TextArea resultArea;
    private Lab1Service lab1Service;

    public void initialize() {
        resultArea.setEditable(false);

        lab1Service = new Lab1Service();
    }

    @FXML
    protected void onCalculateButtonClick() {
        resultArea.setText(
                lab1Service.calculate()
        );
    }
}