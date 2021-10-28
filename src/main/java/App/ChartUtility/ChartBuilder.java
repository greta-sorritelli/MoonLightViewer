package App.ChartUtility;

import App.GraphUtility.TimeGraph;
import javafx.scene.chart.XYChart;

import java.util.List;

public interface ChartBuilder {

    List<XYChart.Series<Number, Number>> getSeriesFromNodes(List<TimeGraph> timeGraph);

}
