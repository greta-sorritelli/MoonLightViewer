package App.GraphicOperations;

import com.opencsv.CSVReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import com.opencsv.exceptions.CsvValidationException;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;

public class CsvToGraphic {

    File file = null;

    public CsvToGraphic(File file) {
        this.file = file;
    }

    public void readCsv(File file) {
        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(file));
            String [] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                for(String token : nextLine) {
                    System.out.print(token + " ");
                }
                System.out.print("\n");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private final NumberAxis yAxis = new NumberAxis(-1, 1, 0.00000000001);
    @FXML
    private final CategoryAxis xAxis = new CategoryAxis();
    @FXML
    private final LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);

    public void getSeriesFromCsv(String path) {
//        SeriesData seriesData = CSVImporter.getSeriesDataFromCSVFile(path, Rows);
//        XYChart chart = new XYChart(500, 200, Styler.ChartTheme.XChart);
//        chart.
//        return chart;

        yAxis.setLabel("Values");
        xAxis.setLabel("time");
        lineChart.setTitle("X,Y,Z values in time");

        XYChart.Series xSeries = new XYChart.Series();
        XYChart.Series ySeries = new XYChart.Series();
        XYChart.Series zSeries = new XYChart.Series();

        xSeries.setName("X");
        ySeries.setName("Y");
        zSeries.setName("Z");

        try (CSVReader dataReader = new CSVReader(new FileReader(path))) {
            String[] nextLine;
            while ((nextLine = dataReader.readNext()) != null) {
                if(nextLine.length == 0 || nextLine[0].equals("")) continue;
                String time = String.valueOf(nextLine[0]);
                double X = Double.parseDouble(nextLine[1]);
                xSeries.getData().add(new XYChart.Data(time, X));
                double Y = Double.parseDouble(nextLine[2]);
                ;
                ySeries.getData().add(new XYChart.Data(time, Y));
                double Z = Double.parseDouble(nextLine[3]);
                zSeries.getData().add(new XYChart.Data(time, Z));
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

        lineChart.getData().addAll(xSeries, ySeries, zSeries);
        Scene scene = new Scene(lineChart, 500, 400);

    }

}
