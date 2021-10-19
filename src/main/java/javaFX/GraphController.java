package javaFX;

import App.CsvUtility.CsvImport;
import App.DialogUtility.DialogBuilder;
import App.TraUtility.LabelEdge;
import javafx.fxml.FXML;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;

import org.jgrapht.Graph;

import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.*;
//import org.jgrapht.graph.DefaultEdge;
//import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.graph.builder.GraphBuilder;

import javax.swing.text.html.ImageView;
import java.util.List;

public class GraphController {

    @FXML
    AnchorPane anchorID;
    @FXML
    ImageView graphImage;

    @FXML
    private void openExplorer() throws IOException {
        FileChooser fileChooser = new FileChooser();
        Stage stage = (Stage) anchorID.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            BufferedReader br = new BufferedReader(new FileReader(file));
            try {
                String line = br.readLine();
                Graph<String, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
                if (line.contains("LOCATIONS")) {
//                    int nodes = Integer.parseInt(line.substring(10));
                    while ((line = br.readLine()) != null) {
                        createEdge(line, graph);
//                        System.out.println(line);
                    }
                }
                visualizeGraph(graph);
            } catch (Exception e) {
                DialogBuilder dialogBuilder = new DialogBuilder();
                dialogBuilder.error("Error!", e.getMessage());
            } finally {
                br.close();
            }
        }

    }

    private void visualizeGraph(Graph<String, DefaultEdge> graph) throws IOException {
        File imgFile = new File("src/test/resources/graph.png");
        imgFile.createNewFile();


    }

    private void createEdge(String line, Graph<String, DefaultEdge> graph) {
        String[] elements = line.split(",");
        String vertex1 = elements[0];
        String vertex2 = elements[1];
        String edge = elements[2];
        graph.addVertex(vertex1);
        graph.addVertex(vertex2);
        graph.addEdge(vertex1, vertex2, new LabelEdge(edge));
    }

}
