package javaFX;

import App.DialogUtility.DialogBuilder;
import App.GraphUtility.TimeGraph;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import org.graphstream.graph.Node;
import java.util.ArrayList;

/**
 * Class controller of filters
 */
public class FiltersController {

    @FXML
    TextField v;
    @FXML
    TextField minor;
    @FXML
    TextField greater;

    private GraphController graphController;

    public void injectGraphController(GraphController graphController) {
        this.graphController = graphController;
    }

    private String getTextField(TextField t) {
        return t.getText();
    }

    /**
     * Resets textFields of minor and greater.
     */
    @FXML
    private void resetTextSearch() {
        minor.clear();
        greater.clear();
    }

    /**
     * Resets textField of value.
     */
    @FXML
    private void resetTextSave() {
        v.clear();
    }

    /**
     * Resets all filters added.
     */
    @FXML
    private void resetFilter() {
        for (TimeGraph g : graphController.getGraphList()) {
            int countNodes = g.getGraph().getNodeCount();
            for (int i = 0; i < countNodes; i++)
                g.getGraph().getNode(i).removeAttribute("ui.class");
        }
    }

    /**
     * @return ArrayList of times, takes from the Radiobutton list.
     */
    private ArrayList<Double> getTimes() {
        ArrayList<Double> times = new ArrayList<>();
        double time;
        for (RadioButton radioButton : graphController.getVariables()) {
            time = Double.parseDouble(radioButton.getText());
            times.add(time);
        }
        return times;
    }

    /**
     * Applies filters to nodes at all times on graph.
     */
    @FXML
    private void saveFilter() {
        try {
            resetFilter();
            for (TimeGraph g : graphController.getGraphList()) {
                int countNodes = g.getGraph().getNodeCount();
                getNodesVector(countNodes, g, getTimes());
            }
        } catch (Exception e) {
            DialogBuilder dialogBuilder = new DialogBuilder();
            dialogBuilder.error("Error!", e.getMessage());
        }
    }

    /**
     * Takes all attributes of a node for each instant.
     *
     * @param countNodes number of nodes in a graph
     * @param g          graph
     * @param times      instants of time
     */
    private void getNodesVector(int countNodes, TimeGraph g, ArrayList<Double> times) {
        for (int i = 0; i < countNodes; i++) {
            Node n = g.getGraph().getNode(i);
            for (double t : times) {
                if (n.getAttribute("time" + t) != null) {
                    String attributes = n.getAttribute("time" + t).toString();
                    String[] vector = attributes.replaceAll("^\\s*\\[|\\]\\s*$", "").split("\\s*,\\s*");
                    colorNode(vector, n);
                }
            }
        }
    }

    /**
     * Applies the style of node when it's filtered.
     *
     * @param vector attributes of node
     * @param n      node of graph
     */
    private void colorNode(String[] vector, Node n) {
        if (!getTextField(v).equals("")) {
            if (!getTextField(v).equals("")) {
                if (vector[4].equals(getTextField(v)))
                    n.setAttribute("ui.class", "filtered");
            }
        }
        double value = Double.parseDouble(vector[4]);
        getFilter(value, n);
    }

    /**
     * Checks which textFields have been entered and applies the style
     * to the nodes based on them.
     *
     * @param value double value of textField value
     * @param n     node of graph
     */
    private void getFilter(double value, Node n) {
        double textMinor, textGreater;
        if (!(getTextField(minor).equals("") || getTextField(greater).equals(""))) {
            textMinor = Double.parseDouble(getTextField(minor));
            textGreater = Double.parseDouble(getTextField(greater));
            if (value < textMinor && value > textGreater)
                n.setAttribute("ui.class", "filtered");
        }
        if (getTextField(minor).equals("") && !getTextField(greater).equals("")) {
            textGreater = Double.parseDouble(getTextField(greater));
            if (value > textGreater)
                n.setAttribute("ui.class", "filtered");
        }
        if (getTextField(greater).equals("") && !getTextField(minor).equals("")) {
            textMinor = Double.parseDouble(getTextField(minor));
            if (value < textMinor)
                n.setAttribute("ui.class", "filtered");
        }
    }
}