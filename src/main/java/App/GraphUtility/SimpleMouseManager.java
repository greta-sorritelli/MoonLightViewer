package App.GraphUtility;

import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.util.InteractiveElement;
import org.graphstream.ui.view.util.MouseManager;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.EnumSet;
import java.util.Optional;

public class SimpleMouseManager implements MouseManager {
    protected View view;
    protected GraphicGraph gGraph;
    final private EnumSet<InteractiveElement> types;
    private double time = 0;
    private Graph graph;
    protected PropertyChangeSupport propertyChangeSupport;
    private String label = "";

    public SimpleMouseManager() {
        this(EnumSet.of(InteractiveElement.NODE, InteractiveElement.SPRITE));
    }

    public SimpleMouseManager(EnumSet<InteractiveElement> types) {
        this.types = types;
    }

    public SimpleMouseManager(Graph graph, Double time) {
        this(EnumSet.of(InteractiveElement.NODE, InteractiveElement.SPRITE));
        propertyChangeSupport = new PropertyChangeSupport(this);
        this.time = time;
        this.graph = graph;
    }

    public void setLabel(String text) {
        String oldText = this.label;
        this.label = text;
        propertyChangeSupport.firePropertyChange("LabelProperty", oldText, text);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void init(GraphicGraph graph, View view) {
        this.view = view;
        this.gGraph = graph;
        view.addListener(MouseEvent.MOUSE_PRESSED, mousePressed);
        view.addListener(MouseEvent.MOUSE_RELEASED, mouseRelease);
        view.addListener(MouseEvent.MOUSE_CLICKED, mouseClicked);
    }

    protected void mouseButtonPress(MouseEvent event) {
        view.requireFocus();
        if (!event.isShiftDown()) {
            gGraph.nodes().filter(n -> n.hasAttribute("ui.selected")).forEach(n -> n.removeAttribute("ui.selected"));
            gGraph.sprites().filter(s -> s.hasAttribute("ui.selected")).forEach(s -> s.removeAttribute("ui.selected"));
            gGraph.edges().filter(e -> e.hasAttribute("ui.selected")).forEach(e -> e.removeAttribute("ui.selected"));
        }
    }

    protected void mouseButtonRelease(MouseEvent event,
                                      Iterable<GraphicElement> elementsInArea) {
        for (GraphicElement element : elementsInArea) {
            if (!element.hasAttribute("ui.selected"))
                element.setAttribute("ui.selected");
        }
    }

    protected void mouseButtonPressOnElement(GraphicElement element,
                                             MouseEvent event) {
        view.freezeElement(element, true);
        if (event.getButton() == MouseButton.SECONDARY) {
            element.setAttribute("ui.selected");
        } else {
            element.setAttribute("ui.clicked");
        }
    }

    protected void elementMoving(GraphicElement element, MouseEvent event) {
        view.moveElementAtPx(element, event.getX(), event.getY());
    }

    protected void mouseButtonReleaseOffElement(GraphicElement element,
                                                MouseEvent event) {
        view.freezeElement(element, false);
        if (event.getButton() != MouseButton.SECONDARY) {
            element.removeAttribute("ui.clicked");
        }
    }

    protected GraphicElement curElement;

    protected double x1, y1;

    EventHandler<MouseEvent> mousePressed = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent e) {
            curElement = view.findGraphicElementAt(types, e.getX(), e.getY());
            if (curElement != null) {
                mouseButtonPressOnElement(curElement, e);
            } else {
                x1 = e.getX();
                y1 = e.getY();
                mouseButtonPress(e);
                view.beginSelectionAt(x1, y1);
            }
        }
    };

    EventHandler<MouseEvent> mouseDragged = event -> {
    };

    EventHandler<MouseEvent> mouseRelease = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if (curElement != null) {
                mouseButtonReleaseOffElement(curElement, event);
                curElement = null;
            } else {
                double x2 = event.getX();
                double y2 = event.getY();
                double t;

                if (x1 > x2) {
                    t = x1;
                    x1 = x2;
                    x2 = t;
                }
                if (y1 > y2) {
                    t = y1;
                    y1 = y2;
                    y2 = t;
                }

                mouseButtonRelease(event, view.allGraphicElementsIn(types, x1, y1, x2, y2));
                view.endSelectionAt(x2, y2);
            }
        }
    };

    @Override
    public void release() {
        view.removeListener(MouseEvent.MOUSE_PRESSED, mousePressed);
        view.removeListener(MouseEvent.MOUSE_RELEASED, mouseRelease);
    }

    @Override
    public EnumSet<InteractiveElement> getManagedTypes() {
        return types;
    }


    public String mouseClicked(MouseEvent me, double time) {
        if (me.getSource().getClass().equals(Node.class)) {
            Node n = (Node) me.getSource();
            n.getAttribute("time" + time);
        }
        return null;
    }

    EventHandler<MouseEvent> mouseClicked = new EventHandler<>() {
        @Override
        public void handle(MouseEvent e) {
            curElement = view.findGraphicElementAt(types, e.getX(), e.getY());
            if (curElement != null) {
                mouseButtonClickOnElement(curElement, e);
            }
        }
    };


    protected void mouseButtonClickOnElement(GraphicElement element,
                                             MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            element.setAttribute("ui.clicked");
            Optional<Node> n1 = gGraph.nodes().filter(n -> n.hasAttribute("ui.clicked")).findFirst();
            if (n1.isPresent()) {
                Node n = graph.getNode(n1.get().getId());
                String attribute = n.getAttribute("time" + this.time).toString();
                String s = attribute.substring(1, attribute.length() - 1);
                String[] list = s.split(", ");
                String newLabel = "Node " + n.getId() + " attributes:  x: " + list[0] + ", y: " + list[1] + ", direction: " + list[2] + ", speed: " + list[3] + ", v: " + list[4];
                setLabel(newLabel);
            }
            element.removeAttribute("ui.clicked");
        }
    }

}

