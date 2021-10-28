package App.ChartUtility;

import App.GraphUtility.TimeGraph;
import javafx.scene.chart.XYChart;
import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.List;

public class SimpleChartBuilder implements ChartBuilder {

    public List<XYChart.Series<Number, Number>> getSeriesFromNodes(List<TimeGraph> timeGraph) {
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

    private void addData(XYChart.Series<Number, Number> series, ArrayList<Double> times, List<TimeGraph> timeGraph, int seriesNode) {
        for (double t : times) {
            for (TimeGraph graph : timeGraph) {
                if (graph.getTime() == t) {
                    String attributes = graph.getGraph().getNode(seriesNode).getAttribute("time" + t).toString();
                    String[] a = attributes.split(", ");
                    double variable = Double.parseDouble(StringUtils.substringBefore(a[4], "]"));
                    series.getData().add(new XYChart.Data<>(t, variable));
                }
            }
        }
    }
}