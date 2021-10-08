package App.GraphicOperations;

import com.opencsv.CSVReader;

import java.io.FileReader;

public class CsvToGraphic {
    public static void main(String[] args)
    {
        CSVReader reader = null;
        try
        {
//parsing a CSV file into CSVReader class constructor
            reader = new CSVReader(new FileReader("C:\\Users\\Greta\\Documents\\Greta\\università\\project\\esercizi\\traajectory.csv"));
            String [] nextLine;
//reads one line at a time
            while ((nextLine = reader.readNext()) != null)
            {
                for(String token : nextLine)
                {
                    System.out.print(token);
                }
                System.out.print("\n");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
