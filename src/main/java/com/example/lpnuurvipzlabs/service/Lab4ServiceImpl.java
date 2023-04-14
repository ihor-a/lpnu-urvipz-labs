package com.example.lpnuurvipzlabs.service;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.text.DecimalFormat;
import java.util.*;

import static java.lang.Math.abs;
import static java.lang.Math.random;

public class Lab4ServiceImpl extends TextResultBase implements Lab4Service {

    private final ScrollPane scrollPane1, scrollPane2, scrollPane3, scrollPane4, scrollPane5;
    private final ScrollPane resultPane1, resultPane2;
    private final TableView<ExpertItem> expertTableView1 = new TableView<>();
    private final TableView<ResourceItem> techTableView = new TableView<>();
    private final TableView<ProbabilityItem> probabilityTableView = new TableView<>();
    private final ObservableList<ExpertItem> expertObservableList1 = FXCollections.observableArrayList();
    private final ObservableList<ResourceItem> techObservableList = FXCollections.observableArrayList();
    private final ObservableList<ProbabilityItem> probabilityObservableList = FXCollections.observableArrayList();

    private final ProbabilityExpert probabilityExpert;
    private final TechResource techResource;

    public Lab4ServiceImpl(ScrollPane scrollPane1, ScrollPane scrollPane2, ScrollPane scrollPane3, ScrollPane scrollPane4,
                           ScrollPane scrollPane5, ScrollPane resultPane1, ScrollPane resultPane2) {
        this.scrollPane1 = scrollPane1;
        this.scrollPane2 = scrollPane2;
        this.scrollPane3 = scrollPane3;
        this.scrollPane4 = scrollPane4;
        this.scrollPane5 = scrollPane5;
        this.resultPane1 = resultPane1;
        this.resultPane2 = resultPane2;

        probabilityExpert = new ProbabilityExpert(expertObservableList1, expertTableView1);
        techResource = new TechResource(techObservableList, techTableView);
        initTables();
    }

    private void initTables() {
        initExpertTable(expertTableView1, scrollPane5, expertObservableList1);
        initResourceTable("Множина настання технічних ризикових подій", techTableView, scrollPane1, techObservableList);
        initProbabilityTable(probabilityTableView, resultPane1, probabilityObservableList);
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
        for (var item: techResource.observableList) {
            appendResultText(item.toString());
        }

        calcProbability();

        return getResult();
    }

    private void calcProbability() {
        probabilityObservableList.clear();

        for (var resourceItem: techResource.observableList) {
            if (!resourceItem.isEnabled()) {
                continue;
            }
            ProbabilityItem probabilityItem = new ProbabilityItem(resourceItem.getName(), probabilityExpert);
            probabilityItem.calcRandoms();
            probabilityObservableList.add(probabilityItem);
        }
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
        ObservableList<ResourceItem> observableList;
        protected TableView<ResourceItem> tableView;

        public BaseResource(ObservableList<ResourceItem> observableList, TableView<ResourceItem> tableView) {
            this.observableList = observableList;
            this.tableView = tableView;
        }
    }

    private class ProbabilityExpert extends BaseExpert {
        public ProbabilityExpert(ObservableList<ExpertItem> observableList, TableView<ExpertItem> tableView) {
            super(observableList, tableView);
            randomSeed = 0.1;
            randomMultiplier = 0.7;

            observableList.add(new ExpertItem("Технічних ризикових подій", 11, new Integer[]{10, 10, 9, 8, 10, 10, 8, 8, 10, 10}));
            observableList.add(new ExpertItem("Вартісних ризикових подій", 7, new Integer[]{8, 7, 9, 10, 8, 8, 10, 7, 8, 10}));
            observableList.add(new ExpertItem("Планових ризикових подій", 9, new Integer[]{10, 7, 8, 10, 9, 10, 9, 7, 10, 10}));
            observableList.add(new ExpertItem("Ризикових події реалізації процесу управління", 14, new Integer[]{10, 9, 7, 9, 9, 9, 7, 8, 9, 7}));
        }
    }

    protected abstract class BaseExpert {
        protected ObservableList<ExpertItem> observableList;
        protected TableView<ExpertItem> tableView;
        protected double randomSeed, randomMultiplier;

        public BaseExpert(ObservableList<ExpertItem> observableList, TableView<ExpertItem> tableView) {
            this.observableList = observableList;
            this.tableView = tableView;
        }

        double makeRandom() {
            return randomSeed + random() * randomMultiplier;
        }
    }

    public class ProbabilityItem extends BaseResultItem {
        public ProbabilityItem(String name, BaseExpert expert) {
            super(name, expert);
        }

        double result;

        public String getResult() {
            return getNumeric(result);
        }
    }

    protected abstract class BaseResultItem {
        final private int OFFSET = 1;
        private final int AM_ELEMENTS = 10;
        protected BaseExpert expert;
        private final String name;
        private double[] randoms = new double[AM_ELEMENTS];
        private double[] estimates = new double[AM_ELEMENTS];
        double sum;
        int level;

        public BaseResultItem(String name, BaseExpert expert) {
            this.name = name;
            this.expert = expert;
        }

        void calcRandoms() {
            for (int i = 0; i<randoms.length; i++) {
                randoms[i] = expert.makeRandom();
            }
            calcAvgSum();
        }

        void calcAvgSum() {
            this.sum = Arrays.stream(randoms).sum() / AM_ELEMENTS;
        }

        public void setRandom(int number, double value) {
            randoms[number-OFFSET] = value;
        }
        public void setEstimate(int number, double value) {
            estimates[number-OFFSET] = value;
        }

        public String getName() {
            return name;
        }

        public String getSum() {
            return getNumeric(sum);
        }
        protected String getNumeric(Double val) {
            if (abs(val - (double)val.intValue()) != 0) {
                return  new DecimalFormat("#.###").format(val);
            } else {
                return Integer.toString(val.intValue());
            }
        }

        public String getLevel() {
            return Integer.toString(level);
        }

        private String getRandom(int number) {
            return getNumeric(randoms[number - OFFSET]);
        }
        private String getEstimate(int number) {
            return getNumeric(estimates[number - OFFSET]);
        }
        public String getE1() {
            return getEstimate(1);
        }
        public String getE2() {
            return getEstimate(2);
        }
        public String getE3() {
            return getEstimate(3);
        }
        public String getE4() {
            return getEstimate(4);
        }
        public String getE5() {
            return getEstimate(5);
        }
        public String getE6() {
            return getEstimate(6);
        }
        public String getE7() {
            return getEstimate(7);
        }
        public String getE8() {
            return getEstimate(8);
        }
        public String getE9() {
            return getEstimate(9);
        }
        public String getE10() {
            return getEstimate(10);
        }

        public String getR1() {
            return getRandom(1);
        }
        public String getR2() {
            return getRandom(2);
        }
        public String getR3() {
            return getRandom(3);
        }
        public String getR4() {
            return getRandom(4);
        }
        public String getR5() {
            return getRandom(5);
        }
        public String getR6() {
            return getRandom(6);
        }
        public String getR7() {
            return getRandom(7);
        }
        public String getR8() {
            return getRandom(8);
        }
        public String getR9() {
            return getRandom(9);
        }
        public String getR10() {
            return getRandom(10);
        }
    }

    public static class ResourceItem {
        private final String name;
        BooleanProperty enabled;
        final Integer number;

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

    protected void initProbabilityTable(TableView<ProbabilityItem> tableView, ScrollPane scrollPane,
                                        ObservableList<ProbabilityItem> observableList) {
        List<String[]> customConfig = new ArrayList<>(){{
            add(new String[]{"result", "P"});
        }};
        initBaseResultTable(tableView, scrollPane, observableList, customConfig);
    }
    protected <T> void initBaseResultTable(TableView<T> tableView, ScrollPane scrollPane,
                                           ObservableList<T> observableList,
                                           List<String[]> customConfig) {
        // Table already initialized
        if (tableView.getColumns().size() > 0) {
            tableView.getColumns().get(0).setPrefWidth(scrollPane.getWidth() * 0.25);
            tableView.setPrefWidth(scrollPane.getWidth());
            return;
        }

        TableColumn<T, String> tableColumn = new TableColumn<>("Назва");
        tableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableView.getColumns().add(tableColumn);

        List<String[]> columnConfig = new ArrayList<>();
        for (int i = 1; i<=10; i++) {
            columnConfig.add(new String[]{"r"+i, String.valueOf(i)});
        }
        columnConfig.add(new String[]{"sum", "Σ"});
        for (int i = 1; i<=10; i++) {
            columnConfig.add(new String[]{"e"+i, String.valueOf(i)});
        }

        columnConfig.addAll(customConfig);
        columnConfig.add(new String[]{"level", "Level"});

        for (String[] column : columnConfig) {
            tableColumn = new TableColumn<>(column[1]);
            tableColumn.setPrefWidth(45d);
            tableColumn.setCellValueFactory(new PropertyValueFactory<>(column[0]));
            tableView.getColumns().add(tableColumn);
        }

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
//        tableColumn.setCellFactory(new Callback<>() {
//            @Override
//            public TableCell<ExpertItem, String> call(TableColumn<ExpertItem, String> param) {
//                return new TableCell<>() {
//                    @Override
//                    public void updateIndex(int i) {
//                        super.updateIndex(i);
//                        if (i == 1) {
//                            this.setStyle("-fx-background-color: #00eeee;");
//                        }
//                        var observableList = param.getTableView().getItems();
//                        if (i >= 0 && i < observableList.size()) {
//                            System.out.printf("Cell(%d): %s%n", i, param.getCellData(i));
//                            System.out.println(observableList.get(i));
//                        }
//                    }
//
//                    @Override
//                    protected void updateItem(String item, boolean empty) {
//                        super.updateItem(item, empty);
//                        if (item != null) {
//                            setText(item);
//                        }
//                    }
//                };
//            }
//        });
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
