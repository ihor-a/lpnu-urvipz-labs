package com.example.lpnuurvipzlabs.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.text.DecimalFormat;
import java.util.*;

import static java.lang.Math.abs;

public class Lab3ServiceImpl extends TextResultBase implements Lab3Service {

    private ScrollPane scrollPane1, scrollPane2;
    private final TableView<ResourceItem> tsTableView = new TableView<>();
    private final ObservableList<ResourceItem> tsObservableArrayList = FXCollections.observableArrayList();
    private final String EDITABLE_COLUMN = "max";
    private int tempCounter = 0;

    public Lab3ServiceImpl(ScrollPane scrollPane1, ScrollPane scrollPane2) {
        this.scrollPane1 = scrollPane1;
        this.scrollPane2 = scrollPane2;

        TsResource.loadData(tsObservableArrayList);
        initTables();
    }

    private void initTables() {
        initTable(tsTableView, scrollPane1, tsObservableArrayList);
    }

    @Override
    public String calculate() {
        initTables();
        resetResult();
        appendResultText("Lab3");

//        ObservableList<ResourceItem> items = tStableView.getItems();
//        ObservableList<TableItem> items = tStableView.getSelectionModel().getSelectedItems();
        for (var item: tsObservableArrayList) {
            appendResultText(item.getSign());
            appendResultText(item.getName());
            appendResultText(item.getMax());
        }

        calcTsTable();

        return getResult();
    }


    private void initTable(TableView<ResourceItem> tableView, ScrollPane scrollPane, ObservableList<ResourceItem> observableList) {
        // Table already initialized
        if (tableView.getColumns().size() > 0) {
            // avoid FX bug with zero getWidth initialized from constructor
            tableView.getColumns().get(0).setPrefWidth(scrollPane.getWidth() * 0.5);
            tableView.setPrefWidth(scrollPane.getWidth());
            return;
        }
        tableView.setEditable(true);
        List<String[]> columnConfig = new ArrayList<>(){{
            add(new String[]{"name", "Name"});
            add(new String[]{"sign", "Sign"});
            add(new String[]{"unit", "Unit"});
            add(new String[]{"min", "Min"});
            add(new String[]{"nom", "Nom"});
            add(new String[]{"max", "Max"});
        }};

        TableColumn<ResourceItem, String> tableColumn;

        for (String[] column : columnConfig) {
            tableColumn = new TableColumn<>(column[1]);
            tableColumn.setCellValueFactory(new PropertyValueFactory<>(column[0]));

            if (column[0].equals(EDITABLE_COLUMN)) {
                tableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
                tableColumn.setOnEditCommit(event -> {
                    var item = tableView.getSelectionModel().getSelectedItem();
                    item.setEditableValue(event.getNewValue(), event.getOldValue());
                    tableView.refresh();
                });
            }
            tableView.getColumns().add(tableColumn);
        }

        scrollPane.setContent(tableView);
        tableView.setItems(observableList);
    }

    public void calcTsTable() {
        tsObservableArrayList.add(new ResourceItem("Тактова частота процесора "+tempCounter++, "fTp","ГГц",2.0,2.0,2.0));
        tsObservableArrayList.add(new ResourceItem("Тактова частота процесора "+tempCounter++, "fTp","ГГц",2.0,2.0,2.0));
        tsObservableArrayList.remove(0);
    }

    static class TsResource {
        static void loadData(ObservableList<ResourceItem> resourceItems) {
            resourceItems.add(new ResourceItem("Тактова частота процесора","fTp","ГГц",2.0));
        }
    }

    public static class ResourceItem {
        String name, sign, unit;
        Double min, nom, max;

        public ResourceItem(String name, String sign, String unit, Double min, Double nom, Double max) {
            this.name = name;
            this.sign = sign;
            this.unit = unit;
            this.min = min;
            this.nom = nom;
            this.max = max;
        }

        public ResourceItem(String name, String sign, String unit, Double max) {
            this.name = name;
            this.sign = sign;
            this.unit = unit;
            this.max = max;
            this.min = 0.0;
            this.nom = 0.0;
        }

        public void setEditableValue(String newValue, String oldValue) {
            if (oldValue.equals(newValue)) {
                return;
            }
            this.max = Double.parseDouble(newValue.matches("^[\\d\\.]+$") ? newValue : oldValue);
            this.min = 0.0;
            this.nom = 0.0;
        }

        public String getName() {
            return name;
        }

        public String getSign() {
            return sign;
        }

        public String getUnit() {
            return unit;
        }

        public String getMin() {
            return getNumeric(min);
        }

        public String getNom() {
            return getNumeric(nom);
        }

        public String getMax() {
            return getNumeric(max);
        }

        private String getNumeric(Double val) {
            if (abs(val - (double)val.intValue()) != 0) {
                return  new DecimalFormat(".###").format(val);
            } else {
                return String.format("%d", val.intValue());
            }
        }
    }

}
