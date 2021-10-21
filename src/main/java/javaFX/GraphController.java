package javaFX;

import javafx.fxml.FXML;
import javafx.scene.SubScene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.javafx.FxGraphRenderer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class GraphController {

    @FXML
    AnchorPane anchorID;
    @FXML
    BorderPane borderPane = new BorderPane();

   private int idGraph = 0;

    @FXML
    private void openExplorer() throws IOException {
        FileChooser fileChooser = new FileChooser();
        Stage stage = (Stage) anchorID.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            BufferedReader br = new BufferedReader(new FileReader(file));
            SingleGraph graph = new SingleGraph(String.valueOf(idGraph));
            FxViewer v = new FxViewer(graph, FxViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
            v.enableAutoLayout();
            FxViewPanel panel = (FxViewPanel)v.addDefaultView(false, new FxGraphRenderer());
            System.setProperty("org.graphstream.ui", "javafx");
            graph.setAttribute("ui.stylesheet", "url(graphStylesheet.css)");

            try {
                String line = br.readLine();
                if (line.contains("LOCATIONS")) {
                    while ((line = br.readLine()) != null) {
                        createEdge(line, graph);
                    }
                }
                SubScene scene = new SubScene(panel, 500, 300);
                borderPane.setCenter(scene);
//                graph.display();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                br.close();
            }
        }
    }

    private void createEdge(String line, Graph graph) {
        String[] elements = line.split(",");
        String vertex1 = elements[0];
        String vertex2 = elements[1];
        String edge = elements[2];
        if(graph.getNode(vertex1) == null )
            graph.addNode(vertex1);
        if(graph.getNode(vertex2) == null)
            graph.addNode(vertex2);
        Edge e = graph.addEdge("id "+ String.valueOf(idGraph), vertex1, vertex2);
        e.setAttribute("ui.label",edge);
        idGraph++;

    }
}
