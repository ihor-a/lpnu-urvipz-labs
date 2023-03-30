package com.example.lpnuurvipzlabs;

import com.example.lpnuurvipzlabs.service.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class Lab2Controller {
    @FXML private TextArea resultArea;

    private Lab2Service lab2Service;

    public void initialize() {
        resultArea.setEditable(false);

        lab2Service = new Lab2ServiceImpl();
    }

    @FXML
    protected void onCalculateButtonClick() {

        resultArea.setText(
                lab2Service.calculate()
        );
    }
}