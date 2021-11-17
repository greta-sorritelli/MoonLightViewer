package App.javaModel.chart;

import App.javaModel.graph.TimeGraph;
import javafx.scene.chart.XYChart;
import org.graphstream.graph.Graph;

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

    List<XYChart.Series<String, Number>> barChartFromNodes(Graph staticGraph);
}
