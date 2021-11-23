package App.javaController;

import org.graphstream.graph.Graph;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class SimpleGraphControllerTest {

    GraphController graphController = SimpleGraphController.getInstance();

    @Test
    void createNodesVectorTest() {
    }

    @Test
    void createPositionsTest() throws IOException {
        File file = new File("src/main/resources/test/test.tra");
        graphController.createGraphFromFile(file);
        String line = "0,3,17,1,0,0,14,19,3,0,0";
        graphController.createPositions(line);
        Graph graph = graphController.getStaticGraph();
        String id = String.valueOf(0);
        String id1 = String.valueOf(1);
        assertTrue(graph.getNode(id).hasAttribute("x"));
        assertTrue(graph.getNode(id1).hasAttribute("x"));
        assertTrue(graph.getNode(id).hasAttribute("y"));
        assertTrue(graph.getNode(id1).hasAttribute("y"));
        assertEquals(graph.getNode(id).getAttribute("x"),"3");
        assertEquals(graph.getNode(id1).getAttribute("x"),"14");
        assertEquals(graph.getNode(id).getAttribute("y"),"17");
        assertEquals(graph.getNode(id1).getAttribute("y"),"19");
    }

    @Test
    void getNodesValuesTest() {
    }

    @Test
    void createGraphFromFileTest() {
    }
}