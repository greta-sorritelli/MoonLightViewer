package App.GraphUtility;

import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.util.InteractiveElement;
import org.graphstream.ui.view.util.MouseManager;

import java.util.EnumSet;

public class SimpleMouseManager implements MouseManager {
    protected View view;
    protected GraphicGraph graph;
    final private EnumSet<InteractiveElement> types;

    public SimpleMouseManager() {
        this(EnumSet.of(InteractiveElement.NODE,InteractiveElement.SPRITE));
    }

    public SimpleMouseManager(EnumSet<InteractiveElement> types) {
        this.types = types;
    }

    public void init(GraphicGraph graph, View view) {
        this.view = view;
        this.graph = graph;
        view.addListener(MouseEvent.MOUSE_PRESSED, mousePressed);
        view.addListener(MouseEvent.MOUSE_RELEASED, mouseRelease);
    }

    protected void mouseButtonPress(MouseEvent event) {
        view.requireFocus();
        if (!event.isShiftDown()) {
            graph.nodes().filter(n -> n.hasAttribute("ui.selected")).forEach(n -> n.removeAttribute("ui.selected"));
            graph.sprites().filter(s -> s.hasAttribute("ui.selected")).forEach(s -> s.removeAttribute("ui.selected"));
            graph.edges().filter(e -> e.hasAttribute("ui.selected")).forEach(e -> e.removeAttribute("ui.selected"));
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

    EventHandler<MouseEvent> mouseDragged = event -> {};

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

                mouseButtonRelease(event, view.allGraphicElementsIn(types,x1, y1, x2, y2));
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
}


