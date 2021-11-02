package App.GraphUtility;

import org.graphstream.graph.Graph;

/**
 * A graph with a time instant
 */
public interface TimeGraph {

    Graph getGraph();

    double getTime();

    Graph getGraphFromTime(double time);

}