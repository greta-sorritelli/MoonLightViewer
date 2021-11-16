package App.javaController;

import App.javaModel.filter.Filter;
import App.javaModel.graph.TimeGraph;
import javafx.collections.ObservableList;
import org.graphstream.graph.Node;

import java.util.ArrayList;

public interface Controller {

    void validationFilter(Filter filter, ObservableList<Filter> filters);

    void checkFilter(Filter f, ObservableList<Filter> filters, ArrayList<Node> nodes, TimeGraph g, ArrayList<Double> times);
}
