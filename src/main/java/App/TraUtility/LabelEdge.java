package App.TraUtility;

import org.jgrapht.graph.DefaultEdge;

public class LabelEdge extends DefaultEdge {

    private String label;

    public LabelEdge(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return "(" + getSource() + ": " + getTarget() + ": " + label + ")";
    }
}
