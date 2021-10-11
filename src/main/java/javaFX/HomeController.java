package javaFX;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class HomeController {

    @FXML
    private AnchorPane anchorID;
    @FXML
    private NumberAxis yAxis = new NumberAxis(-1, 1, 2);
    @FXML
    private CategoryAxis xAxis = new CategoryAxis();
    @FXML
    private LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);

    @FXML
    private void openExplorer() {
        FileChooser fileChooser = new FileChooser();
        Stage stage = (Stage) anchorID.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            String path = file.getAbsolutePath();
//            CsvToGraphic read = new CsvToGraphic(file);
//            read.readCsv(file);
            getSeriesFromCsv(path);
        }
    }

    public void getSeriesFromCsv(String path) {
        yAxis = new NumberAxis(-1, 1, 2);
        yAxis.setLabel("Values");
        xAxis.setLabel("time");
        lineChart.setTitle("X,Y,Z values in time");
        lineChart.setLegendSide(Side.RIGHT);
        lineChart.setAnimated(true);

        XYChart.Series<String,Number> xSeries = new XYChart.Series<>();
        XYChart.Series<String,Number> ySeries = new XYChart.Series<>();
        XYChart.Series<String,Number> zSeries = new XYChart.Series<>();

        xSeries.setName("X");
        ySeries.setName("Y");
        zSeries.setName("Z");

        try (CSVReader dataReader = new CSVReader(new FileReader(path))) {
            String[] nextLine;
            while ((nextLine = dataReader.readNext()) != null) {
                if(nextLine.length == 0 || nextLine[0].equals("")) continue;
                String time = nextLine[0];
                double X = Double.parseDouble(nextLine[1]);
                xSeries.getData().add(new XYChart.Data<>(time, X));
                double Y = Double.parseDouble(nextLine[2]);
                ySeries.getData().add(new XYChart.Data<>(time, Y));
                double Z = Double.parseDouble(nextLine[3]);
                zSeries.getData().add(new XYChart.Data<>(time, Z));
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        lineChart.getData().addAll(xSeries, ySeries, zSeries);
        anchorID.getScene().getStylesheets().add("lineChart.css");
    }

}


