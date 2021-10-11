package javaFX;

import java.io.File;
import App.GraphicOperations.CsvToGraphic;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class HomeController {

    @FXML
    private AnchorPane anchorID;

    @FXML
    private void openExplorer() {
        FileChooser fileChooser = new FileChooser();
        Stage stage = (Stage) anchorID.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            String path = file.getAbsolutePath();
            CsvToGraphic read = new CsvToGraphic(file);
            read.readCsv(file);
            read.getSeriesFromCsv(path);
        }
    }
}


