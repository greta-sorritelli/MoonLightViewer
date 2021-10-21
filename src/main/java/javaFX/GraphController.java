package javaFX;

import App.DialogUtility.DialogBuilder;
import App.GraphUtility.TimeGraph;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.SubScene;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.javafx.FxGraphRenderer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GraphController {

    @FXML
    AnchorPane anchorID;
    @FXML
    BorderPane borderPane = new BorderPane();
    @FXML
    ListView<RadioButton> list;

    private int idGraph = 0;

    private final List<TimeGraph> graphList = new ArrayList<>();

    @FXML
    private void openExplorer() {
        System.setProperty("org.graphstream.ui", "javafx");
        FileChooser fileChooser = new FileChooser();
        Stage stage = (Stage) anchorID.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        createGraph(file);
    }

    private void createGraph(File file) {
        if (file != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line = br.readLine();
                Graph graph = new SingleGraph("id" + idGraph);
                idGraph++;
                FxViewer v = new FxViewer(graph, FxViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
                v.enableAutoLayout();
                FxViewPanel panel = (FxViewPanel) v.addDefaultView(false, new FxGraphRenderer());
                if (line.contains("LOCATIONS")) {
                    if ((line = br.readLine()) != null && line.contains(",")) {
                        staticGraph(line, br, graph);
                        SubScene scene = new SubScene(panel, 390, 310);
                        borderPane.setCenter(scene);
                        graph.setAttribute("ui.stylesheet", "url('file://src/main/resources/graphStylesheet.css')");
                    } else if ((line = br.readLine()) != null && !line.contains(",")) {
                        dynamicGraph(line, br, null);
                        createTimeButtons();
                    }
                }

//                graph.display();
            } catch (Exception e) {
                DialogBuilder dialogBuilder = new DialogBuilder();
                dialogBuilder.error("Error!", e.getMessage());
            }
        }


    }

    private void createTimeButtons() {
        if (list != null && !list.getItems().isEmpty()) {
            list.getItems().clear();
            final ObservableList<RadioButton> variables = FXCollections.observableArrayList();
            for (TimeGraph t : graphList) {
                RadioButton r = new RadioButton(String.valueOf(t.getTime()));
                r.selectedProperty().addListener((observable, oldValue, newValue) -> {
                    r.setSelected(!oldValue);
                    changeGraphView(r.getText());
                });
                variables.add(r);
            }
            if (!variables.isEmpty())
                list.getItems().addAll(variables);
        }
    }


    private void changeGraphView(String time) {
        Optional<TimeGraph> g = graphList.stream().filter(timeGraph -> timeGraph.getTime() == Double.parseDouble(time)).findFirst();
        if (g.isPresent()) {
            FxViewer v = new FxViewer(g.get().getGraph(), FxViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
            v.enableAutoLayout();
            FxViewPanel panel = (FxViewPanel) v.addDefaultView(false, new FxGraphRenderer());
            SubScene scene = new SubScene(panel, 390, 310);
            borderPane.setCenter(scene);
        }
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

    private void dynamicGraph(String line, BufferedReader br, Graph graph) {
        try {
            if (graph == null) {
                graph = new SingleGraph("id" + idGraph);
                idGraph++;
            }
            double time = Double.parseDouble(line);
            while ((line = br.readLine()) != null) {
                if (!line.contains(",")) {
                    TimeGraph tg = new TimeGraph(graph, time);
                    dynamicGraph(line, br, graph);
                } else
                    createEdge(line, graph);
            }
        } catch (Exception e) {
            DialogBuilder dialogBuilder = new DialogBuilder();
            dialogBuilder.error("Error!", e.getMessage());
        }
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
        Edge e = graph.addEdge("id" + idGraph, vertex1, vertex2);
        idGraph++;
        e.setAttribute("ui.label", edge);
    }
}