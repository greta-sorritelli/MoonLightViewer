package javaFX.GraphControllers;

import App.javaController.GraphController;
import App.javaController.GraphType;
import App.javaModel.Graph.SimpleTimeGraph;
import App.javaModel.Graph.TimeGraph;
import App.javaModel.utility.DialogUtility.DialogBuilder;
import App.javaModel.utility.MouseUtility.SimpleMouseManager;
import javaFX.JavaFXChartController;
import javaFX.MainController;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.javafx.FxGraphRenderer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for graphs
 */
public class JavaFXGraphController {

    @FXML
    Label graphType;
    @FXML
    BorderPane borderPane;
    @FXML
    Label infoNode;
    @FXML
    JavaFXNodesTableController nodeTableComponentController;
    @FXML
    JavaFXFiltersController filtersComponentController;
    @FXML
    Slider slider;
    @FXML
    SubScene scene;

    private List<TimeGraph> graphList = new ArrayList<>();
    private Graph currentGraph;
    private String theme;
    private JavaFXChartController chartController;
    private GraphController graphController;
    private MainController mainController;
    private boolean csvRead = false;
    private final ArrayList<FxViewer> viewers = new ArrayList<>();
    private final Label label = new Label();
    private final ArrayList<Double> time = new ArrayList<>();

     public boolean getCsvRead() {
        return this.csvRead;
    }

    public List<TimeGraph> getGraphList() {
        return graphList;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public void injectMainController(MainController mainController, JavaFXChartController chartComponentController) {
        this.mainController = mainController;
        this.chartController = chartComponentController;
        initialize();
    }

    private void initialize() {
        this.graphController = GraphController.getInstance();
        this.nodeTableComponentController.injectGraphController(graphController);
        this.filtersComponentController.injectGraphController(mainController, this, chartController);
    }

    /**
     * Open the explorer to choose a file
     *
     * @param extensions extension of a file to choose
     *
     * @return the file chosen
     */
    private File open(String description, String extensions) {
        FileChooser fileChooser = new FileChooser();
        Stage stage = (Stage) mainController.getRoot().getScene().getWindow();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(description, extensions);
        fileChooser.getExtensionFilters().add(extFilter);
        return fileChooser.showOpenDialog(stage);
    }

    /**
     * Opens explorer with only .tra files
     */
    public void openTraExplorer() {
        System.setProperty("org.graphstream.ui", "javafx");
        File file = open("TRA Files", "*.tra");
        if (file != null) {
            resetAll();
            createGraphFromFile(file);
            nodeTableComponentController.initTable();
        } else {
            DialogBuilder d = new DialogBuilder(mainController.getTheme());
            d.info("No file chosen.");
        }

    }

    /**
     * Opens explorer with only .csv files
     */
    public void openCSVExplorer() {
        chartController.reset();
        File file = open("CSV Files", "*.csv");
        if (file != null) {
            try {
                readCSV(file);
                chartController.createDataFromGraphs(graphList);
            } catch (Exception e) {
                DialogBuilder d = new DialogBuilder(mainController.getTheme());
                d.error("Failed to load chart data.");
            }
        } else {
            DialogBuilder d = new DialogBuilder(mainController.getTheme());
            d.info("No file chosen.");
        }
    }

    /**
     * Reset all lists and info
     */
    private void resetAll() {
        viewers.clear();
        graphList.clear();
        time.clear();
        nodeTableComponentController.resetTable();
        filtersComponentController.resetFiltersNewFile();
        chartController.reset();
        slider.setMax(50);
        csvRead = false;
        infoNode.setText(" ");
    }

    /**
     * Read a .csv file to get info about nodes
     *
     * @param file .csv file
     */
    private void readCSV(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        graphController.setGraphList(graphList);
        while (((line = br.readLine()) != null)) {
            createNodesVector(line);
        }
        graphList = graphController.getGraphList();
        this.csvRead = true;
    }

    /**
     * Creates vectors for every node in a single instant
     *
     * @param line a string of a time instant with all info about nodes
     */
    private void createNodesVector(String line) {
        graphController.createNodesVector(line);
    }

    /**
     * Create a graph from a file
     *
     * @param file file to read
     */
    private void createGraphFromFile(File file) {
        try {
            graphController.setGraphList(graphList);
            GraphType t = graphController.createGraphFromFile(file);
            graphList = graphController.getGraphList();
            for (TimeGraph g : graphList) {
                g.getGraph().setAttribute("ui.stylesheet", this.theme);
            }
            if (t.equals(GraphType.STATIC))
                changeView(graphController.getStaticGraph(), 0.0);
            else if (t.equals(GraphType.DYNAMIC)) {
                createViews();
                createTimeSlider();
                changeGraphView(String.valueOf(graphList.get(0).getTime()));
            }
        } catch (Exception e) {
            DialogBuilder dialogBuilder = new DialogBuilder(mainController.getTheme());
            dialogBuilder.error("Failed to generate graph.");
        }
    }

    private void createTimeSlider() {
        time.clear();
        for (TimeGraph t : graphList) {
            time.add(t.getTime());
        }
        slider.setMin(time.get(0));
        slider.setMax(time.size() - 1);
        slider.setValue(time.get(0));
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
        slider.setLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Double object) {
                int index = object.intValue();
                return String.valueOf(time.get(index));
            }

            @Override
            public Double fromString(String string) {
                return Double.parseDouble(string);
            }
        });
        addListenersToSlider();
    }

    private void addListenersToSlider() {
        slider.applyCss();
        slider.layout();
        Pane thumb = (Pane) slider.lookup(".thumb");
        if (!thumb.getChildren().contains(label)) {
            thumb.getChildren().add(label);
            label.setTextAlignment(TextAlignment.CENTER);
            thumb.setPrefHeight(20);
        }
        slider.valueProperty().addListener((obs, oldValue, newValue) -> {
            label.setText(String.valueOf(this.time.get(newValue.intValue())));
            changeGraphView(String.valueOf(this.time.get(newValue.intValue())));
        });
        slider.setOnMousePressed(event -> borderPane.getScene().setCursor(Cursor.CLOSED_HAND));
        slider.setOnMouseReleased(event -> borderPane.getScene().setCursor(Cursor.DEFAULT));
    }


    /**
     * Changes visualization of a dynamic graph in time
     *
     * @param time instant chosen
     */
    private void changeGraphView(String time) {
        Optional<TimeGraph> g = graphList.stream().filter(timeGraph -> timeGraph.getTime() == Double.parseDouble(time)).findFirst();
        g.ifPresent(timeGraph -> changeView(timeGraph.getGraph(), Double.parseDouble(time)));
    }

    /**
     * @return current graph displayed
     */
    public Graph getCurrentGraph() {
        return currentGraph;
    }

    /**
     * Creates a viewer for each graph
     */
    private void createViews() {
        for (TimeGraph t : graphList) {
            FxViewer viewer = new FxViewer(t.getGraph(), FxViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
            viewer.addView(String.valueOf(t.getTime()), new FxGraphRenderer());
            viewers.add(viewer);
        }
    }

    /**
     * Change viewer in order to display a different graph
     *
     * @param graph graph to show
     * @param time  instant related to the graph
     */
    private void changeView(Graph graph, Double time) {
        setGraphAttribute(graph, time);
        Optional<FxViewer> fv = viewers.stream().filter(fxViewer -> fxViewer.getView(String.valueOf(time)) != null).findFirst();
        if (fv.isPresent()) {
            FxViewer v = fv.get();
            if (this.csvRead)
                v.disableAutoLayout();
            else v.enableAutoLayout();
            FxViewPanel panel = (FxViewPanel) v.getView(String.valueOf(time));
            scene.setRoot(panel);
            borderPane.setCenter(scene);
            scene.setVisible(true);
            scene.heightProperty().bind(borderPane.heightProperty());
            scene.widthProperty().bind(borderPane.widthProperty());
            SimpleMouseManager sm = new SimpleMouseManager(graph, time, chartController);
            sm.addPropertyChangeListener(evt -> {
                if (evt.getPropertyName().equals("LabelProperty")) {
                    infoNode.setText(evt.getNewValue().toString());
                }
            });
            v.getView(String.valueOf(time)).setMouseManager(sm);
        }
    }

    /**
     * Sets attributes to the graph displayed
     */
    private void setGraphAttribute(Graph graph, Double time) {
        Optional<TimeGraph> g = graphList.stream().filter(timeGraph -> timeGraph.getTime() == time).findFirst();
        g.ifPresent(timeGraph -> currentGraph = g.get().getGraph());
        if (graph.hasAttribute("ui.stylesheet"))
            graph.removeAttribute("ui.stylesheet");
        graph.setAttribute("ui.stylesheet", this.theme);
    }

    @FXML
    private void deselectFiltersTable() {
        filtersComponentController.tableFilters.getSelectionModel().clearSelection();
    }

    @FXML
    private void deselectNodeTable() {
        nodeTableComponentController.nodesTable.getSelectionModel().clearSelection();
    }
}