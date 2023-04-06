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
import static java.lang.Math.random;

public class Lab3ServiceImpl extends TextResultBase implements Lab3Service {

    private ScrollPane scrollPane1, scrollPane2;
    private final TableView<ResourceItem> tsTableView = new TableView<>();
    private final TableView<ResourceItem> ssTableView = new TableView<>();
    private final TableView<ResourceItem> msTableView = new TableView<>();
    private final ObservableList<ResourceItem> tsObservableArrayList = FXCollections.observableArrayList();
    private final ObservableList<ResourceItem> ssObservableArrayList = FXCollections.observableArrayList();
    private final ObservableList<ResourceItem> msObservableArrayList = FXCollections.observableArrayList();
    private TsResource tsResource;
    private SsResource ssResource;

    public Lab3ServiceImpl(ScrollPane scrollPane1, ScrollPane scrollPane2) {
        this.scrollPane1 = scrollPane1;
        this.scrollPane2 = scrollPane2;

        tsResource = new TsResource(tsObservableArrayList, tsTableView);
        ssResource = new SsResource(ssObservableArrayList, ssTableView);
        initTables();
    }

    private void initTables() {
        initTable(tsTableView, scrollPane1, tsObservableArrayList);
        initTable(ssTableView, scrollPane2, ssObservableArrayList);
    }

    @Override
    public String calculate() {
        initTables();
        resetResult();

        tsResource.calculate();
        ssResource.calculate();

        tsResource.printLog();

        for (var item: tsObservableArrayList) {
            appendResultText(item.toString());
        }
        return getResult();
    }


    class TsResource extends BaseResource {
        double indPPC, indPNET, indPP;

        TsResource(ObservableList<ResourceItem> observableListExt, TableView<ResourceItem> tableView) {
            this.observableList = observableListExt;
            this.tableView = tableView;

            int number = 1;
            observableList.add(new ResourceItem(number++,"Тактова частота процесора","fTp","ГГц",2.0));
            observableList.add(new ResourceItem(number++,"Кількість ядер процесора","NCp","шт.",2.0));
            observableList.add(new ResourceItem(number++,"Розрядність процесора","Cp","біт",32.0));
            observableList.add(new ResourceItem(number++,"Тактова частота ОЗП","fTRAM","ГГц",0.4));
            observableList.add(new ResourceItem(number++,"Об'єм ОЗП","VRAM","Гбайт",3.25));
            observableList.add(new ResourceItem(number++,"Швидкість доступу до жорсткого диска","VHDD","мс",12.7));
            observableList.add(new ResourceItem(number++,"Об'єм жорсткого диска","SHDD","Гбайт",500.0));
            observableList.add(new ResourceItem(number++,"Кількість портів","NPT","шт.",24.0));
            observableList.add(new ResourceItem(number++,"Кількість протоколів","NPR","шт.",4.0));
            observableList.add(new ResourceItem(number++,"Швидкість передачі","VN","Мбіт/с",1000.0));
            observableList.add(new ResourceItem(number++,"Розрядність даних, що передаються","CNET","біт",32.0));
            observableList.add(new ResourceItem(number++,"Роздільна здатність","RP","піксел",1200.0));
            observableList.add(new ResourceItem(number++,"Швидкість друку (сканування)","VPR","стор./хв.",12.0));
            observableList.add(new ResourceItem(number++,"Швидкість обміну з ПК","RE","Мбіт/с",25.0));
            observableList.add(new ResourceItem(number++,"Об'єм ОЗП","VPRAM","Гбайт",0.128));
        }

        void calcMinNom() {
            for(var item: observableList) {
                switch (item.sign) {
                    case "fTp":
                    case "NCp":
                    case "Cp":
                        item.min = item.max;
                        item.nom = item.max;
                        break;
                    case "fTRAM":
                        item.min = item.max * 0.75;
                        item.nom = 0.33; //std
                        break;
                    case "VRAM":
                        item.min = 2.0;
                        item.nom = 2.56; //std
                        break;
                    case "VHDD":
                        item.min = 8.0;
                        item.nom = 8.4; //std
                        break;
                    case "SHDD":
                        item.min = 200.0;
                        item.nom = 224.0; //std32
                        break;
                    case "NPT":
                        item.min = item.max * 0.75;
                        item.nom = 19.0; //std
                        break;
                    case "NPR":
                        item.min = 1.0;
                        item.nom = 2.0; //std
                        break;
                    case "VN":
                        item.min = 500.0;
                        item.nom = 700.0; //std50
                        break;
                    case "CNET":
                        item.min = 8.0;
                        item.nom = 16.0; //grade
                        break;
                    case "RP":
                        item.min = 300.0;
                        item.nom = 1200.0; //grade
                        break;
                    case "VPR":
                        item.min = 6.0;
                        item.nom = 10.0; //std
                        break;
                    case "RE":
                        item.min = 15.0;
                        item.nom = 21.0; //std
                        break;
                    case "VPRAM":
                        item.min = 0.1;// item.max * 0.75; // std
                        item.nom = 0.1; //std
                }
                itemMap.put(item.sign, item);
                tableView.refresh();
            }
        }

        void calculate() {
            calcMinNom();

            indPPC = 1.0/3 * (indPExpr("fTp")*indPExpr("NCp") + indPExpr("fTRAM")*indPExpr("VRAM")
                    + indPExpr("SHDD")*indPExpr("VHDD"))
                    * indPExpr("Cp");
            indPNET = 0.5 * (indPExpr("NPT") + indPExpr("NPR")) * indPExpr("VN") * indPExpr("CNET");
            indPP = 0.5 * (indPExpr("VPR") + indPExpr("RE")) * indPExpr("VRAM") * indPExpr("RP");

        }

        double indPsumSquare() {
            return indPPC*indPPC + indPNET*indPNET + indPP*indPP;
        }

        void printLog() {
            appendResultValue("P-PC", indPPC);
            appendResultValue("P-NET", indPNET);
            appendResultValue("P-PP", indPP);
        }
    }
    class SsResource extends BaseResource {
        double indPPC, indPNET, indPP;

        SsResource(ObservableList<ResourceItem> observableListExt, TableView<ResourceItem> tableView) {
            this.observableList = observableListExt;
            this.tableView = tableView;

            int number = 1;
            observableList.add(new ResourceItem(number++,"Розрядність ОС","COS","біт",32.0));
            observableList.add(new ResourceItem(number++,"Кількість ядер процесора, підтримуваних ОС","NCOS","шт.",4.0));
            observableList.add(new ResourceItem(number++,"Кількість одночасних задач","NTOS","шт.",1000.0));
            observableList.add(new ResourceItem(number++,"Кількість працюючих користувачів одночасно","NUOS","осіб",3.0));
            observableList.add(new ResourceItem(number++,"Тривалість виконання однієї операції","TOS","с",0.1));
            observableList.add(new ResourceItem(number++,"Розрядність СУБД","CDB","біт",32.0));
            observableList.add(new ResourceItem(number++,"Наявний розмір бази даних","VDB","Тбайт",0.5));
            observableList.add(new ResourceItem(number++,"Наявний розмір таблиці БД","VDBT","Гбайт",5.0));
            observableList.add(new ResourceItem(number++,"Наявна кількість стовпців у записі","VDCR","шт.",8.0));
            observableList.add(new ResourceItem(number++,"Кількість типів даних, що підтримується","VDBDT","шт.",32.0));
            observableList.add(new ResourceItem(number++,"Середня тривалість виконання запиту","TDB","с",17.0));
            observableList.add(new ResourceItem(number++,"Розрядність редактора","CE","біт",64.0));
            observableList.add(new ResourceItem(number++,"Кількість вбудованих функцій","NEF","шт.",152.0));
            observableList.add(new ResourceItem(number++,"Кількість форматів документів","NED","шт.",5.0));
            observableList.add(new ResourceItem(number++,"Наявний об'єм документу","VED","Гбайт",0.5));
            observableList.add(new ResourceItem(number++,"Розрядність генератора звітів","CRG","біт",64.0));
            observableList.add(new ResourceItem(number++,"Наявний об'єм початкових даних","VRGIN","Гбайт",0.1));
            observableList.add(new ResourceItem(number++,"Кількість кодувань, що підтримуються","NRGC","шт.",5.0));
            observableList.add(new ResourceItem(number++,"Кількість форматів звітів","NRGF","шт.",10.0));
            observableList.add(new ResourceItem(number++,"Кількість графічних форматів","NRGGF","шт.",3.0));
            observableList.add(new ResourceItem(number++,"Кількість форматів баз даних","NRGDB","шт.",2.0));
            observableList.add(new ResourceItem(number++,"Тривалість генерування звіту","TRG","кБайт/с",100.0));
        }

        void calcMinNom() {
            for(var item: observableList) {
                switch (item.sign) {
                    case "COS":
                    case "CDB":
                        item.min = 8.0;
                        item.nom = item.max;
                        break;
                    case "NCOS":
                        item.min = (double) (int) (item.max * 0.75);
                        defRandomNomInt(item);
                        break;
                    case "NTOS":
                        item.min = 600.0;
                        defRandomNomInt(item);
                        break;
                    case "NUOS":
                    case "NRGGF":
                    case "NRGDB":
                        item.min = 1.0;
                        defRandomNomInt(item);
                        break;
                    case "TOS":
                        item.min = 0.05;
                        defRandomNomDouble(item);
                        break;
                    case "VDB":
                        item.min = 0.3;
                        defRandomNomDouble(item);
                        break;
                    case "VDBT":
                        item.min = 1.0;
                        defRandomNomDouble(item);
                        break;
                    case "VDCR":
                        item.min = 4.0;
                        defRandomNomInt(item);
                        break;
                    case "VDBDT":
                        item.min = 20.0;
                        defRandomNomInt(item);
                        break;
                    case "TDB":
                        item.min = 10.0;
                        defRandomNomDouble(item);
                        break;
                    case "CE":
                    case "CRG":
                        item.min = 16.0;
                        item.nom = item.max;
                        break;
                    case "NEF":
                        item.min = 100.0;
                        defRandomNomInt(item);
                        break;
                    case "NED":
                    case "NRGC":
                        item.min = 3.0;
                        defRandomNomInt(item);
                        break;
                    case "VED":
                        item.min = 0.1;
                        defRandomNomDouble(item);
                        break;
                    case "VRGIN":
                        item.min = 0.02;
                        defRandomNomDouble(item);
                        break;
                    case "NRGF":
                        item.min = 5.0;
                        defRandomNomInt(item);
                        break;
                    case "TRG":
                        item.min = 50.0;
                        defRandomNomDouble(item);
                        break;
                }
                itemMap.put(item.sign, item);
                tableView.refresh();
            }
        }

        void calculate() {
            calcMinNom();
        }

        double indPsumSquare() {
            return indPPC*indPPC + indPNET*indPNET + indPP*indPP;
        }

        void printLog() {
            appendResultValue("P-PC", indPPC);
            appendResultValue("P-NET", indPNET);
            appendResultValue("P-PP", indPP);
        }
    }

    abstract class BaseResource {
        protected ObservableList<ResourceItem> observableList;
        protected TableView<ResourceItem> tableView;
        protected Map<String, ResourceItem> itemMap = new HashMap<>();

        protected Double indPExpr(String sign) {
            return itemMap.get(sign).nom / itemMap.get(sign).max;
        }

        protected void defRandomNomDouble(ResourceItem item) {
            item.nom =  item.min + (item.max - item.min) * random();
        }
        protected void defRandomNomInt(ResourceItem item) {
            item.nom =  (double)(int)(item.min + (item.max - item.min) * random());
        }
    }

    public static class ResourceItem {
        String name, sign, unit;
        Double min, nom, max;
        Integer number;

        public ResourceItem(Integer number, String name, String sign, String unit, Double max) {
            this.number = number;
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

        public String getNumber() {
            return String.format("%d", number);
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
                return  new DecimalFormat("#.###").format(val);
            } else {
                return String.format("%d", val.intValue());
            }
        }

        @Override
        public String toString() {
            return "{sign=" + sign +
                    ", unit=" + unit +
                    ", min=" + min +
                    ", nom=" + nom +
                    ", max=" + max +
                    '}';
        }
    }

    private void initTable(TableView<ResourceItem> tableView, ScrollPane scrollPane, ObservableList<ResourceItem> observableList) {
        // Table already initialized
        if (tableView.getColumns().size() > 0) {
            // avoid FX bug with zero getWidth initialized from constructor
            // tableView.getColumns().get(1).setPrefWidth(scrollPane.getWidth() * 0.55);
            tableView.setPrefWidth(scrollPane.getWidth());
            return;
        }
        tableView.setEditable(true);

        List<String[]> columnConfig = new ArrayList<>(){{
            add(new String[]{"number", "№"});
            add(new String[]{"name", "Name"});
            add(new String[]{"sign", "Sign"});
            add(new String[]{"unit", "Unit"});
            add(new String[]{"min", "Min"});
            add(new String[]{"nom", "Nom"});
            add(new String[]{"max", "Max"});
        }};
        TableColumn<ResourceItem, String> tableColumn;
        final String EDITABLE_COLUMN = "max";

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
}
