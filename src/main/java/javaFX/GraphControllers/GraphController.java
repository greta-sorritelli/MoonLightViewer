package javaFX.GraphControllers;

import App.DialogUtility.DialogBuilder;
import App.GraphUtility.SimpleMouseManager;
import App.GraphUtility.SimpleTimeGraph;
import App.GraphUtility.TimeGraph;
import javaFX.ChartController;
import javaFX.MainController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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
    BorderPane borderPane = new BorderPane();
    @FXML
    ListView<RadioButton> list;
    @FXML
    Label infoNode;
    @FXML
    NodesTableController nodeTableComponentController;
    @FXML
    FiltersController filtersComponentController;

    private final ObservableList<RadioButton> variables = FXCollections.observableArrayList();
    private final ToggleGroup group = new ToggleGroup();
    private int idGraph = 0;
    private int totNodes = 0;
    private final List<TimeGraph> graphList = new ArrayList<>();
    private Graph currentGraph;
    private String theme = "url('file://src/main/resources/graphLightTheme.css')";
    private ChartController chartController;
    private MainController mainController;
    private FxViewer v;

    public ObservableList<RadioButton> getVariables() {
        return variables;
    }
    public int getTotNodes() {
        return totNodes;
    }
    public List<TimeGraph> getGraphList() {
        return graphList;
    }
    public String getTheme() {
        return theme;
    }
    public void setTheme(String theme) {
        this.theme = theme;
    }

    public void injectMainController(MainController mainController, ChartController chartComponentController) {
        this.mainController = mainController;
        this.chartController = chartComponentController;
    }

    @FXML
    public void initialize() {
        this.nodeTableComponentController.injectGraphController(this);
        this.filtersComponentController.injectGraphController(this);
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
        Stage stage = (Stage) mainController.getVbox().getScene().getWindow();
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
        resetAll();
        createGraph(file);
        nodeTableComponentController.initTable();
    }

    /**
     * Opens explorer with only .csv files
     */
    public void openCSVExplorer() {
        File file = open("CSV Files", "*.csv");
        readCSV(file);
        chartController.createDataFromGraphs(graphList);
    }

    /**
     * Reset all lists and info
     */
    private void resetAll() {
        if (!group.getToggles().isEmpty())
            group.getToggles().clear();
        variables.clear();
        list.getItems().clear();
        idGraph = 0;
        graphList.clear();
        nodeTableComponentController.resetTable();
    }

    /**
     * Read a .csv file to get info about nodes
     *
     * @param file .csv file
     */
    private void readCSV(File file) {
        if (file != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while (((line = br.readLine()) != null)) {
                    createNodesVector(line);
                }
            } catch (Exception e) {
                DialogBuilder dialogBuilder = new DialogBuilder();
                dialogBuilder.error("Error!", e.getMessage());
            }
        }

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
        if (file != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line = br.readLine();
                Graph graph = new MultiGraph("id" + idGraph);
                idGraph++;
                if (line.contains("LOCATIONS")) {
                    totNodes = Integer.parseInt(StringUtils.substringAfterLast(line, "LOCATIONS "));
                    if ((line = br.readLine()) != null && line.contains(",")) {
                        staticGraph(line, br, graph, totNodes);
                        showGraph(graph, "Static Graph", 0.0);
                    } else if (!line.contains(",")) {
                        dynamicGraph(line, br, totNodes);
                        createTimeButtons();
                        group.getToggles().get(0).setSelected(true);
                        changeGraphView(String.valueOf(graphList.get(0).getTime()));
                    }
                }
            } catch (Exception e) {
                DialogBuilder dialogBuilder = new DialogBuilder();
                dialogBuilder.error("Error!", e.getMessage());
            }
        }
    }

    /**
     * For each time instant creates a radio button to select a specific graph in time
     */
    private void createTimeButtons() {
        if (list != null && !list.getItems().isEmpty())
            list.getItems().clear();
        for (TimeGraph t : graphList) {
            RadioButton r = new RadioButton(String.valueOf(t.getTime()));
            r.setToggleGroup(group);
            variables.add(r);
        }
        if (!variables.isEmpty())
            list.getItems().addAll(variables);
        list.setEditable(true);
        group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null)
                oldValue.setSelected(false);
            if (newValue != null) {
                newValue.setSelected(true);
                changeGraphView(((RadioButton) newValue).getText());
            }
        });
    }

    /**
     * Changes visualization of a dynamic graph in time
     *
     * @param time instant chosen
     */
    private void changeGraphView(String time) {
        Optional<TimeGraph> g = graphList.stream().filter(timeGraph -> timeGraph.getTime() == Double.parseDouble(time)).findFirst();
        if (g.isPresent()) {
            showGraph(g.get().getGraph(), "Dynamic Graph", Double.parseDouble(time));
            Optional<RadioButton> r = list.getItems().stream().filter(radioButton -> radioButton.getText().equals(time)).findFirst();
            if (r.isPresent()) {
                r.get().setSelected(true);
                r.get().requestFocus();
            }
        }
    }

    public Graph getCurrentGraph() {
        return currentGraph;
    }

    /**
     * Shows a graph
     *
     * @param graph graph to visualize
     * @param type  dynamic or static
     * @param time  instant chosen if the graph is dynamic
     */
    private void showGraph(Graph graph, String type, Double time) {
        currentGraph = graph;
        graph.setAttribute("ui.stylesheet", theme);
        v = new FxViewer(graph, FxViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        FxViewPanel panel = (FxViewPanel) v.addDefaultView(false, new FxGraphRenderer());
        SubScene scene = new SubScene(panel, borderPane.getWidth(), borderPane.getHeight());
        borderPane.setCenter(scene);
        SimpleMouseManager sm = new SimpleMouseManager(graph, time, chartController);
        sm.addPropertyChangeListener(evt -> {
            if (evt.getPropertyName().equals("LabelProperty")) {
                infoNode.setText(evt.getNewValue().toString());
            }
        });
        v.getDefaultView().setMouseManager(sm);
        graphType.setText(type);
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
            DialogBuilder dialogBuilder = new DialogBuilder();
            dialogBuilder.error("Error!", e.getMessage());
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
            DialogBuilder dialogBuilder = new DialogBuilder();
            dialogBuilder.error("Error!", e.getMessage());
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
        graph.setAttribute("ui.stylesheet", "url('file://src/main/resources/graphStylesheet.css')");
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
}