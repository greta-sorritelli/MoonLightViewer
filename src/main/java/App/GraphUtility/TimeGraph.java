package App.GraphUtility;

import org.graphstream.graph.Graph;

public class TimeGraph {

    private final Graph graph;
    private final double time;

    public TimeGraph(Graph graph, double time) {
        this.graph = graph;
        this.time = time;
    }

    public Graph getGraph() {
        return graph;
    }

    public double getTime() {
        return time;
    }

}
