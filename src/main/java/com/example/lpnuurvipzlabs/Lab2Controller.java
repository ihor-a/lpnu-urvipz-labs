package com.example.lpnuurvipzlabs;

import com.example.lpnuurvipzlabs.service.*;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;

public class Lab2Controller {
    @FXML private TextArea resultArea;
    @FXML private ScrollPane scrollPane1;
    @FXML private ScrollPane scrollPane2;
    @FXML private ScrollPane scrollPane3;
    @FXML Pane lineChartPane;

    private Lab2ServiceImpl lab2Service;

    public void initialize() {
        resultArea.setEditable(false);

        lab2Service = new Lab2ServiceImpl();
        lab2Service.scrollPane1 = scrollPane1;
        lab2Service.scrollPane2 = scrollPane2;
        lab2Service.scrollPane3 = scrollPane3;
        lab2Service.lineChartPane = lineChartPane;
    }

    @FXML
    protected void onCalculateButtonClick() {

        resultArea.setText(
                lab2Service.calculate()
        );
    }
}