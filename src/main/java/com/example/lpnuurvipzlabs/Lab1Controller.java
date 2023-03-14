package com.example.lpnuurvipzlabs;

import com.example.lpnuurvipzlabs.service.Lab1CostsService;
import com.example.lpnuurvipzlabs.service.Lab1QuantityService;
import com.example.lpnuurvipzlabs.service.Lab1Service;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;

import java.util.HashMap;
import java.util.Map;

public class Lab1Controller {
    @FXML private TextArea resultArea;
    @FXML private Label infoLabel;
    @FXML private RadioButton radioButton1;
    @FXML private RadioButton radioButton2;
    @FXML private ToggleGroup toggleGroup;
    private RadioButton selectedButton;

    private Lab1CostsService lab1CostsService;
    private Map<String, Lab1Service> serviceMap;

    public void initialize() {
        resultArea.setEditable(false);

        // Radiobutton group
        toggleGroup = new ToggleGroup();
        radioButton1.setToggleGroup(toggleGroup);
        radioButton2.setToggleGroup(toggleGroup);
        radioButton1.setSelected(true);

        serviceMap = new HashMap<>(){{
            put(radioButton1.getId(), new Lab1CostsService());
            put(radioButton2.getId(), new Lab1QuantityService());
        }};

        // Set info label as value from service
        selectedButton = (RadioButton) toggleGroup.getSelectedToggle();
        infoLabel.setText(String.format("Target state is %d", serviceMap.get(selectedButton.getId()).getTargetState() +1));
    }

    @FXML
    protected void onCalculateButtonClick() {
        selectedButton = (RadioButton) toggleGroup.getSelectedToggle();

        resultArea.setText(
                serviceMap.get(selectedButton.getId()).calculate()
        );
    }
}