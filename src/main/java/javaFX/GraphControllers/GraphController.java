package javaFX.GraphControllers;

import App.DialogUtility.DialogBuilder;
import App.GraphUtility.SimpleMouseManager;
import App.GraphUtility.SimpleTimeGraph;
import App.GraphUtility.TimeGraph;
import javaFX.ChartController;
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

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for graphs
 */
public class GraphController {

    @FXML
    Label graphType;
    @FXML
    BorderPane borderPane;
    @FXML
    Label infoNode;
    @FXML
    NodesTableController nodeTableComponentController;
    @FXML
    FiltersController filtersComponentController;
    @FXML
    Slider slider;
    @FXML
    SubScene scene;

    private int idGraph = 0;
    private int totNodes = 0;
    private final List<TimeGraph> graphList = new ArrayList<>();
    private Graph currentGraph;
    private String theme;
    private ChartController chartController;
    private MainController mainController;
    private boolean csvRead = false;
    private final ArrayList<FxViewer> viewers = new ArrayList<>();
    private final Label label = new Label();
    private final ArrayList<Double> time = new ArrayList<>();

    public int getTotNodes() {
        return totNodes;
    }

    public boolean getCsvRead(){ return this.csvRead; }

    public List<TimeGraph> getGraphList() {
        return graphList;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public void injectMainController(MainController mainController, ChartController chartComponentController) {
        this.mainController = mainController;
        this.chartController = chartComponentController;
        initialize();
    }

    private void initialize() {
        this.nodeTableComponentController.injectGraphController(this);
        this.filtersComponentController.injectGraphController(mainController,this, chartController);
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
            createGraph(file);
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
        idGraph = 0;
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
            while (((line = br.readLine()) != null)) {
                createNodesVector(line);
            }
            this.csvRead = true;
    }

    /**
     * Creates vectors for every node in a single instant
     *
     * @param line a string of a time instant with all info about nodes
     */
    private void createNodesVector(String line) {
        int node = 0;
        ArrayList<ArrayList<String>> nodes = new ArrayList<>();
        String[] elements = line.split(",");
        double time = Double.parseDouble(elements[0]);
        int index = 1;
        while (index < elements.length) {
            ArrayList<String> vector = new ArrayList<>();
            for (int i = 1; i <= 5; i++) {
                vector.add(elements[index]);
                index++;
            }
            Optional<TimeGraph> t = graphList.stream().filter(graphList -> graphList.getGraphFromTime(time) != null).findFirst();
            if (t.isPresent()) {
                t.get().getGraph().getNode(node).setAttribute("time" + time, vector);
            }
            node++;
            nodes.add(vector);
        }
        addPositions(elements, nodes);
    }

    /**
     * Gets positions from the .csv file and adds them to the node coordinates
     */
    private void addPositions(String[] elements, ArrayList<ArrayList<String>> nodes) {
        for (TimeGraph g : graphList) {
            if (g.getTime() == Double.parseDouble(elements[0])) {
                for (int i = 0; i < nodes.size(); i++) {
                    if (g.getGraph().getNode(String.valueOf(i)) != null) {
                        g.getGraph().getNode(String.valueOf(i)).setAttribute("x", nodes.get(i).get(0));
                        g.getGraph().getNode(String.valueOf(i)).setAttribute("y", nodes.get(i).get(1));
                    }
                }
            }
        }
    }

    /**
     * Create a graph from a file
     *
     * @param file file to read
     */
    private void createGraph(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            Graph graph = new MultiGraph("id" + idGraph);
            idGraph++;
            if (line.contains("LOCATIONS")) {
                totNodes = Integer.parseInt(StringUtils.substringAfterLast(line, "LOCATIONS "));
                if ((line = br.readLine()) != null && line.contains(",")) {
                    staticGraph(line, br, graph, totNodes);
                    changeView(graph, 0.0);
                } else if (!line.contains(",")) {
                    dynamicGraph(line, br, totNodes);
                    createViews();
                    createTimeSlider();
                    changeGraphView(String.valueOf(graphList.get(0).getTime()));
                }
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

    /**
     * Builds a static graph from a file
     *
     * @param line     line to read
     * @param br       a {@link BufferedReader} to read the file
     * @param graph    graph in which to add nodes and edges
     * @param totNodes total number of nodes
     */
    private void staticGraph(String line, BufferedReader br, Graph graph, int totNodes) {
        try {
            createEdge(line, graph, totNodes);
            while ((line = br.readLine()) != null) {
                createEdge(line, graph);
            }
        } catch (Exception e) {
            DialogBuilder dialogBuilder = new DialogBuilder(mainController.getTheme());
            dialogBuilder.error(e.getMessage());
        }
    }

    /**
     * Builds a dynamic graph from a file
     *
     * @param line     line to read
     * @param br       a {@link BufferedReader} to read the file
     * @param totNodes total number of nodes
     */
    private void dynamicGraph(String line, BufferedReader br, int totNodes) {
        try {
            double time = Double.parseDouble(line);
            ArrayList<String> linesEdges = new ArrayList<>();
            while (true) {
                if ((line = br.readLine()) != null) {
                    if (!line.contains(",")) {
                        instantGraph(time, linesEdges, totNodes);
                        linesEdges.clear();
                        time = Double.parseDouble(line);
                    } else {
                        linesEdges.add(line);
                    }
                } else {
                    instantGraph(time, linesEdges, totNodes);
                    linesEdges.clear();
                    break;
                }
            }
        } catch (Exception e) {
            DialogBuilder dialogBuilder = new DialogBuilder(mainController.getTheme());
            dialogBuilder.error(e.getMessage());
        }
    }

    /**
     * Creates a single {@link TimeGraph} in a time instant
     *
     * @param time       instant
     * @param linesEdges line of the file that contains the edge between two nodes
     * @param totNodes   total number of nodes
     */
    private void instantGraph(double time, ArrayList<String> linesEdges, int totNodes) {
        Graph graph = new MultiGraph("id" + idGraph);
        graph.setAttribute("ui.stylesheet", this.theme);
        idGraph++;
        createNodes(graph, totNodes);
        for (String l : linesEdges) {
            createEdge(l, graph);
        }
        TimeGraph tg = new SimpleTimeGraph(graph, time);
        graphList.add(tg);
    }

    /**
     * Creates nodes and then an edge between two of them
     */
    private void createEdge(String line, Graph graph, int totNodes) {
        createNodes(graph, totNodes);
        createEdge(line, graph);
    }

    /**
     * Create and edge between two nodes
     */
    private void createEdge(String line, Graph graph) {
        String[] elements = line.split(",");
        String vertex1 = elements[0];
        String vertex2 = elements[1];
        String edge = elements[2];
        boolean exist = graph.edges().anyMatch(edge1 -> (edge1.getSourceNode().equals(graph.getNode(vertex1)) || edge1.getSourceNode().equals(graph.getNode(vertex2))) && (edge1.getTargetNode().equals(graph.getNode(vertex2)) || edge1.getTargetNode().equals(graph.getNode(vertex1))));
        Edge e = graph.addEdge("id" + idGraph, graph.getNode(vertex1), graph.getNode(vertex2));
        idGraph++;
//        e.setAttribute("ui.label", edge);
        if (exist)
            e.setAttributes(Map.of(
//                    "ui.label", edge,
                    "ui.class", "multiple"
            ));
//        else
//            e.setAttribute("ui.label", edge);
    }

    /**
     * Creates all the nodes of a graph
     *
     * @param graph graph in which to add nodes
     * @param tot   total number of nodes to create
     */
    private void createNodes(Graph graph, int tot) {
        int i = 0;
        while (i < tot) {
            Node n = graph.addNode(String.valueOf(i));
            n.setAttribute("ui.label", i);
            i++;
        }
    }

    @FXML
    private void deselectFiltersTable() {
        filtersComponentController.tableFilters.getSelectionModel().clearSelection();
    }

    @FXML
    private void deselectNodeTable() {
        nodeTableComponentController.nodesTable.getSelectionModel().clearSelection();
    }

//    @FXML
//    Slider zoomSlider;
//
//    @FXML
//    private void zoomGraph() {
//        View view = this.v.getDefaultView();
//        view.getCamera().setViewCenter(2, 3, 4);
//        view.getCamera().setViewPercent(0.5);
//    }


}