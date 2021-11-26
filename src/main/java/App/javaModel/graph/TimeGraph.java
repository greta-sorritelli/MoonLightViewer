package App.javaModel.graph;

import org.graphstream.graph.Graph;

/**
 * A graph with a time instant
 *
 * @author Albanese Clarissa, Sorritelli Greta
 */
public interface TimeGraph {

    Graph getGraph();

    double getTime();

    Graph getGraphFromTime(double time);

}