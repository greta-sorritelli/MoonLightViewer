package App.javaController;

import App.javaModel.filter.Filter;
import App.javaModel.graph.TimeGraph;
import org.graphstream.graph.Node;

import java.util.ArrayList;

public interface FiltersController {

    void validationFilter(Filter filter, ArrayList<Filter> filters);

    void checkFilter(Filter f, ArrayList<Filter> filters, ArrayList<Node> nodes, TimeGraph g, ArrayList<Double> times);
}
