package it.unicam.cs.project.moonlightviewer.javaController;

import it.unicam.cs.project.moonlightviewer.javaModel.graph.GraphType;
import it.unicam.cs.project.moonlightviewer.javaModel.graph.TimeGraph;
import org.graphstream.graph.Graph;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Interface that defines a controller for graphs
 *
 * @author Albanese Clarissa, Sorritelli Greta
 */
public interface GraphController {

    List<TimeGraph> getGraphList();

    int getTotNodes();

    Graph getStaticGraph();

    void setGraphList(List<TimeGraph> graphList);

    void createNodesVector(String line);

    void createPositions(String line);

    GraphType createGraphFromFile(File file) throws IOException;
}
