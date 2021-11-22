package App.javaModel.chart;

import App.javaModel.graph.SimpleTimeGraph;
import App.javaModel.graph.TimeGraph;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.junit.jupiter.api.Test;

import java.sql.Time;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SimpleChartBuilderTest {




    @Test
    void getSeriesFromNodesTest() {
        ArrayList<TimeGraph> list = new ArrayList<>();
        Graph graph = new SingleGraph("0");
        Node n = graph.addNode(String.valueOf(0));
        ArrayList<String> vector = new ArrayList<>();
        vector.add("1");
        vector.add("2");
        vector.add("2.5");
        vector.add("0.5");
        vector.add("0");
        n.setAttribute("time" + 0.0, vector);
        TimeGraph t1 = new SimpleTimeGraph(graph,0.0);
        Graph graph2 = new SingleGraph("0");
        Node n2 = graph.addNode(String.valueOf(0));
        ArrayList<String> vector2 = new ArrayList<>();
        vector.add("1");
        vector.add("2");
        vector.add("2.5");
        vector.add("0.5");
        vector.add("0");
        n.setAttribute("time" + 0.0, vector);
        TimeGraph t2 = new SimpleTimeGraph(graph2,0.0);
        list.add(t1);
        list.add(t2);
        SimpleChartBuilder simpleChartBuilder = new SimpleChartBuilder();
        simpleChartBuilder.getSeriesFromNodes(list);
    }

    @Test
    void getSeriesFromStaticGraphTest() {
    }

    @Test
    void addLineDataTest() {

    }
}