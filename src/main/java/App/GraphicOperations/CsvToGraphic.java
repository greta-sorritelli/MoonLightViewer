package App.GraphicOperations;

import com.opencsv.CSVReader;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CsvToGraphic {
    public static void main(String[] args)
    {
        CSVReader reader = null;
        try
        {
//parsing a CSV file into CSVReader class constructor
        //    reader = new CSVReader(new FileReader("C:\\Users\\Greta\\Documents\\Greta\\università\\project\\esercizi\\traajectory.csv"));
//            File file = new File ("C:\\<directory>");
//            Desktop desktop = Desktop.getDesktop();
            //Create a file chooser
            final JFileChooser fc = new JFileChooser();

//In response to a button click:
//            int returnVal = fc.showOpenDialog(aComponent);
//            reader = new CSVReader(new FileReader(file));

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

//    private void OpenActionPerformed(java.awt.event.ActionEvent evt) {
//        int returnVal = fileChooser.showOpenDialog(this);
//        if (returnVal == JFileChooser.APPROVE_OPTION) {
//            File file = fileChooser.getSelectedFile();
//            try {
//                // What to do with the file, e.g. display it in a TextArea
//                textarea.read( new FileReader( file.getAbsolutePath() ), null );
//            } catch (IOException ex) {
//                System.out.println("problem accessing file"+file.getAbsolutePath());
//            }
//        } else {
//            System.out.println("File access cancelled by user.");
//        }
//    }


}
