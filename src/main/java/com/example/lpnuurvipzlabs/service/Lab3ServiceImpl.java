package com.example.lpnuurvipzlabs.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.text.DecimalFormat;
import java.util.*;

import static java.lang.Math.*;

public class Lab3ServiceImpl extends TextResultBase implements Lab3Service {

    private final TextArea resultArea;
    private final ScrollPane scrollPane1, scrollPane2, scrollPane3;
    private final TableView<ResourceItem> tsTableView = new TableView<>();
    private final TableView<ResourceItem> ssTableView = new TableView<>();
    private final TableView<ResourceItem> msTableView = new TableView<>();
    private final ObservableList<ResourceItem> tsObservableArrayList = FXCollections.observableArrayList();
    private final ObservableList<ResourceItem> ssObservableArrayList = FXCollections.observableArrayList();
    private final ObservableList<ResourceItem> msObservableArrayList = FXCollections.observableArrayList();
    private final TsResource tsResource;
    private final SsResource ssResource;
    private final MsResource msResource;

    private final int[][] incidenceTsSsMatrix = new int[][]{
            {1, 0, 0, 1, 1, 1, 0},
            {0, 1, 0, 1, 0, 0, 1},
            {0, 0, 1, 0, 1, 1, 0},
            {1, 0, 1, 1, 0, 0, 1},
            {0, 1, 0, 1, 1, 1, 0},
            {0, 0, 1, 1, 0, 0, 1},
            {0, 1, 1, 0, 1, 1, 1},
            {1, 0, 1, 1, 0, 1, 0},
            {0, 1, 0, 1, 1, 0, 1},
            {1, 0, 1, 1, 0, 1, 0},
            {0, 0, 1, 0, 1, 0, 1},
            {0, 0, 0, 1, 0, 1, 0},
            {0, 1, 1, 0, 1, 0, 1},
            {1, 0, 0, 1, 0, 1, 0},
            {0, 1, 1, 0, 1, 0, 1},
    };
    private final int[][] incidenceMsSsMatrix = new int[][]{
            {1, 0, 1},
            {1, 1, 0},
            {0, 0, 1},
            {1, 1, 0},
            {0, 0, 1},
            {1, 1, 0},
            {1, 0, 1},
            {1, 1, 0},
            {0, 1, 1},
            {0, 0, 1},
            {1, 1, 0},
            {1, 0, 1},
            {0, 1, 0},
            {1, 1, 0},
            {1, 0, 1},
    };

    public Lab3ServiceImpl(TextArea resultArea, ScrollPane scrollPane1, ScrollPane scrollPane2, ScrollPane scrollPane3) {
        this.resultArea = resultArea;
        this.scrollPane1 = scrollPane1;
        this.scrollPane2 = scrollPane2;
        this.scrollPane3 = scrollPane3;

        tsResource = new TsResource(tsObservableArrayList, tsTableView);
        ssResource = new SsResource(ssObservableArrayList, ssTableView);
        msResource = new MsResource(msObservableArrayList, msTableView);
        initTables();
    }

    private void initTables() {
        initTable(tsTableView, scrollPane1, tsObservableArrayList);
        initTable(ssTableView, scrollPane2, ssObservableArrayList);
        initTable(msTableView, scrollPane3, msObservableArrayList);
    }

    @Override
    public String calculate() {
        initTables();
        resetResult();
        appendResultText("Calculation result");
        appendResultNewline();

        tsResource.calculate();
        ssResource.calculate();
        msResource.calculate();

        var zeqMatrix = buildZeqMatrix();
        var zusMatrix = buildZusMatrix();
        var heqMatrix = buildHeqMatrix();
        var husMatrix = buildHusMatrix();
        var rTsSsMatrix = buildRMatrix(zeqMatrix, zusMatrix, incidenceTsSsMatrix);
        var rMsSsMatrix = buildRMatrix(heqMatrix, husMatrix, incidenceMsSsMatrix);

        var mulRTsSsPTs = multiplyMatrices(rTsSsMatrix, transposeMatrix(new Double[][]{tsResource.pVector}));
        var mulRMsSsPMs = multiplyMatrices(rMsSsMatrix, transposeMatrix(new Double[][]{msResource.pVector}));

        Double[][] mulOfRProducts = new Double[mulRTsSsPTs.length][mulRTsSsPTs[0].length];
        for (int i = 0; i<mulRTsSsPTs.length; i++) {
            mulOfRProducts[i][0] = mulRTsSsPTs[i][0] * mulRMsSsPMs[i][0];
        }
        var q = multiplyMatrices(transposeMatrix(mulOfRProducts), transposeMatrix(new Double[][]{ssResource.pVector}));

        appendResultValue("Q", q[0][0], 4);
        appendResultValue( "|P|",
                sqrt(tsResource.sumSquarePIndices() + ssResource.sumSquarePIndices() + msResource.sumSquarePIndices()),
                4
        );
        appendResultValue("Euclidean norm of R-TS-SS Matrix", calcEuclideanNorm(rTsSsMatrix), -1);
        appendResultValue("Euclidean norm of R-MS-SS Matrix", calcEuclideanNorm(rMsSsMatrix), -1);
        appendResultNewline();

        appendResultText("======== Calculation Log ========");
        tsResource.printLog();
        ssResource.printLog();
        msResource.printLog();
        dumpMatrixWithHeader(zeqMatrix, "Zeq Matrix", tsResource.matrixSignList, ssResource.matrixSignList);
        dumpMatrixWithHeader(zusMatrix, "Zus Matrix", tsResource.matrixSignList, ssResource.matrixSignList);
        dumpMatrixWithHeader(heqMatrix, "Heq Matrix", msResource.matrixSignList, ssResource.matrixSignList);
        dumpMatrixWithHeader(husMatrix, "Hus Matrix", msResource.matrixSignList, ssResource.matrixSignList);
        dumpMatrixWithHeader(rTsSsMatrix, "R-TS-SS Matrix", tsResource.matrixSignList, ssResource.matrixSignList);
        dumpMatrixWithHeader(rMsSsMatrix, "R-MS-SS Matrix", msResource.matrixSignList, ssResource.matrixSignList);
        dumpMatrix(mulRTsSsPTs, "R-TS-SS x P-TS");
        dumpMatrix(mulRMsSsPMs, "R-MS-SS x P-MS");
        dumpMatrix(transposeMatrix(mulOfRProducts), "Multiplication of R Matrices Transposed");

        return getResult();
    }

    private void dumpMatrix(Double[][] matrix, String title) {
        appendResultNewline();
        appendResultText(title+":");
        var format = new DecimalFormat("#.###");
        for (Double[] row : matrix) {
            String line = "";
            for (Double val : row) {
                line += format.format(val) +"\t";
            }
            line = line.stripTrailing();
            appendResultText(line);
        }
        appendResultNewline();
    }

    private void dumpMatrixWithHeader(Double[][] matrix, String title, List<String> wHeader, List<String> hHeader) {
        appendResultNewline();
        appendResultText(title+":");
        var defaultDelim = Arrays.stream(matrix).flatMap(Arrays::stream)
                .map(val -> new DecimalFormat("#.###").format(val))
                .mapToInt(String::length)
                .max().orElseGet(() -> 0) >= 8 ? "\t\t" : "\t";
        String line = defaultDelim;
        for (var val: wHeader) {
            line += val +defaultDelim;
        }
        appendResultText(line.stripTrailing());

        var format = new DecimalFormat("#.###");
        int i = 0;
        for (Double[] row : matrix) {
            line = hHeader.get(i++) +defaultDelim;
            for (Double val : row) {
                var displayVal = format.format(val);
                line += displayVal + (displayVal.length() >= 8 ? "\t" : defaultDelim);
            }
            line = line.stripTrailing();
            appendResultText(line);
        }
        appendResultNewline();
    }

    private Double[][] buildZeqMatrix() {
        return buildAnyEqMatrix(tsResource, ssResource);
    }
    private Double[][] buildZusMatrix() {
        return buildAnyUsMatrix(tsResource, ssResource);
    }
    private Double[][] buildHeqMatrix() {
        return buildAnyEqMatrix(msResource, ssResource);
    }
    private Double[][] buildHusMatrix() {
        return buildAnyUsMatrix(msResource, ssResource);
    }

    private Double[][] buildAnyEqMatrix(BaseResource wResource, BaseResource hResource) {
        var height = hResource.pVector.length;
        var width = wResource.pVector.length;
        Double[][] result = new Double[height][width];

        for (int h=0; h < height; h++) {
            for (int w=0; w < width; w++) {
                result[h][w] = hResource.getAnyEqMatrixVal(h) * wResource.getAnyEqMatrixVal(w);
            }
        }
        return result;
    }
    private Double[][] buildAnyUsMatrix(BaseResource wResource, BaseResource hResource) {
        var height = hResource.pVector.length;
        var width = wResource.pVector.length;
        Double[][] result = new Double[height][width];

        for (int h=0; h < height; h++) {
            for (int w=0; w < width; w++) {
                result[h][w] = hResource.getAnyUsMatrixVal(h) * wResource.getAnyUsMatrixVal(w);
            }
        }
        return result;
    }

    private Double[][] buildRMatrix(Double[][] eqMatrix, Double[][] usMatrix, int[][] incidenceMatrix) {
        var height = incidenceMatrix.length;
        var width = incidenceMatrix[0].length;
        Double[][] result = new Double[height][width];

        for (int h=0; h < height; h++) {
            for (int w=0; w < width; w++) {
                result[h][w] = incidenceMatrix[h][w] == 0 ? 0 : eqMatrix[h][w] / usMatrix[h][w];
            }
        }
        return result;
    }

    private Double calcEuclideanNorm(Double[][] matrix) {
        double result = 0.0;

        for (Double[] row : matrix) {
            result += Arrays.stream(row).map(val -> val * val).reduce(0.0, Double::sum);
        }
        return sqrt(result);
    }

    private Double[][] multiplyMatrices(Double[][] firstMatrix, Double[][] secondMatrix) {
        Double[][] result = new Double[firstMatrix.length][secondMatrix[0].length];

        for (int row = 0; row < result.length; row++) {
            for (int col = 0; col < result[row].length; col++) {
                result[row][col] = multiplyMatricesCell(firstMatrix, secondMatrix, row, col);
            }
        }

        return result;
    }
    private Double multiplyMatricesCell(Double[][] firstMatrix, Double[][] secondMatrix, int row, int col) {
        double cell = 0;
        for (int i = 0; i < secondMatrix.length; i++) {
            cell += firstMatrix[row][i] * secondMatrix[i][col];
        }
        return cell;
    }

    private Double[][] transposeMatrix(Double[][] input) {
        var result = new Double[input[0].length][input.length];

        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[0].length; j++) {
                result[j][i] = input[i][j];
            }
        }
        return result;
    }


    class TsResource extends BaseResource {
        double pPCIndex, pNETIndex, pPIndex;
        

        TsResource(ObservableList<ResourceItem> observableListExt, TableView<ResourceItem> tableView) {
            this.observableList = observableListExt;
            this.tableView = tableView;
            pVector = new Double[7];

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
            observableList.add(new ResourceItem(number++,"Роздільна здатність","RP","піксел",600.0));
            observableList.add(new ResourceItem(number++,"Швидкість друку (сканування)","VPR","стор./хв.",12.0));
            observableList.add(new ResourceItem(number++,"Швидкість обміну з ПК","RE","Мбіт/с",25.0));
            observableList.add(new ResourceItem(number++,"Об'єм ОЗП","VPRAM","Гбайт",0.128));

            matrixSignList = new ArrayList<>(Arrays.asList("fTp","fTRAM","SHDD","NPT","NPR","VPR","RE"));
        }

        void calcMinNom() {
            for(var item: observableList) {
                switch (item.sign) {
                    case "fTp", "fTRAM", "VRAM", "VHDD" -> {
                        item.min = item.max * 0.75;
                        defRandomNomDouble(item);
                    }
                    case "NCp" -> {
                        item.min = item.max >= 4d ? 4d : 2d;
                        item.nom = item.min;
                    }
                    case "Cp" -> {
                        item.min = 32d;
                        item.nom = item.max;
                    }
                    case "SHDD", "NPT", "VPR", "RE" -> {
                        item.min = ceil(item.max * 0.75);
                        defRandomNomInt(item);
                    }
                    case "VN" -> {
                        item.min = ceil(item.max * 0.5);
                        defRandomNomInt(item);
                    }
                    case "NPR" -> {
                        item.min = 1.0;
                        defRandomNomInt(item);
                    }
                    case "CNET" -> {
                        item.min = 8.0;
                        item.nom = item.max / 2;
                    }
                    case "RP" -> {
                        item.min = item.max >= 300d ? 300d : 1d;;
                        item.nom = item.min + 300 <= item.max ? item.min + 300 : item.min;
                    }
                    case "VPRAM" -> {
                        item.min = 0.1;
                        defRandomNomDouble(item);
                    }
                }
                itemMap.put(item.sign, item);
                tableView.refresh();
            }
        }

        void calculate() {
            calcMinNom();
            //P-PC: 0.648720
            //P-NET: 0.226042
            //P-PP: 0.659036
            pVector[0] = 1d/3 * indexPExpr("fTp") * indexPExpr("NCp") * indexPExpr("Cp");
            pVector[1] = 1d/3 * indexPExpr("fTRAM") * indexPExpr("VRAM") * indexPExpr("Cp");
            pVector[2] = 1d/3 * indexPExpr("SHDD") * indexPExpr("VHDD") * indexPExpr("Cp");

            pVector[3] = 0.5 * indexPExpr("NPT") * indexPExpr("VN") * indexPExpr("CNET");
            pVector[4] = 0.5 * indexPExpr("NPR") * indexPExpr("VN") * indexPExpr("CNET");

            pVector[5] = 0.5 * indexPExpr("VPR") * indexPExpr("VRAM") * indexPExpr("RP");
            pVector[6] = 0.5 * indexPExpr("RE") * indexPExpr("VRAM") * indexPExpr("RP");

            pPCIndex = pVector[0] + pVector[1] + pVector[2];
            pNETIndex = pVector[3] + pVector[4];
            pPIndex = pVector[5] + pVector[6];
        }

        @Override
        double sumSquarePIndices() {
            return pPCIndex * pPCIndex + pNETIndex * pNETIndex + pPIndex * pPIndex;
        }

        void printLog() {
            appendResultValue("P-PC Index", pPCIndex, 12);
            appendResultValue("P-NET Index", pNETIndex, 12);
            appendResultValue("P-PP Index", pPIndex, 12);
            appendResultValue("P-TS Vector", pVector);
            appendResultNewline();
        }
    }
    class SsResource extends BaseResource {
        double pOSIndex, pDBIndex, pEIndex, pRGIndex;

        SsResource(ObservableList<ResourceItem> observableListExt, TableView<ResourceItem> tableView) {
            this.observableList = observableListExt;
            this.tableView = tableView;
            pVector = new Double[15];

            int number = 1;
            observableList.add(new ResourceItem(number++,"Розрядність ОС","COS","біт",32.0));
            observableList.add(new ResourceItem(number++,"Кількість ядер процесора, підтримуваних ОС","NCOS","шт.",4.0));
            observableList.add(new ResourceItem(number++,"Кількість одночасних задач","NTOS","шт.",1000.0));
            observableList.add(new ResourceItem(number++,"Кількість працюючих користувачів одночасно","NUOS","осіб",4.0));
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
            observableList.add(new ResourceItem(number++,"Наявний об'єм документу","VED","Гбайт",0.1));
            observableList.add(new ResourceItem(number++,"Розрядність генератора звітів","CRG","біт",64.0));
            observableList.add(new ResourceItem(number++,"Наявний об'єм початкових даних","VRGIN","Гбайт",0.1));
            observableList.add(new ResourceItem(number++,"Кількість кодувань, що підтримуються","NRGC","шт.",5.0));
            observableList.add(new ResourceItem(number++,"Кількість форматів звітів","NRGF","шт.",10.0));
            observableList.add(new ResourceItem(number++,"Кількість графічних форматів","NRGGF","шт.",3.0));
            observableList.add(new ResourceItem(number++,"Кількість форматів баз даних","NRGDB","шт.",1.0));
            observableList.add(new ResourceItem(number++,"Тривалість генерування звіту","TRG","кБайт/с",100.0));

            matrixSignList = new ArrayList<>(Arrays.asList("NCOS", "NTOS", "NUOS", "VDBT", "VDCR", "VDBDT", "TDB", "NEF", "NED", "VED", "NRGC", "NRGF", "NRGGF", "NRGDB", "VRGIN"));
        }

        void calcMinNom() {
            for(var item: observableList) {
                switch (item.sign) {
                    case "COS", "CDB" -> {
                        item.min = 8.0;
                        item.nom = item.max;
                    }
                    case "NCOS" -> {
                        item.min = (double) (int) (item.max * 0.75);
                        defRandomNomInt(item);
                    }
                    case "NTOS" -> {
                        item.min = item.max >= 600d ? 600d : 1d;
                        defRandomNomInt(item);
                    }
                    case "NUOS", "NRGGF", "NRGDB" -> {
                        item.min = 1.0;
                        defRandomNomInt(item);
                    }
                    case "TOS" -> {
                        item.min = 0.05;
                        defRandomNomDouble(item);
                    }
                    case "VDB" -> {
                        item.min = 0.3;
                        defRandomNomDouble(item);
                    }
                    case "VDBT" -> {
                        item.min = 1.0;
                        defRandomNomDouble(item);
                    }
                    case "VDCR" -> {
                        item.min = item.max >= 4d ? 4d : 1d;;
                        defRandomNomInt(item);
                    }
                    case "VDBDT" -> {
                        item.min = item.max >= 20d ? 20d : 1d;;
                        defRandomNomInt(item);
                    }
                    case "TDB" -> {
                        item.min = item.max >= 10d ? 10d : 1d;;
                        defRandomNomDouble(item);
                    }
                    case "CE", "CRG" -> {
                        item.min = item.max >= 16d ? 16d : 1d;;
                        item.nom = item.max;
                    }
                    case "NEF" -> {
                        item.min = item.max >= 100d ? 100d : 1d;;
                        defRandomNomInt(item);
                    }
                    case "NED", "NRGC" -> {
                        item.min = item.max >= 3d ? 3d : 1d;;
                        defRandomNomInt(item);
                    }
                    case "VED" -> {
                        item.min = 0.1;
                        defRandomNomDouble(item);
                    }
                    case "VRGIN" -> {
                        item.min = 0.02;
                        defRandomNomDouble(item);
                    }
                    case "NRGF" -> {
                        item.min = item.max >= 5d ? 5d : 1d;;
                        defRandomNomInt(item);
                    }
                    case "TRG" -> {
                        item.min = item.max >= 50d ? 50d : 1d;;
                        defRandomNomDouble(item);
                    }
                }
                itemMap.put(item.sign, item);
                tableView.refresh();
            }
        }

        void calculate() {
            calcMinNom();

            pVector[0] = 1d/3 * indexPExpr("NCOS") * indexPExpr("COS") * indexPExpr("TOS");
            pVector[1] = 1d/3 * indexPExpr("NTOS") * indexPExpr("COS") * indexPExpr("TOS");
            pVector[2] = 1d/3 * indexPExpr("NUOS") * indexPExpr("COS") * indexPExpr("TOS");

            pVector[3] = 1d/4 * indexPExpr("VDBT") * indexPExpr("TDB") * indexPExpr("CDB");
            pVector[4] = 1d/4 * indexPExpr("VDCR") * indexPExpr("TDB") * indexPExpr("CDB");
            pVector[5] = 1d/4 * indexPExpr("VDBDT") * indexPExpr("TDB") * indexPExpr("CDB");
            pVector[6] = 1d/4 * indexPExpr("VDB") * indexPExpr("TDB") * indexPExpr("CDB");

            pVector[7] = 1d/3 * indexPExpr("NEF") * indexPExpr("TOS") * indexPExpr("CE");
            pVector[8] = 1d/3 * indexPExpr("NED") * indexPExpr("TOS") * indexPExpr("CE");
            pVector[9] = 1d/3 * indexPExpr("VED") * indexPExpr("TOS") * indexPExpr("CE");

            pVector[10] = 1d/5 * indexPExpr("NRGC") * indexPExpr("TRG") * indexPExpr("CRG");
            pVector[11] = 1d/5 * indexPExpr("NRGF") * indexPExpr("TRG") * indexPExpr("CRG");
            pVector[12] = 1d/5 * indexPExpr("NRGGF") * indexPExpr("TRG") * indexPExpr("CRG");
            pVector[13] = 1d/5 * indexPExpr("NRGDB") * indexPExpr("TRG") * indexPExpr("CRG");
            pVector[14] = 1d/5 * indexPExpr("VRGIN") * indexPExpr("TRG") * indexPExpr("CRG");

            pOSIndex = pVector[0] + pVector[1] + pVector[2];
            pDBIndex = pVector[3] + pVector[4] + pVector[5] + pVector[6];
            pEIndex = pVector[7] + pVector[8] + pVector[9];
            pRGIndex = pVector[10] + pVector[11] + pVector[12] + pVector[13] + pVector[14];
        }

        @Override
        double sumSquarePIndices() {
            return pOSIndex * pOSIndex + pDBIndex * pDBIndex + pEIndex * pEIndex + pRGIndex * pRGIndex;
        }

        void printLog() {
            appendResultValue("P-OS Index", pOSIndex, 12);
            appendResultValue("P-DB Index", pDBIndex, 12);
            appendResultValue("P-E Index", pEIndex, 12);
            appendResultValue("P-RG Index", pRGIndex, 12);
            appendResultValue("P-SS Vector", pVector);
            appendResultNewline();
        }
    }

    class MsResource extends BaseResource {
        double pMIndex;

        MsResource(ObservableList<ResourceItem> observableListExt, TableView<ResourceItem> tableView) {
            this.observableList = observableListExt;
            this.tableView = tableView;
            pVector = new Double[3];

            int number = 1;
            observableList.add(new ResourceItem(number++,"Кількість методів розв'язання задачі","NNM","шт.",4d));//
            observableList.add(new ResourceItem(number++,"Точність виконання розрахунків","PNM","%",0.5));
            observableList.add(new ResourceItem(number++,"Тривалість розв'язання задачі","TNM","с",25d));
            observableList.add(new ResourceItem(number++,"Тривалість підготовки вхідних даних","TPD","хв",7d));
            observableList.add(new ResourceItem(number++,"Тривалість поточної інтерпретації даних","TPID","хв",4d));
            observableList.add(new ResourceItem(number++,"Тривалість аналізу результатів розрахунку","TARR","хв",17d));

            matrixSignList = new ArrayList<>(Arrays.asList("TPD","TPID","TARR"));
        }

        void calcMinNom() {
            for(var item: observableList) {
                switch (item.sign) {
                    case "NNM" -> {
                        item.min = 1.0;
                        defRandomNomInt(item);
                    }
                    case "PNM" -> {
                        item.min = 0.1;
                        defRandomNomDouble(item);
                    }
                    case "TNM", "TARR" -> {
                        item.min = item.max >= 5d ? 5d : 1d;
                        defRandomNomDouble(item);
                    }
                    case "TPD" -> {
                        item.min = 1.0;
                        defRandomNomDouble(item);
                    }
                    case "TPID" -> {
                        item.min = 0.5;
                        defRandomNomDouble(item);
                    }
                }
                itemMap.put(item.sign, item);
                tableView.refresh();
            }
        }

        void calculate() {
            calcMinNom();
            pVector[0] = 1d/3 * indexPExpr("TPD") * indexPExpr("NNM") * indexPExpr("PNM") * indexPExpr("TNM");
            pVector[1] = 1d/3 * indexPExpr("TPID") * indexPExpr("NNM") * indexPExpr("PNM") * indexPExpr("TNM");
            pVector[2] = 1d/3 * indexPExpr("TARR") * indexPExpr("NNM") * indexPExpr("PNM") * indexPExpr("TNM");

            pMIndex = pVector[0] + pVector[1] + pVector[2];
        }

        @Override
        double sumSquarePIndices() {
            return pMIndex * pMIndex;
        }

        void printLog() {
            appendResultValue("P-M Index", pMIndex, 12);
            appendResultValue("P-MS Vector", pVector);
            appendResultNewline();
        }
    }

    abstract class BaseResource {
        protected ObservableList<ResourceItem> observableList;
        protected TableView<ResourceItem> tableView;
        protected Map<String, ResourceItem> itemMap = new HashMap<>();
        List<String> matrixSignList;
        Double[] pVector;

        abstract double sumSquarePIndices();

        public Double getAnyEqMatrixVal(int index) {
            return itemMap.get(matrixSignList.get(index)).max;
        }

        public Double getAnyUsMatrixVal(int index) {
            return itemMap.get(matrixSignList.get(index)).nom;
        }

        protected Double indexPExpr(String sign) {
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
                    resetResult();
                    resultArea.setText(getResult());
                });
            }
            tableView.getColumns().add(tableColumn);
        }

        scrollPane.setContent(tableView);
        tableView.setItems(observableList);
    }
}
