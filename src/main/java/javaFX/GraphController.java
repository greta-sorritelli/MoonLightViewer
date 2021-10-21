package javaFX;

import App.DialogUtility.DialogBuilder;
import javafx.fxml.FXML;
import javafx.scene.SubScene;
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

public class GraphController {

    @FXML
    AnchorPane anchorID;
    @FXML
    BorderPane borderPane = new BorderPane();

    private int idGraph = 0;


    @FXML
    private void openExplorer() {
        System.setProperty("org.graphstream.ui", "javafx");
        FileChooser fileChooser = new FileChooser();
        Stage stage = (Stage) anchorID.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line = br.readLine();
                Graph graph = new SingleGraph("id" + idGraph);
                idGraph++;
                FxViewer v = new FxViewer(graph, FxViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
                v.enableAutoLayout();
                FxViewPanel panel = (FxViewPanel) v.addDefaultView(false, new FxGraphRenderer());
                if (line.contains("LOCATIONS")) {
                    while ((line = br.readLine()) != null) {
                        createEdge(line, graph);
                    }
                }
                SubScene scene = new SubScene(panel, 500, 300);
                borderPane.setCenter(scene);
                graph.setAttribute("ui.stylesheet", "url('file://src/main/resources/graphStylesheet.css')");
//                graph.display();
            } catch (Exception e) {
                DialogBuilder dialogBuilder = new DialogBuilder();
                dialogBuilder.error("Error!", e.getMessage());
            }
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