package App.CsvUtility;

import App.DialogUtility.DialogBuilder;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CsvImport {

    public static List<Series<Number, Number>> getSeriesFromCsv(String path) {
        Series<Number, Number> xSeries = new Series<>();
        Series<Number, Number> ySeries = new Series<>();
        Series<Number, Number> zSeries = new Series<>();
        xSeries.setName("X");
        ySeries.setName("Y");
        zSeries.setName("Z");
        readCsv(path, xSeries, ySeries, zSeries);
        List<Series<Number, Number>> series = new ArrayList<>();
        series.add(xSeries);
        series.add(ySeries);
        series.add(zSeries);
        return series;
    }

    private static void readCsv(String path, Series<Number, Number> xSeries, Series<Number, Number> ySeries, Series<Number, Number> zSeries) {
        try (CSVReader dataReader = new CSVReader(new FileReader(path))) {
            String[] nextLine;
            while ((nextLine = dataReader.readNext()) != null) {
                if (nextLine.length == 0 || nextLine[0].equals("")) continue;
                Double time = Double.parseDouble(nextLine[0]);
                double X = Double.parseDouble(nextLine[1]);
                xSeries.getData().add(new XYChart.Data<>(time, X));
                double Y = Double.parseDouble(nextLine[2]);
                ySeries.getData().add(new XYChart.Data<>(time, Y));
                double Z = Double.parseDouble(nextLine[3]);
                zSeries.getData().add(new XYChart.Data<>(time, Z));
            }
        } catch (IOException | CsvValidationException e) {
            DialogBuilder dialogBuilder = new DialogBuilder();
            dialogBuilder.error("Error", e.getMessage());
        }
    }
}
