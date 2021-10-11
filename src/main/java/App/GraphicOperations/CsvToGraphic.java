package App.GraphicOperations;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;

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
}
