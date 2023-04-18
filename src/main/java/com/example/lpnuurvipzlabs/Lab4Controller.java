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
    @FXML private ScrollPane resourcePane1;
    @FXML private ScrollPane resourcePane2;
    @FXML private ScrollPane resourcePane3;
    @FXML private ScrollPane resourcePane4;
    @FXML private ScrollPane expertPane1;
    @FXML private ScrollPane expertPane2;
    @FXML private ScrollPane costsPane2;
    @FXML private ScrollPane resultPane1;
    @FXML private ScrollPane resultPane2;
    @FXML private ScrollPane planRiskPane;

    private Lab4ServiceImpl lab4Service;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lab4Service = new Lab4ServiceImpl(resourcePane1, resourcePane2, resourcePane3, resourcePane4,
                expertPane1, expertPane2, costsPane2, resultPane1, resultPane2, planRiskPane);
    }

    @FXML
    protected void onCalculateButtonClick() {
        try {
            lab4Service.calculate();

        } catch (RuntimeException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Exception");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    @FXML
    protected void onCalculateMeasureButtonClick() {
        try {
            lab4Service.refreshMeasure();

        } catch (RuntimeException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Exception");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }
}