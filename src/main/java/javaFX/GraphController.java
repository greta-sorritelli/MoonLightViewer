package javaFX;

import App.CsvUtility.CsvImport;
import App.DialogUtility.DialogBuilder;
import javafx.fxml.FXML;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import org.jgrapht;
import java.util.List;

public class GraphController {

    @FXML
    AnchorPane anchorID;

    @FXML
    private void openExplorer() throws IOException {
        FileChooser fileChooser = new FileChooser();
        Stage stage = (Stage) anchorID.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            BufferedReader br = new BufferedReader(new FileReader(file));
            try {
                String line = br.readLine();
                if(line.contains("LOCATIONS")) {
                    int nodes = Integer.parseInt(line.substring(10));
//                    Graph
                    while ((line = br.readLine()) != null) {
                        createEdge();
                        System.out.println(line);
                    }
                }
            } catch (Exception e) {
                DialogBuilder dialogBuilder = new DialogBuilder();
                dialogBuilder.error("Error!", e.getMessage());
            } finally {
                br.close();
            }
        }

    }

    private void createEdge() {
    }

}
