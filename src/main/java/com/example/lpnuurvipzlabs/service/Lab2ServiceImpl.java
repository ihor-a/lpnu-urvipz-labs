package com.example.lpnuurvipzlabs.service;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

import java.util.*;
import java.util.stream.IntStream;

import static java.lang.Math.*;

public class Lab2ServiceImpl extends TextResultBase implements Lab2Service {

    public ScrollPane scrollPane1, scrollPane2, scrollPane3;
    public Pane lineChartPane;

    int n = 28;
    double aMin = 1.16;
    double aMax = 1.51;
    double rMin = 0.15;
    double rMax = 0.2;
    double bMin = 36.0;
    double bMax = 46.8;
    double ciMin = 4.8;
    double ciMax = 6.2;
    int lMin = 3, lMax = 5;
    double ci1Min = 7.0, ci2Min = 4.3, ci3Min = 3.7, ci4Min = 3.5, ci5Min = 2.9;
    double ci1Max = 9.1, ci2Max = 5.6, ci3Max = 4.8, ci4Max = 4.6, ci5Max = 3.8;
    double lambdaMin = 0.76, lambdaMax = 0.99;
    double qMin = 1.03, qMax = 1.34;

    @Override
    public String calculate() {
        resetResult();

        calcTable1();
        appendResultNewline();
        calcTable2();

        return getResult();
    }

    private void calcTable2() {
        List<TableResultItem> tableResult = new ArrayList<>();

        double a = randomMinMax(aMin, aMax);
        var s1list = new ArrayList<>(Collections.nCopies(n, 0.0));
        var s2list = new ArrayList<>(Collections.nCopies(n, 0.0));
        var s3list = new ArrayList<>(Collections.nCopies(n, 0.0));
        var cNPVList = new ArrayList<>(Collections.nCopies(n, 0.0));

        for (int j = 0; j < n; j++) {
            var i = j + 1;
            var l = randomMinMax(lMin, lMax);
            var r = randomMinMax(rMin, rMax);
            var b = randomMinMax(bMin, bMax);
            var ci = randomMinMax(ciMin, ciMax);
            var cFrom1To5List = generateCFrom1To5();
            s1list.set(j, calcS1(i, s1list, b, a, ci, r));
            s2list.set(j, calcS2(i, l, s2list, cFrom1To5List, r));
            s3list.set(j, calcS3(i, l, s3list, cFrom1To5List, r));

            cNPVList.set(j, s1list.get(j) - s2list.get(j) - s3list.get(j));

            // a == F4 List2 (Excel)
            tableResult.add(new Table2ResultItem(l, i, r, b, ci, s1list.get(j), s2list.get(j), s3list.get(j), cNPVList.get(j)));
        }
        renderTable2(tableResult);

        Pair cNPVMinMax = calcCNPVMinMax();

        var cNPV = cNPVList.get(cNPVList.size()-1);
        var cNPVMin = cNPVMinMax.min;
        var cNPVMax = cNPVMinMax.max;
        var cNPVAvg = (cNPVMinMax.min + cNPVMinMax.max) / 2.0;

        var alpha = calcAlpha(cNPV, cNPVMin, cNPVMax, cNPVAvg);
        var r = calcR(cNPV, cNPVMin, cNPVMax);

        var rnpv = calcRNPV(cNPV, cNPVMin, cNPVMax, cNPVAvg, r, alpha);

        var rl = calcRL(cNPV, cNPVMin, cNPVMax, cNPVAvg);

        appendResultText("== Table2 Result ==");
        appendResultText(String.format("A = %.2f", a));
        appendResultText(String.format("C-NPV = %.2f", cNPV));
        appendResultText(String.format("min C-NPV = %.2f", cNPVMin));
        appendResultText(String.format("avg C-NPV = %.2f", cNPVAvg));
        appendResultText(String.format("max C-NPV = %.2f", cNPVMax));
        appendResultNewline();
        appendResultText(String.format("α = %.2f", alpha));
        appendResultText(String.format("R = %.2f", r));
        appendResultText(String.format("R-NPV = %.2f", rnpv));
        appendResultText(String.format("RL = %.2f%%", rl));
    }

    private void renderTable2(List<TableResultItem> resultList) {
        List<String[]> columnConfig = new ArrayList<>(){{
            add(new String[]{"l", "l"});
            add(new String[]{"i", "i"});
            add(new String[]{"r", "r"});
            add(new String[]{"b", "B"});
            add(new String[]{"ci", "C"});
            add(new String[]{"s1", "S1"});
            add(new String[]{"s2", "S2"});
            add(new String[]{"s3", "S3"});
            add(new String[]{"cnpv", "C-NPV"});
        }};

        renderTable(scrollPane2, columnConfig, resultList);
    }

    private void renderTable(ScrollPane scrollPane, List<String[]> columnConfig, List<TableResultItem> resultList) {
        TableView<TableResultItem> tableView = new TableView<>();
        tableView.setPrefWidth(scrollPane.getWidth());

        final double COLUMN_WIDTH = scrollPane.getWidth()/columnConfig.size() - 3;
        TableColumn<TableResultItem, String> tableColumn;

        for (String[] column: columnConfig) {
            tableColumn = new TableColumn<>(column[1]);
            tableColumn.setPrefWidth(COLUMN_WIDTH);
            tableColumn.setCellValueFactory(new PropertyValueFactory<>(column[0]));
            tableView.getColumns().add(tableColumn);
        }

        scrollPane.setContent(tableView);
        resultList.forEach(item -> tableView.getItems().add(item));
    }

    private void calcTable1() {
        List<TableResultItem> tableResult = new ArrayList<>();
        var stepsQuantity = 11; // excluded last

        var c1 = new Pair(ci1Min, ci1Max);
        var c2 = new Pair(ci2Min, ci2Max);
        var c3 = new Pair(ci3Min, ci3Max);

        var alphaList = new ArrayList<>(IntStream.range(0, stepsQuantity).boxed().map((e) -> e / 10.0).toList());
        Collections.reverse(alphaList);

        var npvList = new ArrayList<>(Collections.nCopies(stepsQuantity, new Pair(0, 0)));

        for (int j = 0; j < stepsQuantity; j++) {
            var i = j + 1;
            var a = getRangeMinMaxValue(i, stepsQuantity, aMin, aMax);
            var r = getRangeMinMaxValue(i, stepsQuantity, rMin, rMax);
            var b = getRangeMinMaxValue(i, stepsQuantity, bMin, bMax);
            var ci = getRangeMinMaxValue(i, stepsQuantity, ciMin, ciMax);
            var ci1 = getRangeMinMaxValue(i, stepsQuantity, c1.min, c1.max);
            var ci2 = getRangeMinMaxValue(i, stepsQuantity, c2.min, c2.max);
            var ci3 = getRangeMinMaxValue(i, stepsQuantity, c3.min, c3.max);

            npvList.set(j, new Pair(
                    calcNvp(r.min, b.min, a.min, ci.min, ci1.min, ci2.min, ci3.min),
                    calcNvp(r.max, b.max, a.max, ci.max, ci1.max, ci2.max, ci3.max)
                    ));
            tableResult.add(
                    new Table1ResultItem(alphaList.get(j), r, b, new Pair(a.min * ci.min, a.max * ci.max), ci1, ci2, ci3, npvList.get(j))
            );
        }
        renderTable1(tableResult);

        appendResultText("== Table1 Result ==");
        appendResultText(String.format("NPV min, max = %.2f, %.2f%n",
                npvList.get(npvList.size()-1).min,
                npvList.get(npvList.size()-1).max)
        );

        List<Point> pointList = new ArrayList<>();

        for (int idx = 0; idx < npvList.size(); idx++) {
            pointList.add(new Point(npvList.get(idx).min, alphaList.get(idx)));
            pointList.add(new Point(npvList.get(idx).max, alphaList.get(idx)));
        }
        renderLineChart(pointList);
    }

    private void renderTable1(List<TableResultItem> resultList) {
        List<String[]> columnConfig = new ArrayList<>(){{
            add(new String[]{"alpha", "α"});
            add(new String[]{"r", "r"});
            add(new String[]{"b", "B"});
            add(new String[]{"ac", "AC"});
            add(new String[]{"c1", "C1"});
            add(new String[]{"c2", "C2"});
            add(new String[]{"c3", "C3"});
            add(new String[]{"npv", "NPV"});
        }};

        renderTable(scrollPane1, columnConfig, resultList);
    }

    private void renderLineChart(List<Point> pointList) {
        XYChart.Series<Double, Double> series = new XYChart.Series<>();

        double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE;
        for (var point: pointList) {
            series.getData().add(new XYChart.Data<>(point.x, point.y));
            minX = Math.min(minX, point.x);
            maxX = Math.max(maxX, point.x);
        }
        var diffMaxMinX = (Math.abs(maxX) - Math.abs(minX));

        LineChart lineChart = new LineChart(
                new NumberAxis("NPV", minX-diffMaxMinX*0.1, maxX+diffMaxMinX*0.1, diffMaxMinX/pointList.size()),
                new NumberAxis()
        );
        lineChart.setLegendVisible(false);
        lineChart.setMaxWidth(lineChartPane.getWidth());
        lineChart.setMaxHeight(lineChartPane.getHeight());
        lineChart.getData().add(series);

        lineChartPane.getChildren().add(lineChart);
    }

    private double calcNvp(double r, double b, double a, double c, double c1, double c2, double c3) {
        return (b - a * c) / (1 + r) - (c1 + c2 + c3) / (1 + r);
    }

    private int randomMinMax(int min, int max) {
        var random = new Random();
        return min + random.nextInt(max - min + 1);
    }

    private double randomMinMax(double min, double max) {
        var random = new Random();
        return min + (max - min) * random.nextDouble();
    }

    private List<Double> generateCFrom1To5() {
        return new ArrayList<>(){{
            add(randomMinMax(ci1Min, ci1Max));
            add(randomMinMax(ci2Min, ci2Max));
            add(randomMinMax(ci3Min, ci3Max));
            add(randomMinMax(ci4Min, ci4Max));
            add(randomMinMax(ci5Min, ci5Max));
        }};
    }

    private double calcS1(int i, List<Double> s1list, double b, double a, double c, double r) {
        var prevSum = i == 1 ? 0 : s1list.get(i - 2);
        return prevSum + (b - a * c) / pow(1 + r, i);
    }

    private double calcS2(int i, int l, List<Double> s2List, List<Double> cFrom1To5List, double r) {
        var prevSum = i == 1 ? 0 : s2List.get(i - 2);
        var filteredC = cFrom1To5List.subList(0, min(l + 1, cFrom1To5List.size()));
        return prevSum +
                (filteredC.size() == 0
                        ? 0
                        : filteredC.stream().reduce(0.0, Double::sum) / pow(1 + r, i)
                );
    }

    double calcS3(int i, int l, List<Double> s3, List<Double> cFrom1To5List, double r) {
        var prevSum = i == 1 ? 0 : s3.get(i - 2);
        var lambda = randomMinMax(lambdaMin, lambdaMax);
        var q = randomMinMax(qMin, qMax);
        var filteredC = cFrom1To5List.subList(0, min(l + 1, cFrom1To5List.size()));
        var calcResSum = filteredC.size() == 0
                ? 0.0
                : filteredC.stream().reduce(0.0, (value, element) -> {
            var powValue = pow(Math.E, -lambda * q * element);
            return value + powValue;
        });
        var powValue = pow(1 + r, i);
        var calcRes = (calcResSum / powValue);
        return prevSum + calcRes;
    }

    private Pair calcCNPVMinMax() {

        Pair l = new Pair(lMin, lMax);
        Pair a = new Pair(aMin, aMax);
        var s1 = new ArrayList<>(Collections.nCopies(n, new Pair(0, 0)));
        var s2 = new ArrayList<>(Collections.nCopies(n, new Pair(0, 0)));
        var s3 = new ArrayList<>(Collections.nCopies(n, new Pair(0, 0)));
        var cNPVArr = new ArrayList<>(Collections.nCopies(n, new Pair(0, 0)));

        for (int j = 0; j < n; j++) {
            var i = j + 1;
            Pair r = getRangeMinMaxValue(i, n, rMin, rMax);
            Pair b = getRangeMinMaxValue(i, n, bMin, bMax);
            Pair ci = getRangeMinMaxValue(i, n, ciMin, ciMax);
            var c = generateCPair(i, n);

            s1.set(j, new Pair(
                    calcS1(i, s1.stream().map((e) -> e.min).toList(), b.min, a.min, ci.min, r.min),
                    calcS1(i, s1.stream().map((e) -> e.max).toList(), b.max, a.max, ci.max, r.max))
            );

            s2.set(j, new Pair(
                    calcS2(i, (int)l.min, s2.stream().map((e) -> e.min).toList(), c.stream().map((e) -> e.min).toList(), r.min),
                    calcS2(i, (int)l.max, s2.stream().map((e) -> e.max).toList(), c.stream().map((e) -> e.max).toList(), r.max))
            );

            s3.set(j, new Pair(
                    calcS3(i, (int)l.min, s3.stream().map((e) -> e.min).toList(), c.stream().map((e) -> e.min).toList(), r.min),
                    calcS3(i, (int)l.max, s3.stream().map((e) -> e.max).toList(), c.stream().map((e) -> e.max).toList(), r.max))
            );

            cNPVArr.set(j, new Pair(
                    s1.get(j).min - s2.get(j).min - s3.get(j).min,
                    s1.get(j).max - s2.get(j).max - s3.get(j).max)
            );
        }

        if(cNPVArr.get(cNPVArr.size()-1).max > cNPVArr.get(cNPVArr.size()-1).min) {
            return new Pair(cNPVArr.get(cNPVArr.size()-1).min, cNPVArr.get(cNPVArr.size()-1).max);
        } else {
            return new Pair(cNPVArr.get(cNPVArr.size()-1).max, cNPVArr.get(cNPVArr.size()-1).min);
        }
    }

    private Pair getRangeMinMaxValue(int i, int n, double min, double max) {
        double avg = (max + min) / 2;
        double step = (max - avg) / n;
        return new Pair(avg - (i - 1) * step, avg + (i - 1) * step);
    }

    private List<Pair> generateCPair(int i, int n) {
        return new ArrayList<>(){{
            add(getRangeMinMaxValue(i, n, ci1Min, ci1Max));
            add(getRangeMinMaxValue(i, n, ci2Min, ci2Max));
            add(getRangeMinMaxValue(i, n, ci3Min, ci3Max));
            add(getRangeMinMaxValue(i, n, ci4Min, ci4Max));
            add(getRangeMinMaxValue(i, n, ci5Min, ci5Max));
        }};
    }

    private double calcAlpha(double cNPV, double cNPVMin, double cNPVMax, double cNPVAvg) {
        double alpha;

        if (cNPV < cNPVMin) {
            alpha = 0;
        } else if (cNPV < cNPVAvg) {
            alpha = (cNPV - cNPVMin) / (cNPVAvg - cNPVMin);
        } else if (cNPV <= cNPVMax) {
            alpha = (cNPVMax - cNPV) / (cNPVMax - cNPVAvg);
        } else {
            alpha = 1;
        }
        return alpha;
    }

    private double calcRL(double cNPV, double cNPVMin, double cNPVMax, double cNPVAvg) {
        if (cNPV < cNPVAvg) {
            return cNPVMin * cNPVMin / ((cNPVAvg - cNPVMin) * (cNPVMax - cNPVMin));
        }

        return 1 - cNPVMax * cNPVMax / ((cNPVMax - cNPVAvg) * (cNPVMax - cNPVMin));
    }

    private double calcRNPV(double cNPV, double cNPVMin, double cNPVMax, double cNPVAvg, double r, double alpha) {
        double rnpv;

        if (cNPV < cNPVMin) {
            rnpv = 0;
        } else if (cNPV < cNPVAvg) {
            rnpv = r * (1 + (1 - alpha) / alpha * log(1 - alpha));
        } else if (cNPV <= cNPVMax) {
            rnpv = 1 - (1 - r) * (1 + (1 - alpha) / alpha * log(1 - alpha));
        } else {
            rnpv = 1;
        }

        return rnpv;
    }

    private double calcR(double cNPV, double cNPVMin, double cNPVMax) {
        if (cNPV < cNPVMax) {
            return (cNPV - cNPVMin) / (cNPVMax - cNPVMin);
        }

        return 1;
    }

    private record Pair(double min, double max) {
        @Override
        public String toString() {
            return String.format("%.2f, %.2f", min, max);
        }
    }

    record Point(double x, double y) {
    }

    interface TableResultItem {
    }
    public static class Table2ResultItem implements TableResultItem{

        int l, i;
        double r, b, ci, s1, s2, s3, cnpv;

        public Table2ResultItem(int l, int i, double r, double b, double ci, double s1, double s2, double s3, double cnpv) {
            this.l = l;
            this.i = i;
            this.r = r;
            this.b = b;
            this.ci = ci;
            this.s1 = s1;
            this.s2 = s2;
            this.s3 = s3;
            this.cnpv = cnpv;
        }

        public int getL() {
            return l;
        }
        public int getI() {
            return i;
        }

        public String getR() {
            return getNumeric(r);
        }

        public String getB() {
            return getNumeric(b);
        }

        public String getCi() {
            return getNumeric(ci);
        }

        public String getS1() {
            return getNumeric(s1);
        }

        public String getS2() {
            return getNumeric(s2);
        }

        public String getS3() {
            return getNumeric(s3);
        }

        public String getCnpv() {
            return getNumeric(cnpv);
        }

        private String getNumeric(double val) {
            return String.format("%.2f", val);
        }

        @Override
        public String toString() {
            return String.format("%d | %d | %.2f | %.2f | %.2f | %.2f | %.2f | %.2f | %.2f", l, i, r, b, ci, s1, s2, s3, cnpv);
        }
    }

    public static class Table1ResultItem implements TableResultItem {
        Double alpha;
        Pair r, b, ac, c1, c2, c3, npv;
        public Table1ResultItem(Double alpha, Pair r, Pair b, Pair ac, Pair c1, Pair c2, Pair c3, Pair npv) {
            this.alpha = alpha;
            this.r = r;
            this.b = b;
            this.ac = ac;
            this.c1 = c1;
            this.c2 = c2;
            this.c3 = c3;
            this.npv = npv;
        }

        public String getAlpha() {
            return String.format("%.2f", alpha);
        }

        public Pair getR() {
            return r;
        }

        public Pair getB() {
            return b;
        }

        public Pair getAc() {
            return ac;
        }

        public Pair getC1() {
            return c1;
        }

        public Pair getC2() {
            return c2;
        }

        public Pair getC3() {
            return c3;
        }

        public Pair getNpv() {
            return npv;
        }

        @Override
        public String toString() {
            return String.format("%.2f  %s  %s  %s  %s  %s  %s  %s", alpha, r, b, ac, c1, c2, c3, npv);
        }
    }
}
