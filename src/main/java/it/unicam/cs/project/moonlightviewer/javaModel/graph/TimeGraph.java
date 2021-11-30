package it.unicam.cs.project.moonlightviewer.javaModel.graph;

import org.graphstream.graph.Graph;

/**
 * Interface that defines a graph with a time instant
 *
 * @author Albanese Clarissa, Sorritelli Greta
 */
public interface TimeGraph {

    Graph getGraph();

    double getTime();

    Graph getGraphFromTime(double time);

}