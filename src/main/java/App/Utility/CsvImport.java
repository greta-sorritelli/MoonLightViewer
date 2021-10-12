package App.Utility;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import javafx.scene.chart.XYChart;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CsvImport {

//    private static final CsvImport csvImport = new CsvImport();
//
//    private CsvImport() {
//    }
//
//    public static CsvImport getCvsImport() {
//       return csvImport;
//    }


    public static List<XYChart.Series<Number, Number>> getSeriesFromCsv(String path) {
        XYChart.Series<Number, Number> xSeries = new XYChart.Series<>();
        XYChart.Series<Number, Number> ySeries = new XYChart.Series<>();
        XYChart.Series<Number, Number> zSeries = new XYChart.Series<>();

        xSeries.setName("X");
        ySeries.setName("Y");
        zSeries.setName("Z");

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
            e.printStackTrace();
        }
        List<XYChart.Series<Number, Number>> series = new ArrayList<>();
        series.add(xSeries);
        series.add(ySeries);
        series.add(zSeries);
        return series;
    }
}
