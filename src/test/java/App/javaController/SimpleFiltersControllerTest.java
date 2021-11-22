package App.javaController;

import App.javaModel.filter.Filter;
import App.javaModel.filter.SimpleFilter;
import App.javaModel.graph.SimpleTimeGraph;
import App.javaModel.graph.TimeGraph;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SimpleFiltersControllerTest {

    FiltersController filtersController = SimpleFiltersController.getInstance();

    @Test
    void validationFilterTest() {
        ArrayList<Filter> filters = new ArrayList<>();
        Filter filter = new SimpleFilter("Value", "=",2.0);
        Filter filter1 = new SimpleFilter("Value", "=", 1.0);
        Filter filter2 = new SimpleFilter("Direction","<",5.0);
        filters.add(filter);
        assertThrows(IllegalArgumentException.class, () -> filtersController.validationFilter(filter1,filters));
        assertDoesNotThrow(() -> filtersController.validationFilter(filter2,filters));
        assertEquals(filter.getAttribute(), filter1.getAttribute());
        assertEquals(filter.getOperator(),filter1.getOperator());
    }

    @Test
    void checkFilterTest() {
        ArrayList<Filter> filters = new ArrayList<>();
        ArrayList<Node> nodes = new ArrayList<>();
        ArrayList<String> vector = new ArrayList<>();
        vector.add("1");
        vector.add("2");
        vector.add("2.5");
        vector.add("0.5");
        vector.add("0");
        Filter filter = new SimpleFilter("Value", "=",2.0);
        Filter filter1 = new SimpleFilter("Direction","<", 3.0);
        Graph graph = new SingleGraph("0");
        Node n = graph.addNode(String.valueOf(0));
        Node n1 = graph.addNode(String.valueOf(1));
        n.setAttribute("time"+ 0.0);

        nodes.add(n);
        TimeGraph g = new SimpleTimeGraph(graph,0.0);

    }
}