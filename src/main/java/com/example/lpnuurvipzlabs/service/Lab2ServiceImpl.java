package com.example.lpnuurvipzlabs.service;

import java.util.*;

import static java.lang.Math.*;

public class Lab2ServiceImpl extends TextResultBase implements Lab2Service {

//    MatrixInput("min", 28, 1.16, 0.15, 36.00, 4.8, 3.0, //7.0, 4.3, 3.7, 3.5, 2.9, //0.76, 1.03),
//    MatrixInput("max",  0, 1.51, 0.20, 46.80, 6.2, 5.0, //9.1, 5.6, 4.8, 4.6, 3.8, //0.99, 1.34)
    int n = 28;
    double aMin = 1.16;
    double aMax = 1.51;
    double a = _randBetweenDouble(aMin, aMax);
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

    List<Double> s1, s2, s3, cNPVArr;


    @Override
    public String calculate() {
        resetResult();

        _regenerateMatrix1();
        appendResultNewline();

        _regenerateMatrix2();

        return getResult();
    }

    void _regenerateMatrix1() {
        appendResultText("MATRIX-1");

        List<MatrixResultItem> matrix1 = new ArrayList<>();

        var s1 = new ArrayList<>(Collections.nCopies(n, 0.0));
        var s2 = new ArrayList<>(Collections.nCopies(n, 0.0));
        var s3 = new ArrayList<>(Collections.nCopies(n, 0.0));
        var cNPVArr = new ArrayList<>(Collections.nCopies(n, 0.0));

        for (int j = 0; j < n; j++) {
            var i = j + 1;
            var l = _randBetween(lMin, lMax);
            var r = _randBetweenDouble(rMin, rMax);
            var b = _randBetweenDouble(bMin, bMax);
            var ci = _randBetweenDouble(ciMin, ciMax);
            var c = _generateC();
            s1.set(j, _calcSi1(i, s1, b, a, ci, r));
            s2.set(j, _calcSi2(i, l, s2, c, r));
            s3.set(j, _calcSi3(i, l, s3, c, r));

            cNPVArr.set(j, s1.get(j) - s2.get(j) - s3.get(j));

//            var cNPVArrDebug = cNPVArr.stream().map(aDouble -> String.format("%.2f", aDouble)).toArray();

            // __RESULT ???
            matrix1.add(new MatrixResultItem(l, i, a, r, b, ci, s1.get(j), s2.get(j), s3.get(j), cNPVArr.get(j)));
        }
        appendResultText("l, i, a, r, b, ci, s1, s2, s3, cNPVArr");
        matrix1.forEach(matrixResultItem -> appendResultText(matrixResultItem.toString()));
        appendResultNewline();

        // ???
//        this.matrix1 = matrix1;
//        matrix1Key = DateTime.now().millisecondsSinceEpoch.toString();

        Pair cNPVMinMax = calcCNPVMinMax();

        var cNPV = cNPVArr.get(cNPVArr.size()-1);
        var cNPVMin = cNPVMinMax.min;
        var cNPVMax = cNPVMinMax.max;
        var cNPVAvg = (cNPVMinMax.min + cNPVMinMax.max) / 2.0;

        var alpha = _calcAlpha(cNPV, cNPVMin, cNPVMax, cNPVAvg);
        var r = _calcR(cNPV, cNPVMin, cNPVMax);

        var rnpv = _calcRNPV(cNPV, cNPVMin, cNPVMax, cNPVAvg, r, alpha);

        var rl = _calcRL(cNPV, cNPVMin, cNPVMax, cNPVAvg);

        // ???
        String tempNPVResult = "";
        tempNPVResult += String.format("C-NPV = %.2f%n", cNPV);
        tempNPVResult += String.format("C-NPV MIN = %.2f%n", cNPVMin);
        tempNPVResult += String.format("C-NPV MAX = %.2f%n", cNPVMax);
        tempNPVResult += String.format("C-NPV AVG = %.2f%n", cNPVAvg);
        tempNPVResult += String.format("Alpha = %.2f%n", alpha);
        tempNPVResult += String.format("R = %.2f%n", r);
        tempNPVResult += String.format("R-NPV = %.2f%n", rnpv);
        tempNPVResult += String.format("RiskL = %.2f%%", rl);
        // ???
        //npvTextResult = tempNPVResult;
        appendResultText(tempNPVResult);
    }

    void _regenerateMatrix2() {
//        var matrix = List<MatrixResultItem2>.empty(growable: true);
        appendResultText("MATRIX-2");

        List<MatrixResultItem2> matrix = new ArrayList<>();
        // ???
        var n = 11;
//        var aMin = matrix0[0].aValue;
//        var aMax = matrix0[1].aValue;
//        var rMin = matrix0[0].riValue;
//        var rMax = matrix0[1].riValue;
//        var bMin = matrix0[0].biValue;
//        var bMax = matrix0[1].biValue;
//        var ciMin = matrix0[0].ciValue;
//        var ciMax = matrix0[1].ciValue;

        var c1 = new Pair(ci1Min, ci1Max);
        var c2 = new Pair(ci2Min, ci2Max);
        var c3 = new Pair(ci3Min, ci3Max);

        var alpha = new ArrayList<>(_range(0, n).stream().map((e) -> e / 10.0).toList());
        Collections.reverse(alpha);

        var npvArr = new ArrayList<>(Collections.nCopies(n, new Pair(0, 0)));

        for (int j = 0; j < n; j++) {
            var i = j + 1;
            var a = _getRangeMinMaxValue(i, n, aMin, aMax);
            var r = _getRangeMinMaxValue(i, n, rMin, rMax);
            var b = _getRangeMinMaxValue(i, n, bMin, bMax);
            var ci = _getRangeMinMaxValue(i, n, ciMin, ciMax);
            var ci1 = _getRangeMinMaxValue(i, n, c1.min, c1.max);
            var ci2 = _getRangeMinMaxValue(i, n, c2.min, c2.max);
            var ci3 = _getRangeMinMaxValue(i, n, c3.min, c3.max);

            npvArr.set(j, new Pair(
                    _calcNvp(r.min, b.min, a.min, ci.min, ci1.min, ci2.min, ci3.min),
                    _calcNvp(r.max, b.max, a.max, ci.max, ci1.max, ci2.max, ci3.max)
                    ));
            matrix.add(
                    new MatrixResultItem2(alpha.get(j), r, b, new Pair(a.min * ci.min, a.max * ci.max), ci1, ci2, ci3, npvArr.get(j))
            );
        }

        appendResultText("alpha, r, b, pair, ci1, ci2, ci3, npvArr");
        matrix.forEach(matrixResultItem2 -> appendResultText(matrixResultItem2.toString()));
        appendResultNewline();

        String npvTextResult2Temp = "";
        npvTextResult2Temp += String.format("NPV MIN = %.2f%n", npvArr.get(npvArr.size()-1).min);
        npvTextResult2Temp += String.format("NPV MAX = %.2f%n", npvArr.get(npvArr.size()-1).max);

        appendResultText(npvTextResult2Temp);

        List<Point> tempPoints = new ArrayList<>();
        List<Point> tempPoints1 = new ArrayList<>();
        for (int m = 0; m < npvArr.size(); m++) {
            tempPoints.add(new Point(npvArr.get(m).min, alpha.get(m)));
        }
        for (int m = 0; m < npvArr.size(); m++) {
            tempPoints1.add(new Point(npvArr.get(m).max, alpha.get(m)));
        }

        // ???
//        points = tempPoints;
//        points1 = tempPoints1;
//        npvTextResult2 = npvTextResult2Temp;

        // ???
//        matrix2 = matrix;
//        matrix2Key = DateTime.now().millisecondsSinceEpoch.toString();
    }

    double _calcNvp(double r, double b, double a, double c, double c1, double c2, double c3) {
        return (b - a * c) / (1 + r) - (c1 + c2 + c3) / (1 + r);
    }

    List<Integer> _range(int from, int to) {
        List<Integer> result = new ArrayList<>(to - from);
        for (int i = 0; i < to-from; i++) {
            result.add(i + from);
        }
        // ???
//        List.generate(to - from, (i) = > i + from);
        return result;
    }

    int _randBetween(int min, int max) {
        var rand = new Random();
        return min + rand.nextInt(max - min + 1);
    }

    double _randBetweenDouble(double min, double max) {
        var rand = new Random();
        return min + (max - min) * rand.nextDouble();
    }

    List<Double> _generateC() {
        return new ArrayList<>(){{
            add(_randBetweenDouble(ci1Min, ci1Max));
            add(_randBetweenDouble(ci2Min, ci2Max));
            add(_randBetweenDouble(ci3Min, ci3Max));
            add(_randBetweenDouble(ci4Min, ci4Max));
            add(_randBetweenDouble(ci5Min, ci5Max));
        }};
    }

    double _calcSi1(int i, List<Double> s1, double b, double a, double c, double r) {
        var prevSum = i == 1 ? 0 : s1.get(i - 2);
        return prevSum + (b - a * c) / pow(1 + r, i);
    }

    double _calcSi2(int i, int l, List<Double> s2, List<Double> c, double r) {
        var prevSum = i == 1 ? 0 : s2.get(i - 2);
        var filteredC = c.subList(0, min(l + 1, c.size()));
        return prevSum +
                (filteredC.size() == 0
                        ? 0
                        : filteredC.stream().reduce(0.0, Double::sum) / pow(1 + r, i)
                );
    }

    double _calcSi3(int i, int l, List<Double> s3, List<Double> c, double r) {
        var prevSum = i == 1 ? 0 : s3.get(i - 2);
        var lambda = _randBetweenDouble(lambdaMin, lambdaMax);
        var q = _randBetweenDouble(qMin, qMax);
        var filteredC = c.subList(0, min(l + 1, c.size()));
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

    Pair calcCNPVMinMax() {

        Pair l = new Pair(lMin, lMax);
        Pair a = new Pair(aMin, aMax);
        var s1 = new ArrayList<>(Collections.nCopies(n, new Pair(0, 0)));
        var s2 = new ArrayList<>(Collections.nCopies(n, new Pair(0, 0)));
        var s3 = new ArrayList<>(Collections.nCopies(n, new Pair(0, 0)));
        var cNPVArr = new ArrayList<>(Collections.nCopies(n, new Pair(0, 0)));

        for (int j = 0; j < n; j++) {
            var i = j + 1;
            Pair r = _getRangeMinMaxValue(i, n, rMin, rMax);
            Pair b = _getRangeMinMaxValue(i, n, bMin, bMax);
            Pair ci = _getRangeMinMaxValue(i, n, ciMin, ciMax);
            var c = _generateCPair(i, n);

            s1.set(j, new Pair(
                    _calcSi1(i, s1.stream().map((e) -> e.min).toList(), b.min, a.min, ci.min, r.min),
                    _calcSi1(i, s1.stream().map((e) -> e.max).toList(), b.max, a.max, ci.max, r.max))
            );

            s2.set(j, new Pair(
                    _calcSi2(i, (int)l.min, s2.stream().map((e) -> e.min).toList(), c.stream().map((e) -> e.min).toList(), r.min),
                    _calcSi2(i, (int)l.max, s2.stream().map((e) -> e.max).toList(), c.stream().map((e) -> e.max).toList(), r.max))
            );

            s3.set(j, new Pair(
                    _calcSi3(i, (int)l.min, s3.stream().map((e) -> e.min).toList(), c.stream().map((e) -> e.min).toList(), r.min),
                    _calcSi3(i, (int)l.max, s3.stream().map((e) -> e.max).toList(), c.stream().map((e) -> e.max).toList(), r.max))
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

    Pair _getRangeMinMaxValue(int i, int n, double min, double max) {
        double avg = (max + min) / 2;
        double step = (max - avg) / n;
        return new Pair(avg - (i - 1) * step, avg + (i - 1) * step);
    }

    List<Pair> _generateCPair(int i, int n) {
        return new ArrayList<>(){{
            add(_getRangeMinMaxValue(i, n, ci1Min, ci1Max));
            add(_getRangeMinMaxValue(i, n, ci2Min, ci2Max));
            add(_getRangeMinMaxValue(i, n, ci3Min, ci3Max));
            add(_getRangeMinMaxValue(i, n, ci4Min, ci4Max));
            add(_getRangeMinMaxValue(i, n, ci5Min, ci5Max));
        }};
    }

    double _calcAlpha(double cNPV, double cNPVMin, double cNPVMax, double cNPVAvg) {
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

    double _calcRL(double cNPV, double cNPVMin, double cNPVMax, double cNPVAvg) {
        if (cNPV < cNPVAvg) {
            return cNPVMin * cNPVMin / ((cNPVAvg - cNPVMin) * (cNPVMax - cNPVMin));
        }

        return 1 - cNPVMax * cNPVMax / ((cNPVMax - cNPVAvg) * (cNPVMax - cNPVMin));
    }

    double _calcRNPV(double cNPV, double cNPVMin, double cNPVMax, double cNPVAvg, double r, double alpha) {
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

    double _calcR(double cNPV, double cNPVMin, double cNPVMax) {
        if (cNPV < cNPVMax) {
            return (cNPV - cNPVMin) / (cNPVMax - cNPVMin);
        }

        return 1;
    }

    static class Pair {
        public Pair(double min, double max) {
            this.min = min;
            this.max = max;
        }
        final double min;
        final double max;

        @Override
        public String toString() {
            return String.format("[%.2f, %.2f]", min, max);
        }
    }

    static class Point {
        final double x;
        final double y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    static class MatrixResultItem {

        int l, i;
        double a, r, b, ci, s1, s2, s3, cnpv;

        public MatrixResultItem(int l, int i, double a, double r, double b, double ci, double s1, double s2, double s3, double cnpv) {
            this.l = l;
            this.i = i;
            this.a = a;
            this.r = r;
            this.b = b;
            this.ci = ci;
            this.s1 = s1;
            this.s2 = s2;
            this.s3 = s3;
            this.cnpv = cnpv;
        }

        @Override
        public String toString() {
            return String.format("%d | %d | %.2f | %.2f | %.2f | %.2f | %.2f | %.2f | %.2f | %.2f", l, i, a, r, b, ci, s1, s2, s3, cnpv);
        }
    }

    static class MatrixResultItem2 {
        Double alpha;
        Pair r, b, pair, ci1, ci2, ci3, npv;
        public MatrixResultItem2(Double alpha, Pair r, Pair b, Pair pair, Pair ci1, Pair ci2, Pair ci3, Pair npv) {
            this.alpha = alpha;
            this.r = r;
            this.b = b;
            this.pair = pair;
            this.ci1 = ci1;
            this.ci2 = ci2;
            this.ci3 = ci3;
            this.npv = npv;
        }

        @Override
        public String toString() {
            return String.format("%.2f  %s  %s  %s  %s  %s  %s  %s", alpha, r, b, pair, ci1, ci2, ci3, npv);
        }
    }
}
