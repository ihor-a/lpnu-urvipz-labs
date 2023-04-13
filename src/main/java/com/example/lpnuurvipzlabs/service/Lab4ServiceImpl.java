package com.example.lpnuurvipzlabs.service;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.util.*;

public class Lab4ServiceImpl extends TextResultBase implements Lab4Service {

    private final ScrollPane scrollPane1, scrollPane2, scrollPane3, scrollPane4, scrollPane5;
    private final ScrollPane resultPane1, resultPane2;
    private final TableView<ExpertItem> expertTableView1 = new TableView<>();
    private final TableView<ResourceItem> techTableView = new TableView<>();
    private final ObservableList<ExpertItem> expertObservableList1 = FXCollections.observableArrayList();
    private final ObservableList<ResourceItem> techObservableList = FXCollections.observableArrayList();

    private final ExpertResource expertResource1;

    public Lab4ServiceImpl(ScrollPane scrollPane1, ScrollPane scrollPane2, ScrollPane scrollPane3, ScrollPane scrollPane4,
                           ScrollPane scrollPane5, ScrollPane resultPane1, ScrollPane resultPane2) {
        this.scrollPane1 = scrollPane1;
        this.scrollPane2 = scrollPane2;
        this.scrollPane3 = scrollPane3;
        this.scrollPane4 = scrollPane4;
        this.scrollPane5 = scrollPane5;
        this.resultPane1 = resultPane1;
        this.resultPane2 = resultPane2;

        expertResource1 = new ExpertResource(expertObservableList1, expertTableView1);
        new TechResource(techObservableList, techTableView);
        initTables();
    }

    private void initTables() {
        initExpertTable(expertTableView1, scrollPane5, expertObservableList1);
        initResourceTable("Множина настання технічних ризикових подій", techTableView, scrollPane1, techObservableList);
    }

    @Override
    public String calculate() {
        initTables();
        resetResult();
        appendResultText("Lab4 result");
        appendResultNewline();

        for (var item: expertObservableList1) {
            appendResultText(item.toString());
        }
        for (var item: techObservableList) {
            appendResultText(item.toString());
        }

        return getResult();
    }

    class TechResource extends BaseResource {
        public TechResource(ObservableList<ResourceItem> observableListExt, TableView<ResourceItem> tableView) {
            super(observableListExt, tableView);

            Integer number = 1;
            observableList.add(new ResourceItem("затримки у постачанні обладнання, необхідного для підтримки процесу розроблення ПЗ", number++));
            observableList.add(new ResourceItem("затримки у постачанні інструмент. засобів, необхідних для процесу розроблення ПЗ", number++));
            observableList.add(new ResourceItem("небажання команди виконавців використовувати інструмент. засоби для розроблення ПЗ", number++));
            observableList.add(new ResourceItem("формування запитів на більш потужні інструментальні засоби розроблення ПЗ", number++));
            observableList.add(new ResourceItem("відмова команди виконавців від CASE-засобів розроблення ПЗ", number++));
            observableList.add(new ResourceItem("неефективність програмного коду, згенерованого CASE-засобами розроблення ПЗ", number++));
            observableList.add(new ResourceItem("неможливість інтеграції CASE-засобів з іншими інструмент. засобами для підтримки розроблення ПЗ", number++));
            observableList.add(new ResourceItem("недостатня продуктивність баз(и) даних для підтримки процесу розроблення ПЗ", number++));
            observableList.add(new ResourceItem("прогр. компоненти, які використовують повторно в ПЗ, мають дефекти та обмежені функ. можливості", number++));
            observableList.add(new ResourceItem("швидкість виявлення дефектів у програмному коді є нижчою від раніше запланованих термінів", number++));
            observableList.add(new ResourceItem("поява дефектних системних компонент, які використовують для розроблення ПЗ", number++));
        }
    }

    abstract class BaseResource {
        protected ObservableList<ResourceItem> observableList;
        protected TableView<ResourceItem> tableView;

        public BaseResource(ObservableList<ResourceItem> observableList, TableView<ResourceItem> tableView) {
            this.observableList = observableList;
            this.tableView = tableView;
        }
    }

    class ExpertResource {
        protected ObservableList<ExpertItem> observableList;
        protected TableView<ExpertItem> tableView;

        public ExpertResource(ObservableList<ExpertItem> observableList, TableView<ExpertItem> tableView) {
            this.observableList = observableList;
            this.tableView = tableView;

            observableList.add(new ExpertItem("Технічних ризикових подій", 11, new Integer[]{10, 10, 9, 8, 10, 10, 8, 8, 10, 10}));
            observableList.add(new ExpertItem("Вартісних ризикових подій", 7, new Integer[]{8, 7, 9, 10, 8, 8, 10, 7, 8, 10}));
            observableList.add(new ExpertItem("Планових ризикових подій", 9, new Integer[]{10, 7, 8, 10, 9, 10, 9, 7, 10, 10}));
            observableList.add(new ExpertItem("Ризикових події реалізації процесу управління", 14, new Integer[]{10, 9, 7, 9, 9, 9, 7, 8, 9, 7}));
        }
    }

    public static class ResourceItem {
        private final String name;
        BooleanProperty enabled;
        private final Integer number;

        public ResourceItem(String name, Integer number) {
            this.name = name;
            this.number = number;
            this.enabled = new SimpleBooleanProperty(true);
        }

        public String getName() {
            return name;
        }

        public String getNumber() {
            return number.toString();
        }


        public boolean isEnabled() {
            return enabled.get();
        }

        public BooleanProperty enabledProperty() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled.set(enabled);
        }

        @Override
        public String toString() {
            return "ResourceItem{name='" + name + '\'' + ", enabled=" + isEnabled() + ", number=" + number + '}';
        }
    }

    public static class ExpertItem {
        private final String name;
        Integer quantity;
        private final Integer[] values;
        final private int OFFSET = 1;

        public ExpertItem(String name, Integer quantity, Integer[] values) {
            this.name = name;
            this.quantity = quantity;
            this.values = values;
        }

        public void setValue(int number, String oldValue, String newValue) {
            if (oldValue.equals(newValue)) {
                return;
            }
            if (newValue.matches("^[\\d]+$")) {
                var value  = Integer.parseInt(newValue);
                if (value >= 0 && value <= 10) {
                    this.values[number-OFFSET] = value;
                }
            }
        }

        private String getValue(int number) {
            return values[number - OFFSET].toString();
        }

        public String getName() {
            return name;
        }

        public String getN1() {
            return getValue(1);
        }
        public String getN2() {
            return getValue(2);
        }
        public String getN3() {
            return getValue(3);
        }
        public String getN4() {
            return getValue(4);
        }
        public String getN5() {
            return getValue(5);
        }
        public String getN6() {
            return getValue(6);
        }
        public String getN7() {
            return getValue(7);
        }
        public String getN8() {
            return getValue(8);
        }
        public String getN9() {
            return getValue(9);
        }
        public String getN10() {
            return getValue(10);
        }

        @Override
        public String toString() {
            return "ExpertItem{name='" + name + '\'' + ", quantity=" + quantity +
                    ", " + Arrays.toString(values) + '}';
        }
    }

    private void initResourceTable(String title, TableView<ResourceItem> tableView, ScrollPane scrollPane, ObservableList<ResourceItem> observableList) {
        // Table already initialized
        if (tableView.getColumns().size() > 0) {
            tableView.setPrefWidth(scrollPane.getWidth());
            tableView.getColumns().get(0).setMaxWidth(scrollPane.getWidth() * 0.90);
            return;
        }
        
        tableView.setEditable(true);
        TableColumn<ResourceItem, String> tableColumn = new TableColumn<>(title);
        tableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableView.getColumns().add(tableColumn);

        TableColumn<ResourceItem, Boolean> tableColumnBoolean = new TableColumn<>("Y/N");
        tableColumnBoolean.setCellValueFactory(param -> param.getValue().enabledProperty());
        tableColumnBoolean.setCellFactory(CheckBoxTableCell.forTableColumn(tableColumnBoolean));
        tableView.getColumns().add(tableColumnBoolean);

        tableView.setItems(observableList);
        scrollPane.setContent(tableView);
    }

    private void initExpertTable(TableView<ExpertItem> tableView, ScrollPane scrollPane, ObservableList<ExpertItem> observableList) {
        // Table already initialized
        if (tableView.getColumns().size() > 0) {
            return;
        }

        tableView.setEditable(true);
        TableColumn<ExpertItem, String> tableColumn = new TableColumn<>("Множина\\ Коеф. вагомості експертів");
        tableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableView.getColumns().add(tableColumn);

        List<String[]> columnConfig = new ArrayList<>(){{
            add(new String[]{"n1", "1"});
            add(new String[]{"n2", "2"});
            add(new String[]{"n3", "3"});
            add(new String[]{"n4", "4"});
            add(new String[]{"n5", "5"});
            add(new String[]{"n6", "6"});
            add(new String[]{"n7", "7"});
            add(new String[]{"n8", "8"});
            add(new String[]{"n9", "9"});
            add(new String[]{"n10", "10"});
        }};

        for (String[] column : columnConfig) {
            tableColumn = new TableColumn<>(column[1]);
            tableColumn.setCellValueFactory(new PropertyValueFactory<>(column[0]));
            tableColumn.setMaxWidth(35d);

            tableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            tableColumn.setOnEditCommit(event -> {
                var item = event.getRowValue();
                item.setValue(event.getTablePosition().getColumn(), event.getOldValue(), event.getNewValue());
                event.getTableView().refresh();
            });
            tableView.getColumns().add(tableColumn);
        }

        tableView.setItems(observableList);
        scrollPane.setContent(tableView);
    }
}
