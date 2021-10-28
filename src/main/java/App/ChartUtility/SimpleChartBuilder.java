package App.ChartUtility;

import App.DialogUtility.DialogBuilder;
import App.GraphUtility.TimeGraph;
import com.opencsv.CSVReader;
import javafx.scene.chart.XYChart;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class SimpleChartBuilder {

//    public static List<XYChart.Series<Number, Number>> getSeriesFromCsv(String path) {
//        XYChart.Series<Number, Number> xSeries = new XYChart.Series<>();
//        XYChart.Series<Number, Number> ySeries = new XYChart.Series<>();
//        XYChart.Series<Number, Number> zSeries = new XYChart.Series<>();
//        xSeries.setName("X");
//        ySeries.setName("Y");
//        zSeries.setName("Z");
//        readCsv(path, xSeries, ySeries, zSeries);
//        List<XYChart.Series<Number, Number>> series = new ArrayList<>();
//        series.add(xSeries);
//        series.add(ySeries);
//        series.add(zSeries);
//        return series;
//    }

    private static void readCsv(String path, XYChart.Series<Number, Number> xSeries, XYChart.Series<Number, Number> ySeries, XYChart.Series<Number, Number> zSeries) {
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
        } catch (Exception e) {
            DialogBuilder dialogBuilder = new DialogBuilder();
            dialogBuilder.error("Error", e.getMessage());
        }
    }

    public static List<XYChart.Series<Number, Number>> getSeriesFromNodes(List<TimeGraph> timeGraph) {
        List<XYChart.Series<Number, Number>> series = new ArrayList<>();
        int totSeries = timeGraph.get(0).getGraph().getNodeCount();
        ArrayList<Double> times = new ArrayList<>();
        timeGraph.forEach(timeGraph1 -> times.add(timeGraph1.getTime()));
        int node = 0;
        while (node < totSeries) {
            XYChart.Series<Number, Number> seriesX = new XYChart.Series<>();
            seriesX.setName("Node " + node);
            addData(seriesX, times, timeGraph, node);
            series.add(seriesX);

            node++;
        }

        return series;
    }

    private static void addData(XYChart.Series<Number, Number> series, ArrayList<Double> times, List<TimeGraph> timeGraph, int seriesNode) {

        for (double t : times) {
            for (TimeGraph graph : timeGraph) {
//                if()
//                String attribute = graph.forEach(timeGraph1 -> timeGraph1.getGraph().getNode(seriesNode).getAttribute("time" + t).toString());
//                series.getData().add(new XYChart.Data<>(t, X));
            }

        }
    }
}
