package App.javaController;

import App.javaModel.Filter.Filter;
import javafx.collections.ObservableList;
import org.graphstream.graph.Node;

import java.util.ArrayList;

public interface Controller {

    void validationFilter(Filter filter, ObservableList<Filter> filters);

    void checkFilter(Filter f, ObservableList<Filter> filters, ArrayList<Node> nodes);
}
