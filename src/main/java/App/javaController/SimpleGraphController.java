package App.javaController;

import App.javaModel.graph.GraphType;
import App.javaModel.graph.SimpleTimeGraph;
import App.javaModel.graph.TimeGraph;
import org.apache.commons.lang3.StringUtils;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SimpleGraphController implements GraphController{

    private static SimpleGraphController instance = null;
    private List<TimeGraph> graphList;
    private Graph staticGraph;
    private int idGraph = 0;
    private int totNodes = 0;

    private SimpleGraphController() {
    }

    public static SimpleGraphController getInstance() {
        if (instance == null)
            instance = new SimpleGraphController();
        return instance;
    }


    public List<TimeGraph> getGraphList() {
        return graphList;
    }

    public int getTotNodes() {
        return totNodes;
    }

    public Graph getStaticGraph() {
        return staticGraph;
    }

    public void setGraphList(List<TimeGraph> graphList) {
        this.graphList = graphList;
    }

    /**
     * Creates vectors for every node in a single instant
     *
     * @param line a string of a time instant with all info about nodes
     */
    public void createNodesVector(String line) {
        int node = 0;
        ArrayList<ArrayList<String>> nodes = new ArrayList<>();
        String[] elements = line.split(",");
        double time = Double.parseDouble(elements[0]);
        int index = 1;
        while (index < elements.length) {
            ArrayList<String> vector = new ArrayList<>();
            for (int i = 1; i <= 5; i++) {
                vector.add(elements[index]);
                index++;
            }
            Optional<TimeGraph> t = graphList.stream().filter(graph -> graph.getGraphFromTime(time) != null).findFirst();
            if (t.isPresent()) {
                t.get().getGraph().getNode(node).setAttribute("time" + time, vector);
            }
            node++;
            nodes.add(vector);
        }
        addPositionsDynamicGraph(elements, nodes);
    }

    public void createPositions(String line) {
        String[] array = line.split(",");
        int index = 1;
        for (int i = 0; i < staticGraph.getNodeCount(); i++){
            staticGraph.getNode(String.valueOf(i)).setAttribute("x", array[index]);
            staticGraph.getNode(String.valueOf(i)).setAttribute("y", array[++index]);
            index += 4;
        }
    }

//    public void getNodesValues(String line) {
//        String[] lineToArray = line.split(", ");
//        int index = 0;
//        for (int node = 0; node < this.staticGraph.getNodeCount(); node++) {
//            if (index < lineToArray.length) {
//                ArrayList<String> attributesOneNode = new ArrayList<>();
//                for (int i = 0; i <= 2; i++) {
//                    attributesOneNode.add(lineToArray[index]);
//                    index++;
//                }
//                Node n = this.staticGraph.getNode(String.valueOf(node));
//                n.setAttribute("Attributes", attributesOneNode);
//                addPositionsStaticGraph(n, attributesOneNode);
//            }
//        }
//    }

    private void addPositionsStaticGraph(Node node, ArrayList<String> elements) {
        node.setAttribute("x", elements.get(0));
        node.setAttribute("y", elements.get(1));
    }

    /**
     * Gets positions from the .csv file and adds them to the node coordinates
     */
    private void addPositionsDynamicGraph(String[] elements, ArrayList<ArrayList<String>> nodes) {
        for (TimeGraph g : graphList) {
            if (g.getTime() == Double.parseDouble(elements[0])) {
                for (int i = 0; i < nodes.size(); i++) {
                    if (g.getGraph().getNode(String.valueOf(i)) != null) {
                        g.getGraph().getNode(String.valueOf(i)).setAttribute("x", nodes.get(i).get(0));
                        g.getGraph().getNode(String.valueOf(i)).setAttribute("y", nodes.get(i).get(1));
                    }
                }
            }
        }
    }

    /**
     * Create a graph from a file
     *
     * @param file file to read
     */
    public GraphType createGraphFromFile(File file) throws IOException {
        idGraph = 0;
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = br.readLine();
        if (line.contains("LOCATIONS")) {
            totNodes = Integer.parseInt(StringUtils.substringAfterLast(line, "LOCATIONS "));
            if ((line = br.readLine()) != null && line.contains(",")) {
                staticGraph = new MultiGraph("id" + idGraph);
                idGraph++;
                staticGraph(line, br, staticGraph, totNodes);
                return GraphType.STATIC;
            } else if (!line.contains(",")) {
                dynamicGraph(line, br, totNodes);
                return GraphType.DYNAMIC;
            }
        }
        return null;
    }

    /**
     * Builds a dynamic graph from a file
     *
     * @param line     line to read
     * @param br       a {@link BufferedReader} to read the file
     * @param totNodes total number of nodes
     */
    private void dynamicGraph(String line, BufferedReader br, int totNodes) throws IOException {
        double time = Double.parseDouble(line);
        ArrayList<String> linesEdges = new ArrayList<>();
        while (true) {
            if ((line = br.readLine()) != null) {
                if (!line.contains(",")) {
                    instantGraph(time, linesEdges, totNodes);
                    linesEdges.clear();
                    time = Double.parseDouble(line);
                } else {
                    linesEdges.add(line);
                }
            } else {
                instantGraph(time, linesEdges, totNodes);
                linesEdges.clear();
                break;
            }
        }
    }

    /**
     * Creates a single {@link TimeGraph} in a time instant
     *
     * @param time       instant
     * @param linesEdges line of the file that contains the edge between two nodes
     * @param totNodes   total number of nodes
     */
    private void instantGraph(double time, ArrayList<String> linesEdges, int totNodes) {
        Graph graph = new MultiGraph("id" + idGraph);
        idGraph++;
        createNodes(graph, totNodes);
        for (String l : linesEdges) {
            createEdge(l, graph);
        }
        TimeGraph tg = new SimpleTimeGraph(graph, time);
        graphList.add(tg);
    }

    /**
     * Builds a static graph from a file
     *
     * @param line     line to read
     * @param br       a {@link BufferedReader} to read the file
     * @param graph    graph in which to add nodes and edges
     * @param totNodes total number of nodes
     */
    private void staticGraph(String line, BufferedReader br, Graph graph, int totNodes) throws IOException {
        createEdge(line, graph, totNodes);
        while ((line = br.readLine()) != null) {
            createEdge(line, graph);
        }
    }

    /**
     * Creates nodes and then an edge between two of them
     */
    private void createEdge(String line, Graph graph, int totNodes) {
        createNodes(graph, totNodes);
        createEdge(line, graph);
    }

    /**
     * Create and edge between two nodes
     */
    private void createEdge(String line, Graph graph) {
        String[] elements = line.split(",");
        String vertex1 = elements[0];
        String vertex2 = elements[1];
        String edge = elements[2];
        boolean exist = graph.edges().anyMatch(edge1 -> (edge1.getSourceNode().equals(graph.getNode(vertex1)) || edge1.getSourceNode().equals(graph.getNode(vertex2))) && (edge1.getTargetNode().equals(graph.getNode(vertex2)) || edge1.getTargetNode().equals(graph.getNode(vertex1))));
        Edge e = graph.addEdge("id" + idGraph, graph.getNode(vertex1), graph.getNode(vertex2));
        idGraph++;
//        e.setAttribute("ui.label", edge);
        if (exist)
            e.setAttributes(Map.of(
//                    "ui.label", edge,
                    "ui.class", "multiple"
            ));
//        else
//            e.setAttribute("ui.label", edge);
    }

    /**
     * Creates all the nodes of a graph
     *
     * @param graph graph in which to add nodes
     * @param tot   total number of nodes to create
     */
    private void createNodes(Graph graph, int tot) {
        int i = 0;
        while (i < tot) {
            Node n = graph.addNode(String.valueOf(i));
            n.setAttribute("ui.label", i);
            i++;
        }
    }
}