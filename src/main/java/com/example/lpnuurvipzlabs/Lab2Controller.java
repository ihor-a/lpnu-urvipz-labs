package com.example.lpnuurvipzlabs;

import com.example.lpnuurvipzlabs.service.*;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Lab2Controller {
    @FXML private TextArea resultArea;
    @FXML private ScrollPane scrollPane1;
    @FXML private ScrollPane scrollPane2;
    @FXML Pane lineChartPane;
    @FXML private TextField n;
    @FXML private TextField aMin;
    @FXML private TextField aMax;
    @FXML private TextField rMin;
    @FXML private TextField rMax;
    @FXML private TextField bMin;
    @FXML private TextField bMax;
    @FXML private TextField ciMin;
    @FXML private TextField ciMax;
    @FXML private TextField lMin;
    @FXML private TextField lMax;
    @FXML private TextField ci1Min;
    @FXML private TextField ci2Min;
    @FXML private TextField ci3Min;
    @FXML private TextField ci4Min;
    @FXML private TextField ci5Min;
    @FXML private TextField ci1Max;
    @FXML private TextField ci2Max;
    @FXML private TextField ci3Max;
    @FXML private TextField ci4Max;
    @FXML private TextField ci5Max;
    @FXML private TextField lambdaMin;
    @FXML private TextField lambdaMax;
    @FXML private TextField qMin;
    @FXML private TextField qMax;

    private Map<TextField, String> inputFieldsMap;

    private Lab2ServiceImpl lab2Service;

    public void initialize() {
        resultArea.setEditable(false);
        inputFieldsMap = new HashMap<>() {{
            put(n, "11");
            put(aMin, "0.91");
            put(aMax, "1.18");
            put(rMin, "0.14");
            put(rMax, "0.18");
            put(bMin, "38.7");
            put(bMax, "50.3");
            put(ciMin, "2.8");
            put(ciMax, "3.6");
            put(lMin, "3");
            put(lMax, "5");
            put(ci1Min, "5.5");
            put(ci1Max, "7.2");
            put(ci2Min, "4.8");
            put(ci2Max, "6.2");
            put(ci3Min, "3.8");
            put(ci3Max, "4.9");
            put(ci4Min, "3.6");
            put(ci4Max, "4.7");
            put(ci5Min, "3.3");
            put(ci5Max, "4.3");
            put(lambdaMin, "0.86");
            put(lambdaMax, "1.12");
            put(qMin, "0.96");
            put(qMax, "1.25");
        }};

        inputFieldsMap.forEach((textField, value) -> {
            textField.setText(value);
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("[\\d*.]")) {
                    textField.setText(newValue.replaceAll("[^\\d.]", ""));
                }
            });
        });

        lab2Service = new Lab2ServiceImpl();
        lab2Service.scrollPane1 = scrollPane1;
        lab2Service.scrollPane2 = scrollPane2;
        lab2Service.lineChartPane = lineChartPane;
    }

    private double getNumericFieldValue(TextField textField) {
        double result;
        if (textField.getText().equals("")) {
            result = 0.0;
            textField.setText(String.valueOf(result));
        } else {
            result = Double.parseDouble(textField.getText());
        }
        return result;
    }

    @FXML
    protected void onCalculateButtonClick() {
        Map<String, Double> inputValuesMap = inputFieldsMap.keySet()
                .stream()
                .collect(Collectors.toMap(TextField::getId, this::getNumericFieldValue));

        try {
            resultArea.setText(
                    lab2Service.calculate(inputValuesMap)
            );
        } catch (RuntimeException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Exception");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }
}