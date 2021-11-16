package App.javaModel.Chart;

import App.javaModel.Graph.TimeGraph;
import javafx.scene.chart.XYChart;

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

}
