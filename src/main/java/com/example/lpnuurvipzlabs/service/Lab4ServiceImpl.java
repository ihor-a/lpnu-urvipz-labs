package com.example.lpnuurvipzlabs.service;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.IntStream;

import static java.lang.Math.abs;
import static java.lang.Math.random;

public class Lab4ServiceImpl extends TextResultBase implements Lab4Service {

    private final ScrollPane resourcePane1, resourcePane2, resourcePane3, resourcePane4;
    private final ScrollPane expertPane1, expertPane2, costsPane2, resultPane1, resultPane2, planRiskPane;
    private final TableView<ExpertItem> expertTableView1 = new TableView<>();
    private final TableView<ExpertItem> expertTableView2 = new TableView<>();
    private final TableView<CostItem> costTableView = new TableView<>();
    private final TableView<ResourceItem> techTableView = new TableView<>();
    private final TableView<ResourceItem> costRiskTableView = new TableView<>();
    private final TableView<ResourceItem> planTableView = new TableView<>();
    private final TableView<ResourceItem> implementTableView = new TableView<>();
    private final TableView<ProbabilityResultItem> probabilityResultTableView = new TableView<>();
    private final TableView<CostResultItem> costResultTableView = new TableView<>();
    private final TableView<MeasureItem> measureItemTableView = new TableView<>();
    private final ObservableList<ExpertItem> expertObservableList1 = FXCollections.observableArrayList();
    private final ObservableList<ExpertItem> expertObservableList2 = FXCollections.observableArrayList();
    private final ObservableList<CostItem> costObservableList = FXCollections.observableArrayList();
    private final ObservableList<ResourceItem> techObservableList = FXCollections.observableArrayList();
    private final ObservableList<ResourceItem> costRiskObservableList = FXCollections.observableArrayList();
    private final ObservableList<ResourceItem> planObservableList = FXCollections.observableArrayList();
    private final ObservableList<ResourceItem> implementObservableList = FXCollections.observableArrayList();
    private final ObservableList<ProbabilityResultItem> probabilityResultObservableList = FXCollections.observableArrayList();
    private final ObservableList<CostResultItem> costResultObservableList = FXCollections.observableArrayList();
    private final ObservableList<MeasureItem> measureObservableList = FXCollections.observableArrayList();

    private final ProbabilityExpert probabilityExpert;
    private final CostExpert costExpert;
    private final CostResource costResource;
    private final TechResource techResource;
    private final CostRiskResource costRiskResource;
    private final PlanResource planResource;
    private final ImplementResource implementResource;

    public Lab4ServiceImpl(ScrollPane resourcePane1, ScrollPane resourcePane2, ScrollPane resourcePane3, ScrollPane resourcePane4,
                           ScrollPane expertPane1, ScrollPane expertPane2, ScrollPane costsPane2,
                           ScrollPane resultPane1, ScrollPane resultPane2, ScrollPane planRiskPane) {
        this.resourcePane1 = resourcePane1;
        this.resourcePane2 = resourcePane2;
        this.resourcePane3 = resourcePane3;
        this.resourcePane4 = resourcePane4;
        this.expertPane1 = expertPane1;
        this.expertPane2 = expertPane2;
        this.costsPane2 = costsPane2;
        this.resultPane1 = resultPane1;
        this.resultPane2 = resultPane2;
        this.planRiskPane = planRiskPane;

        probabilityExpert = new ProbabilityExpert(expertObservableList1, expertTableView1);
        costExpert = new CostExpert(expertObservableList2, expertTableView2);
        costResource = new CostResource(costObservableList, costTableView);
        techResource = new TechResource(techObservableList, techTableView);
        costRiskResource = new CostRiskResource(costRiskObservableList, costRiskTableView);
        planResource = new PlanResource(planObservableList, planTableView);
        implementResource = new ImplementResource(implementObservableList, implementTableView);
        initTables();
    }

    private void initTables() {
        initExpertTable(expertTableView1, expertPane1, expertObservableList1);
        initExpertTable(expertTableView2, expertPane2, expertObservableList2);
        initCostTable(costTableView, costsPane2, costObservableList);
        initResourceTable(techResource.getTitle(), techTableView, resourcePane1, techObservableList);
        initResourceTable(costRiskResource.getTitle(), costRiskTableView, resourcePane2, costRiskObservableList);
        initResourceTable(planResource.getTitle(), planTableView, resourcePane3, planObservableList);
        initResourceTable(implementResource.getTitle(), implementTableView, resourcePane4, implementObservableList);
        initProbabilityResultTable(probabilityResultTableView, resultPane1, probabilityResultObservableList);
        initCostResultTable(costResultTableView, resultPane2, costResultObservableList);
        initMeasureTable(measureItemTableView, planRiskPane, measureObservableList);
    }

    @Override
    public String calculate() {
        initTables();
        resetResult();

        // Probability block
        probabilityResultObservableList.clear();
        for (var resource: Arrays.asList(techResource, costRiskResource, planResource, implementResource)) {
            calcProbability(resource, probabilityExpert);
        }
        probabilityResultTableView.refresh();

        // Costs block
        costResultObservableList.clear();
        double minAddCost = Double.MAX_VALUE, maxAddCost = Double.MIN_VALUE;
        double[] minMax;

        for (var resource: Arrays.asList(techResource, costRiskResource, planResource, implementResource)) {
            minMax = calcCosts(resource, costExpert);
            minAddCost = Math.min(minAddCost, minMax[0]);
            maxAddCost = Math.max(maxAddCost, minMax[1]);
        }

        var mpr = (maxAddCost - minAddCost) / 3;
        List<double[]> costsList = new ArrayList<>();
        costsList.add(new double[]{minAddCost, minAddCost+mpr});
        costsList.add(new double[]{costsList.get(0)[1], costsList.get(0)[1]+mpr});
        costsList.add(new double[]{costsList.get(1)[1], costsList.get(1)[1]+mpr});

        for (var item: costResultObservableList) {
            if (item.name == null) {
                continue;
            }
            item.level = defCostLevel(item.addCost, costsList);
        }
        costResultTableView.refresh();

        // Measures block
        calcMeasure(costResultObservableList);

        return getResult();
    }

    private double[] splitRandomly(double source, int n) {
        if (n < 1) {
            return new double[1];
        }
        double[] result = new double[n];

        Random rand = new Random();
        var steps = IntStream.range(0, n-1).mapToDouble(operand -> rand.nextDouble(source)).sorted().toArray();

        for (int i = 0; i < n-1; i++) {
            result[i] = steps[i] - (i-1 >= 0 ? steps[i-1] : 0);
        }
        result[n-1] = source - Arrays.stream(result).sum();

        return result;
    }

    private void calcProbability(BaseResource resource, BaseExpert expert) {
        List<ProbabilityResultItem> resultList = new ArrayList<>();

        var resourceRandoms = splitRandomly(1d, (int)resource.observableList.stream().filter(ResourceItem::isEnabled).count());
        int row = 0;

        for (var resourceItem: resource.observableList) {
            if (!resourceItem.isEnabled()) {
                continue;
            }

            ProbabilityResultItem probabilityResultItem = new ProbabilityResultItem(resourceItem.getOrigName());
            for (int i = 0; i< probabilityResultItem.randoms.length; i++) {
                probabilityResultItem.randoms[i] = resourceRandoms[row] + random() * expert.makeRandom();
                probabilityResultItem.estimates[i] = probabilityResultItem.randoms[i] *
                        expert.getValue(resource.resourceIndex, i);
            }
            probabilityResultItem.sum = Arrays.stream(probabilityResultItem.randoms).sum() / probabilityResultItem.randoms.length;
            probabilityResultItem.result = Arrays.stream(probabilityResultItem.estimates).reduce(0, Double::sum) /
                    expert.getSumValue(resource.resourceIndex);
            probabilityResultItem.level = defProbabilityLevel(probabilityResultItem.result);

            resultList.add(probabilityResultItem);
            row++;
        }

        // Resource row
        ProbabilityResultItem probabilityResultItem = new ProbabilityResultItem(resource.getTitle());
        for (int i = 0; i< probabilityResultItem.randoms.length; i++) {
            probabilityResultItem.randoms[i] = expert.getValue(resource.resourceIndex, i);
            int finalI = i;
            probabilityResultItem.estimates[i] = resultList.stream().map(item -> item.estimates[finalI]).reduce(0d, Double::sum) /
                    resultList.size() / probabilityResultItem.randoms[i];
        }
        probabilityResultItem.sum = expert.getSumValue(resource.resourceIndex);
        probabilityResultItem.result = resultList.stream().map(item -> item.result).reduce(0d, Double::sum) /
                resultList.size();
        probabilityResultItem.level = defProbabilityLevel(probabilityResultItem.result);

        resultList.add(0, probabilityResultItem);

        // Add empty line
        if (resource.resourceIndex > 0) {
            resultList.add(0, new ProbabilityResultItem(null));
        }

        probabilityResultObservableList.addAll(resultList);
    }
    private double[] calcCosts(BaseResource resource, BaseExpert expert) {
        List<CostResultItem> resultList = new ArrayList<>();
        var minMax = new double[2];

        var resourceRandoms = splitRandomly(1d, (int)resource.observableList.stream().filter(ResourceItem::isEnabled).count());
        int row = 0;

        for (var resourceItem: resource.observableList) {
            if (!resourceItem.isEnabled()) {
                continue;
            }

            CostResultItem costResultItem = new CostResultItem(resourceItem.getOrigName());
            for (int i = 0; i< costResultItem.randoms.length; i++) {
                costResultItem.randoms[i] = resourceRandoms[row] + random() * expert.makeRandom();
                costResultItem.estimates[i] = costResultItem.randoms[i] *
                        expert.getValue(resource.resourceIndex, i);
            }
            costResultItem.startCost = costResource.getValue(resource.resourceIndex) * resourceRandoms[row];
            costResultItem.sum = Arrays.stream(costResultItem.randoms).sum() / costResultItem.randoms.length *
                    costResultItem.startCost;
            costResultItem.addCost = Arrays.stream(costResultItem.estimates).reduce(0, Double::sum) /
                    expert.getSumValue(resource.resourceIndex) * costResultItem.startCost;
            costResultItem.finalCost = costResultItem.startCost + costResultItem.addCost;

            resultList.add(costResultItem);
            row++;
        }
        minMax[0] = resultList.stream().mapToDouble(item -> item.addCost).min().orElse(0);
        minMax[1] = resultList.stream().mapToDouble(item -> item.addCost).max().orElse(0);

        // Resource row
        CostResultItem costResultItem = new CostResultItem(resource.getTitle());
        for (int i = 0; i< costResultItem.randoms.length; i++) {
            costResultItem.randoms[i] = expert.getValue(resource.resourceIndex, i);
            int finalI = i;
            costResultItem.estimates[i] = resultList.stream().map(item -> item.estimates[finalI]).reduce(0d, Double::sum) /
                    resultList.size() / costResultItem.randoms[i];
        }
        costResultItem.sum = expert.getSumValue(resource.resourceIndex);
        costResultItem.startCost = costResource.getValue(resource.resourceIndex);
        costResultItem.addCost = resultList.stream().map(item -> item.addCost).reduce(0d, Double::sum);
        costResultItem.finalCost = costResultItem.startCost + costResultItem.addCost;

        resultList.add(0, costResultItem);

        // Add empty line
        if (resource.resourceIndex > 0) {
            resultList.add(0, new CostResultItem(null));
        }
        costResultObservableList.addAll(resultList);

        return minMax;
    }

    private void calcMeasure(ObservableList<CostResultItem> resultList) {
        measureObservableList.clear();
        for (var resultItem: resultList) {
            if (Arrays.asList(BaseResource.resourceTitles).contains(resultItem.getName())) {
                continue;
            }
            var item = new MeasureItem(resultItem.getName());
            item.startCost = resultItem.startCost;
            item.addCost = resultItem.addCost;
            measureObservableList.add(item);
        }

        refreshMeasure();
    }
    public void refreshMeasure() {
        for (var measureItem: measureObservableList) {
            measureItem.finalAddCost = measureItem.addCost * randomMinMax(0.75, 0.99);
            measureItem.finalCost = measureItem.startCost + measureItem.finalAddCost;
        }
        measureItemTableView.refresh();
    }

    private double randomMinMax(double min, double max) {
        var random = new Random();
        return min + (max - min) * random.nextDouble();
    }

    private String defProbabilityLevel(double result) {
        String level = "ДВ";

        if (result < 0.1) {
            level = "ДН";
        } else if (result >= 0.1 && result < 0.25) {
            level = "Н";
        } else if (result >= 0.25 && result < 0.5) {
            level = "С";
        } else if (result >= 0.5 && result < 0.75) {
            level = "В";
        }
        return level;
    }

    private String defCostLevel(double addCost, List<double[]> costsList) {
        String level = "ВзМ";

        if (addCost >= costsList.get(0)[0] && addCost < costsList.get(0)[1]) {
            level = "Н";
        } else if (addCost >= costsList.get(1)[0] && addCost < costsList.get(1)[1]) {
            level = "С";
        } else if (addCost >= costsList.get(2)[0] && addCost <= costsList.get(2)[1]) {
            level = "В";
        }
        return level;
    }

    private class CostResource {
        protected ObservableList<CostItem> observableList;
        protected TableView<CostItem> tableView;

        public CostResource(ObservableList<CostItem> observableList, TableView<CostItem> tableView) {
            this.observableList = observableList;
            this.tableView = tableView;

            this.observableList.add(new CostItem(new double[]{430, 270, 370, 460}));
        }

        double getValue(int resourceIndex) {
            return observableList.get(0).values[resourceIndex];
        }
    }

    private class TechResource extends BaseResource {
        public TechResource(ObservableList<ResourceItem> observableListExt, TableView<ResourceItem> tableView) {
            super(observableListExt, tableView);
            resourceIndex = 0;

            Integer number = 1;
            observableList.add(new ResourceItem("затримки у постачанні обладнання, необхідного\nдля підтримки процесу розроблення ПЗ", number++));
            observableList.add(new ResourceItem("затримки у постачанні інструментальних засобів,\nнеобхідних для процесу розроблення ПЗ", number++));
            observableList.add(new ResourceItem("небажання команди виконавців використовувати\nінструментальні засоби для розроблення ПЗ", number++));
            observableList.add(new ResourceItem("формування запитів на більш потужні інструментальні\nзасоби розроблення ПЗ", number++));
            observableList.add(new ResourceItem("відмова команди виконавців від CASE-засобів\nрозроблення ПЗ", number++));
            observableList.add(new ResourceItem("неефективність програмного коду, згенерованого\nCASE-засобами розроблення ПЗ", number++));
            observableList.add(new ResourceItem("неможливість інтеграції CASE-засобів з іншими інструментальними\nзасобами для підтримки розроблення ПЗ", number++));
            observableList.add(new ResourceItem("недостатня продуктивність баз(и) даних \nдля підтримки процесу розроблення ПЗ", number++));
            observableList.add(new ResourceItem("прогр. компоненти, які використовують повторно в ПЗ,\nмають дефекти та обмежені функ. можливості", number++));
            observableList.add(new ResourceItem("швидкість виявлення дефектів у програмному коді\nє нижчою від раніше запланованих термінів", number++));
            observableList.add(new ResourceItem("поява дефектних системних компонент,\nякі використовують для розроблення ПЗ", number++));
        }
    }
    private class CostRiskResource extends BaseResource {
        public CostRiskResource(ObservableList<ResourceItem> observableListExt, TableView<ResourceItem> tableView) {
            super(observableListExt, tableView);
            resourceIndex = 1;

            Integer number = 1;
            observableList.add(new ResourceItem("недо(пере)оцінювання витрат на реалізацію\nпрограмного проекту (надмірно низька вартість)", number++));
            observableList.add(new ResourceItem("фінансові ускладнення у компанії-замовника ПЗ", number++));
            observableList.add(new ResourceItem("фінансові ускладнення у компанії-розробника ПЗ", number++));
            observableList.add(new ResourceItem("змен(збіль)шення бюджету програмного проекта з\nініціативи компанії-замовника ПЗ під час його реалізації", number++));
            observableList.add(new ResourceItem("висока вартість виконання повторних робіт,\nнеобхідних для зміни вимог до ПЗ", number++));
            observableList.add(new ResourceItem("реорганізація структурних підрозділів\nу компанії-замовника ПЗ", number++));
            observableList.add(new ResourceItem("реорганізація команди виконавців\nу компанії-розробника ПЗ", number++));
        }
    }
    private class PlanResource extends BaseResource {
        public PlanResource(ObservableList<ResourceItem> observableListExt, TableView<ResourceItem> tableView) {
            super(observableListExt, tableView);
            resourceIndex = 2;

            Integer number = 1;
            observableList.add(new ResourceItem("зміни графіка виконання робіт\nз боку замовника чи розробника ПЗ", number++));
            observableList.add(new ResourceItem("порушення графіка виконання робіт\nз боку компанії-розробника ПЗ", number++));
            observableList.add(new ResourceItem("потреба зміни користувацьких вимог\nдо ПЗ з боку компанії-замовника ПЗ", number++));
            observableList.add(new ResourceItem("потреба зміни функціональних вимог\nдо ПЗ з боку компанії-розробника ПЗ", number++));
            observableList.add(new ResourceItem("потреба виконання великої кількості повторних\nробіт, необхідних для зміни вимог до ПЗ", number++));
            observableList.add(new ResourceItem("недо(пере)оцінювання тривалості етапів реалізації\nпрограмного проекту з боку компанії-замовника ПЗ", number++));
            observableList.add(new ResourceItem("остаточний розмір ПЗ значно перевищує (менший від)\nзаплановані(их) його характеристики", number++));
            observableList.add(new ResourceItem("поява на ринку аналогічного ПЗ\nдо виходу замовленого", number++));
            observableList.add(new ResourceItem("поява на ринку більш конкурентоздатного ПЗ", number++));

        }
    }
    private class ImplementResource extends BaseResource {
        public ImplementResource(ObservableList<ResourceItem> observableListExt, TableView<ResourceItem> tableView) {
            super(observableListExt, tableView);
            resourceIndex = 3;

            Integer number = 1;
            observableList.add(new ResourceItem("низький моральний стан персоналу команди виконавців ПЗ", number++));
            observableList.add(new ResourceItem("низька взаємодія між членами команди виконавців ПЗ", number++));
            observableList.add(new ResourceItem("пасивність керівника (менеджера) програмного проекту", number++));
            observableList.add(new ResourceItem("недостатня компетентність керівника\n(менеджера) програмного проекту", number++));
            observableList.add(new ResourceItem("незадоволеність замовника результатами\nетапів реалізації програмного проекту", number++));
            observableList.add(new ResourceItem("недостатня кількість фахівців у команді\nвиконавців ПЗ з необхідним професійним рівнем", number++));
            observableList.add(new ResourceItem("хвороба провідного виконавця в найкритичніший\nмомент розроблення ПЗ", number++));
            observableList.add(new ResourceItem("одночасна хвороба декількох виконавців\nпідчас розроблення ПЗ", number++));
            observableList.add(new ResourceItem("неможливість організації необхідного\nнавчання персоналу команди виконавців ПЗ", number++));
            observableList.add(new ResourceItem("зміна пріоритетів у процесі управління\nпрограмним проектом", number++));
            observableList.add(new ResourceItem("недо(пере)оцінювання необхідної кількості розробників (підрядників\nі субпідрядників) на етапах життєвого циклу розроблення ПЗ", number++));
            observableList.add(new ResourceItem("недостатнє (надмірне) документування результатів\nна етапах реалізації програмного проекту", number++));
            observableList.add(new ResourceItem("нереалістичне прогнозування результатів\nна етапах реалізації програмного проекту", number++));
            observableList.add(new ResourceItem("недостатній професійний рівень представників\nвід компанії-замовника ПЗ", number++));
        }
    }

    protected abstract class BaseResource {
        static final String[] resourceTitles = new String[]{
                "Множина настання технічних ризикових подій",
                "Множина настання вартісних ризикових подій",
                "Множина настання планових ризикових подій",
                "Множина настання ризикових подій реалізації\nпроцесу управління програмним проектом"
        };
        protected ObservableList<ResourceItem> observableList;
        protected TableView<ResourceItem> tableView;
        protected int resourceIndex = -1; // undefined

        public BaseResource(ObservableList<ResourceItem> observableList, TableView<ResourceItem> tableView) {
            this.observableList = observableList;
            this.tableView = tableView;
        }

        String getTitle() {
            return resourceIndex >=0 ? resourceTitles[resourceIndex] : "resourceIndex undefined";
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

    private class CostExpert extends BaseExpert {
        public CostExpert(ObservableList<ExpertItem> observableList, TableView<ExpertItem> tableView) {
            super(observableList, tableView);
            randomSeed = 0.2;
            randomMultiplier = 0.5;

            observableList.add(new ExpertItem("Технічних ризикових подій", 11, new Integer[]{7, 7, 10, 10, 10, 10, 10, 9, 8, 6}));
            observableList.add(new ExpertItem("Вартісних ризикових подій", 7, new Integer[]{10, 10, 10, 10, 8, 10, 8, 8, 8, 9}));
            observableList.add(new ExpertItem("Планових ризикових подій", 9, new Integer[]{7, 8, 7, 8, 8, 7, 9, 7, 10, 8}));
            observableList.add(new ExpertItem("Ризикових події реалізації процесу управління", 14, new Integer[]{9, 7, 9, 7, 7, 9, 10, 10, 10, 10}));
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

        double getValue(int resourceIndex, int expertIndex) {
            return observableList.get(resourceIndex).values[expertIndex];
        }
        int getSumValue(int resourceIndex) {
            return Arrays.stream(observableList.get(resourceIndex).values).reduce(0, Integer::sum);
        }
    }

    public class ProbabilityResultItem extends BaseResultItem {
        public ProbabilityResultItem(String name) {
            super(name);
        }

        double result;

        public String getResult() {
            return getNumeric(result);
        }
    }
    public class CostResultItem extends BaseResultItem {
        public CostResultItem(String name) {
            super(name);
        }

        double startCost, addCost, finalCost;

        public String getStartCost() {
            return getNumeric(startCost);
        }

        public String getAddCost() {
            return getNumeric(addCost);
        }

        public String getFinalCost() {
            return getNumeric(finalCost);
        }
    }

    protected abstract class BaseNumericItem {
        protected String name;
        protected String getNumeric(Double val) {
            // empty row
            if (name == null) {
                return "";
            }

            if (abs(val - (double)val.intValue()) != 0) {
                return  new DecimalFormat("#.###").format(val);
            } else {
                return Integer.toString(val.intValue());
            }
        }
    }
    protected abstract class BaseResultItem extends BaseNumericItem {
        final private int OFFSET = 1;
        private final int AM_ELEMENTS = 10;
        protected double[] randoms = new double[AM_ELEMENTS];
        protected double[] estimates = new double[AM_ELEMENTS];
        protected double sum;
        String level = "";
        int rowType = 0; // for row bg color. default uncolored

        public BaseResultItem(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public String getSum() {
            return getNumeric(sum);
        }

        public String getLevel() {
            return level;
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

        public String getOrigName() {
            return name;
        }
        public String getName() {
            return name.replaceAll("\n", " ");
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
    public class MeasureItem extends BaseNumericItem {
        String measure, action;
        double startCost, addCost, finalAddCost, finalCost;

        static String[] comboMeasures = {
                "1-попереднє навчання членів проектного колективу",
                "2-узгодження детального переліку вимог до ПЗ із замовником",
                "3-внесення узгодженого переліку вимог до ПЗ замовника в договір",
                "4-точне слідування вимогам замовника\nз узгодженого переліку вимог до ПЗ",
                "5-попередні дослідження ринку",
                "6-експертна оцінка програмного проекту\nдосвідченим стороннім консультантом",
                "7-консультації досвідченого стороннього консультанта",
                "8-тренінг з вивчення необхідних інструментів розроблення ПЗ",
                "9-укладання договору страхування",
                "10-використання \"шаблонних\" рішень з вдалих попередніх\nпроектів при управлінні програмним проектом",
                "11-підготовка документів, які показують важливість даного проекту\nдля досягнення фінансових цілей компанії-розробника",
                "12-реорганізація роботи проектного колективу так, щоб обов'язки\nта робота членів колективу перекривали один одного",
                "13-придбання (замовлення) частини компонент розроблюваного ПЗ",
                "14-заміна потенційно дефектних компонент розроблюваного ПЗ придбаними\nкомпонентами, які гарантують якість виконання роботи",
                "15-придбання більш продуктивної бази даних",
                "16-використання генератора програмного коду",
                "17-реорганізація роботи проектного колективу залежно від рівня\nтруднощів виконання завдань та професійних рівнів розробників",
                "18-повторне використання придатних компонент ПЗ, які були\nрозроблені для інших програмних проектів",
                "19-аналіз доцільності розроблення даного ПЗ",
        };
        static String[] comboActions = {"Пом'якшення", "Прийняття", "Ухилення", "Передача"};

        public MeasureItem(String name) {
            this.name = name;
            measure = comboMeasures[(int) randomMinMax(0,18)];
            action = comboActions[(int) randomMinMax(0,3)];
        }
        public String getName() {
            return name;
        }

        public String getStartCost() {
            return getNumeric(startCost);
        }

        public String getAddCost() {
            return getNumeric(addCost);
        }

        public String getFinalAddCost() {
            return getNumeric(finalAddCost);
        }

        public String getFinalCost() {
            return getNumeric(finalCost);
        }

        public String getMeasure() {
            return name == null || name.equals("") ? "" : measure;
        }
        public String getAction() {
            return name == null || name.equals("") ? "" : action;
        }
        @Override
        public String toString() {
            return "name='" + name + '\'' + ", measure=" + measure + '}';
        }
    }

    public class CostItem extends BaseNumericItem {
        private final int OFFSET=1;
        double[] values;

        public CostItem(double[] values) {
            this.values = values;
            this.name = "";
        }

        public void setValue(int number, String oldValue, String newValue) {
            if (oldValue.equals(newValue)) {
                return;
            }
            if (newValue.matches("^[\\d\\.]+$")) {
                var value  = Double.parseDouble(newValue);
                if (value >= 0) {
                    this.values[number] = value;
                }
            }
        }

        private String getValue(int number) {
            return getNumeric(values[number-OFFSET]);
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
        public String getSum() {
            return getNumeric(Arrays.stream(values).sum());
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
            tableView.setPrefWidth(scrollPane.getWidth() - 20);
            tableView.getColumns().get(1).setPrefWidth(scrollPane.getWidth() * 0.85);
            return;
        }
        
        tableView.setEditable(true);
        TableColumn<ResourceItem, String> tableColumn = new TableColumn<>("N");
        tableColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        tableView.getColumns().add(tableColumn);

        tableColumn = new TableColumn<>(title);
        tableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableView.getColumns().add(tableColumn);

        TableColumn<ResourceItem, Boolean> tableColumnBoolean = new TableColumn<>("Y/N");
        tableColumnBoolean.setCellValueFactory(param -> param.getValue().enabledProperty());
        tableColumnBoolean.setCellFactory(CheckBoxTableCell.forTableColumn(tableColumnBoolean));
        tableView.getColumns().add(tableColumnBoolean);

        tableView.setItems(observableList);
        scrollPane.setContent(tableView);
    }
    private void initMeasureTable(TableView<MeasureItem> tableView, ScrollPane scrollPane, ObservableList<MeasureItem> observableList) {
        // Table already initialized
        if (tableView.getColumns().size() > 0) {
            tableView.setPrefWidth(scrollPane.getWidth()-20);
            tableView.setPrefHeight(scrollPane.getHeight()-20);
            tableView.getColumns().get(0).setPrefWidth(scrollPane.getWidth() * 0.3);
            tableView.getColumns().get(1).setPrefWidth(scrollPane.getWidth() * 0.25);
            tableView.getColumns().get(2).setPrefWidth(scrollPane.getWidth() * 0.1);
            return;
        }

        tableView.setEditable(true);

        TableColumn<MeasureItem, String>tableColumn = new TableColumn<>("Множина ризикових подій");
        tableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableView.getColumns().add(tableColumn);

        tableColumn = new TableColumn<>("Назва заходів");
        tableColumn.setCellValueFactory(new PropertyValueFactory<>("measure"));
        tableColumn.setCellFactory(param -> {
            ComboBox<String> comboBox = new ComboBox<>();
            comboBox.getItems().addAll(MeasureItem.comboMeasures);
            TableCell<MeasureItem, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty || item.equals("")) {
                        setGraphic(null);
                    } else {
                        comboBox.setValue(item);
                        setGraphic(comboBox);
                    }
                }
            };
            return cell;
        });
        tableView.getColumns().add(tableColumn);

        tableColumn = new TableColumn<>("Напрямок");
        tableColumn.setCellValueFactory(new PropertyValueFactory<>("action"));
        tableColumn.setCellFactory(param -> {
            ComboBox<String> comboBox = new ComboBox<>();
            comboBox.getItems().addAll(MeasureItem.comboActions);
            TableCell<MeasureItem, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty || item.equals("")) {
                        setGraphic(null);
                    } else {
                        comboBox.setValue(item);
                        setGraphic(comboBox);
                    }
                }
            };
            return cell;
        });
        tableView.getColumns().add(tableColumn);

        //double startCost, addCost, finalCost;
        tableColumn = new TableColumn<>("Початкова\nВартість");
        tableColumn.setCellValueFactory(new PropertyValueFactory<>("startCost"));
        tableView.getColumns().add(tableColumn);

        tableColumn = new TableColumn<>("Початк. рівень\nДодатк. Вартості");
        tableColumn.setCellValueFactory(new PropertyValueFactory<>("addCost"));
        tableColumn.setPrefWidth(100d);
        tableView.getColumns().add(tableColumn);

        tableColumn = new TableColumn<>("Кінцевий рівень\nДодатк. Вартості");
        tableColumn.setCellValueFactory(new PropertyValueFactory<>("finalAddCost"));
        tableColumn.setPrefWidth(100d);
        tableView.getColumns().add(tableColumn);

        tableColumn = new TableColumn<>("Кінцева\nВартість");
        tableColumn.setCellValueFactory(new PropertyValueFactory<>("finalCost"));
        tableView.getColumns().add(tableColumn);

        tableView.setItems(observableList);
        scrollPane.setContent(tableView);
    }

    protected void initProbabilityResultTable(TableView<ProbabilityResultItem> tableView, ScrollPane scrollPane,
                                              ObservableList<ProbabilityResultItem> observableList) {
        List<String[]> customConfig = new ArrayList<>(){{
            add(new String[]{"result", "Ймовір\nність"});
        }};
        initBaseResultTable(tableView, scrollPane, observableList, customConfig);
    }
    protected void initCostResultTable(TableView<CostResultItem> tableView, ScrollPane scrollPane,
                                        ObservableList<CostResultItem> observableList) {
        List<String[]> customConfig = new ArrayList<>(){{
            add(new String[]{"startCost", "Початк.\nВартість"});
            add(new String[]{"addCost", "Додатк.\nВартість"});
            add(new String[]{"finalCost", "Кінцева\nВартість"});
        }};
        initBaseResultTable(tableView, scrollPane, observableList, customConfig);
    }
    protected <T> void initBaseResultTable(TableView<T> tableView, ScrollPane scrollPane,
                                           ObservableList<T> observableList,
                                           List<String[]> customConfig) {
        // Table already initialized
        if (tableView.getColumns().size() > 0) {
            tableView.getColumns().get(0).setPrefWidth(scrollPane.getWidth() * 0.25);
            tableView.setPrefWidth(scrollPane.getWidth()-20);
            tableView.setPrefHeight(scrollPane.getHeight()-20);
            return;
        }

        // Contains bug
//        tableView.setRowFactory(param -> new TableRow<>() {
//            @Override
//            protected void updateItem(T item, boolean empty) {
//                super.updateItem(item, empty);
//
//                var row = (BaseResultItem) item;
//                if (row != null && Arrays.asList(resourceTitles).contains(row.getName())) {
//                    this.setStyle("-fx-background-color: #eeeeff;");
//                    System.out.printf("%s %d%n", row.getName(), getIndex());
//                }
//            }
//        });

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
        columnConfig.add(new String[]{"level", "Рівень"});

        for (String[] column : columnConfig) {
            tableColumn = new TableColumn<>(column[1]);
            tableColumn.setPrefWidth(42d);
            tableColumn.setCellValueFactory(new PropertyValueFactory<>(column[0]));
            switch (column[0]) {
                case "sum" -> {
                    tableColumn.setPrefWidth(55d);
                    tableColumn.setStyle("-fx-background-color: #CCFFFF;");
                }
                case "result", "startCost", "addCost", "finalCost" -> {
                    tableColumn.setPrefWidth(57d);
                    tableColumn.setStyle("-fx-background-color: #CCFFCC;");
                }
                case "level" -> tableColumn.setCellFactory(new Callback<>() {
                    @Override
                    public TableCell<T, String> call(TableColumn<T, String> param) {
                        return new TableCell<>() {
                            @Override
                            protected void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);

                                if (item != null) {
                                    setText(item);
                                    var color = switch (item) {
                                        case "ДН" -> "DDEBF7";
                                        case "Н" -> "BDD7EE";
                                        case "С" -> "FFD966";
                                        case "В" -> "F4B084";
                                        case "" -> null;
                                        default -> "C65911";
                                    };
                                    if (color != null) {
                                        this.setStyle("-fx-background-color: #" + color + ";");
                                    }
                                }
                            }
                        };
                    }
                });
            }
            tableView.getColumns().add(tableColumn);
        }

        tableView.setItems(observableList);
        scrollPane.setContent(tableView);
    }

    private void initExpertTable(TableView<ExpertItem> tableView, ScrollPane scrollPane, ObservableList<ExpertItem> observableList) {
        // Table already initialized
        if (tableView.getColumns().size() > 0) {
            tableView.setPrefWidth(scrollPane.getWidth() - 20);
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

                probabilityResultObservableList.clear();
                costResultObservableList.clear();
                measureObservableList.clear();
            });
            tableView.getColumns().add(tableColumn);
        }

        tableView.setItems(observableList);
        scrollPane.setContent(tableView);
    }
    private void initCostTable(TableView<CostItem> tableView, ScrollPane scrollPane, ObservableList<CostItem> observableList) {
        // Table already initialized
        if (tableView.getColumns().size() > 0) {
            tableView.setPrefWidth(scrollPane.getWidth() - 20);
            return;
        }

        tableView.setEditable(true);
        TableColumn<CostItem, String> tableColumn;
        List<String[]> columnConfig = new ArrayList<>(){{
            add(new String[]{"n1", "Технічні"});
            add(new String[]{"n2", "Вартісні"});
            add(new String[]{"n3", "Планові"});
            add(new String[]{"n4", "Реалізації"});
        }};

        for (String[] column : columnConfig) {
            tableColumn = new TableColumn<>(column[1]);
            tableColumn.setCellValueFactory(new PropertyValueFactory<>(column[0]));
            tableColumn.setPrefWidth(70d);

            tableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            tableColumn.setOnEditCommit(event -> {
                var item = event.getRowValue();
                item.setValue(event.getTablePosition().getColumn(), event.getOldValue(), event.getNewValue());
                event.getTableView().refresh();

                probabilityResultObservableList.clear();
                costResultObservableList.clear();
                measureObservableList.clear();
            });
            tableView.getColumns().add(tableColumn);
        }
        tableColumn = new TableColumn<>("Сума, тис. грн");
        tableColumn.setCellValueFactory(new PropertyValueFactory<>("sum"));
        tableView.getColumns().add(tableColumn);

        tableView.setItems(observableList);
        scrollPane.setContent(tableView);
    }
}
