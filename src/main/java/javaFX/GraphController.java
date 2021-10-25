package javaFX;

import App.DialogUtility.DialogBuilder;
import App.GraphUtility.SimpleMouseManager;
import App.GraphUtility.SimpleTimeGraph;
import App.GraphUtility.TimeGraph;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.graph.implementations.SingleGraph;
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
import java.util.stream.Stream;

public class GraphController {

    @FXML
    AnchorPane anchorID;
    @FXML
    Label graphType;
    @FXML
    BorderPane borderPane = new BorderPane();
    @FXML
    ListView<RadioButton> list;

    private final ObservableList<RadioButton> variables = FXCollections.observableArrayList();

    private final ToggleGroup group = new ToggleGroup();

    private int idGraph = 0;

    private final List<TimeGraph> graphList = new ArrayList<>();

    @FXML
    private void openExplorer() {
        System.setProperty("org.graphstream.ui", "javafx");
        FileChooser fileChooser = new FileChooser();
        Stage stage = (Stage) anchorID.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        resetAll();
        createGraph(file);
    }

    private void resetAll() {
        if (!group.getToggles().isEmpty())
            group.getToggles().clear();
        variables.clear();
        list.getItems().clear();
        idGraph = 0;
        graphList.clear();
    }

    private void createGraph(File file) {
        if (file != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line = br.readLine();
                Graph graph = new MultiGraph("id" + idGraph);
                idGraph++;
                if (line.contains("LOCATIONS")) {
                    if ((line = br.readLine()) != null && line.contains(",")) {
                        staticGraph(line, br, graph);
                        showGraph(graph, "Static Graph");
                    } else if (!line.contains(",")) {
                        dynamicGraph(line, br);
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


    private void changeGraphView(String time) {
        Optional<TimeGraph> g = graphList.stream().filter(timeGraph -> timeGraph.getTime() == Double.parseDouble(time)).findFirst();
        if (g.isPresent()) {
            showGraph(g.get().getGraph(), "Dynamic Graph");
            list.getItems().stream().filter(radioButton -> radioButton.getText().equals(time)).findFirst().get().setSelected(true);
            list.getItems().stream().filter(radioButton -> radioButton.getText().equals(time)).findFirst().get().requestFocus();
        }
    }

    private void showGraph(Graph graph, String type) {
        graph.setAttribute("ui.stylesheet", "url('file://src/main/resources/graphStylesheet.css')");
        FxViewer v = new FxViewer(graph, FxViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        v.enableAutoLayout();
        FxViewPanel panel = (FxViewPanel) v.addDefaultView(false, new FxGraphRenderer());
        SubScene scene = new SubScene(panel, borderPane.getWidth(), borderPane.getHeight());
        borderPane.setCenter(scene);
        v.getDefaultView().setMouseManager(new SimpleMouseManager());
        graphType.setText(type);
    }


    private void staticGraph(String line, BufferedReader br, Graph graph) {
        try {
            createEdge(line, graph);
            while ((line = br.readLine()) != null) {
                createEdge(line, graph);
            }
        } catch (Exception e) {
            DialogBuilder dialogBuilder = new DialogBuilder();
            dialogBuilder.error("Error!", e.getMessage());
        }
    }

    private void dynamicGraph(String line, BufferedReader br) {
        try {
            double time = Double.parseDouble(line);
            ArrayList<String> linesEdges = new ArrayList<>();
            while (true) {
                if ((line = br.readLine()) != null) {
                    if (!line.contains(",")) {
                        instantGraph(time, linesEdges);
                        linesEdges.clear();
                        time = Double.parseDouble(line);
                    } else {
                        linesEdges.add(line);
                    }
                } else {
                    instantGraph(time, linesEdges);
                    linesEdges.clear();
                    break;
                }
            }
        } catch (Exception e) {
            DialogBuilder dialogBuilder = new DialogBuilder();
            dialogBuilder.error("Error!", e.getMessage());
        }
    }

    private void instantGraph(double time, ArrayList<String> linesEdges) {
        Graph graph = new MultiGraph("id" + idGraph);
        graph.setAttribute("ui.stylesheet", "url('file://src/main/resources/graphStylesheet.css')");
        idGraph++;
        for (String l : linesEdges) {
            createEdge(l, graph);
        }
        TimeGraph tg = new SimpleTimeGraph(graph, time);
        graphList.add(tg);
    }

    private void createEdge(String line, Graph graph) {
        String[] elements = line.split(",");
        String vertex1 = elements[0];
        String vertex2 = elements[1];
        String edge = elements[2];
        if (graph.getNode(vertex1) == null) {
            Node n1 = graph.addNode(vertex1);
            n1.setAttribute("ui.label", vertex1);
        }
        if (graph.getNode(vertex2) == null) {
            Node n2 = graph.addNode(vertex2);
            n2.setAttribute("ui.label", vertex2);
        }
        boolean exist = graph.edges().anyMatch(edge1 -> (edge1.getSourceNode().equals(graph.getNode(vertex1)) || edge1.getSourceNode().equals(graph.getNode(vertex2))) && (edge1.getTargetNode().equals(graph.getNode(vertex2)) || edge1.getTargetNode().equals(graph.getNode(vertex1))));
        Edge e = graph.addEdge("id" + idGraph, graph.getNode(vertex1), graph.getNode(vertex2));
        idGraph++;
        e.setAttribute("ui.label", edge);
        if (exist)
            e.setAttributes(Map.of(
                    "ui.label", edge,
                    "ui.class", "multiple"
            ));
        else
            e.setAttribute("ui.label", edge);
    }
}