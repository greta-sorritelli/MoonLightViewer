package App.GraphicOperations;

import com.opencsv.CSVReader;
import java.io.File;
import java.io.FileReader;
import org.knowm.xchart.*;
import org.knowm.xchart.CSVImporter.*;
import org.knowm.xchart.style.Styler;

import static org.knowm.xchart.CSVImporter.DataOrientation.*;

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

    public XYChart getSeriesFromCsv(String path) {
//        SeriesData seriesData = CSVImporter.getSeriesDataFromCSVFile(path, Rows);
        XYChart chart = CSVImporter.getChartFromCSVDir(path, Rows, 500, 200, Styler.ChartTheme.XChart);
        return chart;
    }
}
