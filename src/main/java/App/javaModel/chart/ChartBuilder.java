package App.javaModel.chart;

import App.javaModel.graph.TimeGraph;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.List;

/**
 * Interface for a builder of charts from a graph
 */
public interface ChartBuilder {




    /**
     * Create series of a chart from nodes in a graph
     * @param timeGraph a {@link TimeGraph}
     * @return a list of series created
     */
    List<XYChart.Series<Number, Number>> getSeriesFromNodes(List<TimeGraph> timeGraph);

    ArrayList<XYChart.Series<Number, Number>> getSeriesFromStaticGraph(String line, ArrayList<XYChart.Series<Number, Number>> list, boolean b);

    void clearList();

    void addLineData(LineChart<Number, Number> lineChart, String[] attributes);


    ArrayList<ArrayList<String>> getAttributes();

    ArrayList<XYChart.Series<Number, Number>> getListLinear();

    ArrayList<XYChart.Series<Number, Number>> getListLog();

    void addAttributes(String[] attributes);
//    List<XYChart.Series<Number, Number>> getSeriesFromNodes(Graph staticGraph);
}
