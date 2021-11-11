package javaFX.GraphControllers;

import App.DialogUtility.DialogBuilder;
import App.GraphUtility.Filter;
import App.GraphUtility.SimpleFilter;
import App.GraphUtility.TimeGraph;
import com.google.gson.Gson;
import javaFX.ChartController;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import org.graphstream.graph.Node;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Class controller of filters
 */
public class FiltersController {

    @FXML
    TextField text;
    @FXML
    MenuButton attribute;
    @FXML
    MenuButton operator;
    @FXML
    TableView<Filter> tableFilters;
    @FXML
    TableColumn<Filter, String> attributeColumn;
    @FXML
    TableColumn<Filter, String> operatorColumn;
    @FXML
    TableColumn<Filter, Double> valueColumn;
    @FXML
    TableColumn<Filter, Void> resetColumn;

    private GraphController graphController;
    private ChartController chartController;
    private final ArrayList<Node> nodes = new ArrayList<>();

    public void injectGraphController(GraphController graphController, ChartController chartController) {
        this.graphController = graphController;
        this.chartController = chartController;
    }

    /**
     * Assigns the text of the clicked menuItem to the menuButton.
     */
    @FXML
    public void initialize() {
        attribute.getItems().forEach(menuItem -> menuItem.setOnAction(event -> attribute.setText(menuItem.getText())));
        operator.getItems().forEach(menuItem -> menuItem.setOnAction(event -> operator.setText(menuItem.getText())));
    }

    /**
     * Resets texFields and buttons.
     */
    @FXML
    private void reset() {
        attribute.setText("Attribute");
        operator.setText("Operator");
        text.clear();
    }

    /**
     * Matches the {@link Filter} fields with the columns of the table.
     */
    private void setCellValueFactory() {
        attributeColumn.setCellValueFactory(filter -> new SimpleObjectProperty<>(filter.getValue().getAttribute()));
        operatorColumn.setCellValueFactory(filter -> new SimpleObjectProperty<>(filter.getValue().getOperator()));
        valueColumn.setCellValueFactory(filter -> new SimpleObjectProperty<>(filter.getValue().getValue()));
        addButtonToTable();
    }

    /**
     * Adds a delete button for each row of table.
     */
    private void addButtonToTable() {
        Callback<TableColumn<Filter, Void>, TableCell<Filter, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Filter, Void> call(TableColumn<Filter, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button();

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Filter filter = getTableView().getItems().get(getIndex());
                            deleteFilter(filter);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty)
                            setGraphic(null);
                        else {
                            setGraphic(btn);
                            btn.setGraphic(new ImageView("images/remove.png"));
                        }
                    }
                };
            }
        };
        resetColumn.setCellFactory(cellFactory);
    }

    /**
     * Deletes a {@link Filter} from graph and filtersTable.
     *
     * @param filter filter to delete
     */
    @FXML
    private void deleteFilter(Filter filter) {
        ObservableList<Filter> filters = tableFilters.getItems();
        if (filters.size() == 1)
            resetFilters();
        else {
            filters.remove(filter);
            nodes.clear();
            filters.forEach(this::checkFilter);
        }
    }

    /**
     * Resets all filters added.
     */
    @FXML
    private void resetFilters() {
        graphController.getGraphList().forEach(timeGraph -> {
            int countNodes = timeGraph.getGraph().getNodeCount();
            for (int i = 0; i < countNodes; i++)
                timeGraph.getGraph().getNode(i).removeAttribute("ui.class");
        });
        tableFilters.getItems().clear();
        nodes.clear();
        chartController.selectAllSeries();
    }

    public void resetFiltersNewFile() {
        tableFilters.getItems().clear();
        nodes.clear();
    }

    /**
     * @return ArrayList of times, takes from all {@link TimeGraph}.
     */
    private ArrayList<Double> getTimes() {
        ArrayList<Double> times = new ArrayList<>();
        graphController.getGraphList().forEach(timeGraph -> times.add(timeGraph.getTime()));
        return times;
    }

    /**
     * Applies filter entered from user.
     */
    @FXML
    private void saveFilter() {
        try {
            if (!graphController.getGraphList().isEmpty()) {
                if (!(text.getText().equals("") || attribute.getText().equals("Attribute") || operator.getText().equals("Operator"))) {
                    double value = Double.parseDouble(text.getText());
                    Filter filter = new SimpleFilter(attribute.getText(), operator.getText(), value);
                    addFilter(filter);
                } else if (!tableFilters.getItems().isEmpty()) {
                    chartController.deselectAllSeries();
                    nodes.forEach(node -> chartController.selectOneSeries(node.getId()));
                }
            }
        } catch (Exception e) {
            reset();
            DialogBuilder dialogBuilder = new DialogBuilder();
            dialogBuilder.error("Error!", e.getMessage());
        }
    }

    /**
     * Adds filter to graph and table.
     *
     * @param filter {@link Filter} to add
     */
    private void addFilter(Filter filter) throws IOException {
        if (!tableFilters.getItems().contains(filter)) {
            validationFilter(filter);
            tableFilters.getItems().add(filter);
            checkFilter(filter);
            Gson gson = new Gson();
            Writer writer = Files.newBufferedWriter(Paths.get("src/main/resources/file.json"));
            gson.toJson(filter,writer);
            writer.close();
            reset();
            setCellValueFactory();
        } else
            throw new IllegalArgumentException("Filter already present.");
    }

    /**
     * Checks if the attribute and the operator of filter are already used.
     *
     * @param filter {@link Filter} to validate
     */
    private void validationFilter(Filter filter) {
        ObservableList<Filter> filters = tableFilters.getItems();
        filters.forEach(f -> {
            if (f.getOperator().equals(filter.getOperator()) && f.getAttribute().equals(filter.getAttribute()))
                throw new IllegalArgumentException("Operator already used.");
        });
    }

    /**
     * Based on the filter entered by the user, checks if there are nodes
     * in the graph that correspond to it.
     *
     * @param f {@link Filter} entered
     */
    private void checkFilter(Filter f) {
        boolean check;
        chartController.deselectAllSeries();
        for (TimeGraph g : graphController.getGraphList()) {
            int countNodes = g.getGraph().getNodeCount();
            for (double t : getTimes()) {
                for (int i = 0; i < countNodes; i++) {
                    Node n = g.getGraph().getNode(i);
                    if (n.getAttribute("time" + t) != null) {
                        check = getVector(n, t, f);
                        changeStyleNodes(check, n, f);
                    }
                }
            }
        }
        nodes.forEach(node -> chartController.selectOneSeries(node.getId()));
    }

    /**
     * Takes attributes of node which will be compared with the filter.
     *
     * @param n node from which take the attributes
     * @param t time of graph of node
     * @param f {@link Filter} to compare
     *
     * @return true, if there are any mismatches or false
     */
    private boolean getVector(Node n, Double t, Filter f) {
        String attributes = n.getAttribute("time" + t).toString();
        String[] vector = attributes.replaceAll("^\\s*\\[|\\]\\s*$", "").split("\\s*,\\s*");
        return checkAttribute(f.getAttribute(), f.getOperator(), f.getValue(), vector);
    }

    /**
     * Adds or removes style on nodes when the user adds a filter.
     *
     * @param check boolean to know if there are any mismatches
     * @param n     node to change style to
     * @param f     {@link Filter} added
     */
    private void changeStyleNodes(boolean check, Node n, Filter f) {
        if (check) {
            if (tableFilters.getItems().indexOf(f) == 0) {
                if (!nodes.contains(n))
                    nodes.add(n);
                n.setAttribute("ui.class", "filtered");
            } else if (!nodes.contains(n))
                n.removeAttribute("ui.class");
        } else {
            if (tableFilters.getItems().size() != 1) {
                if (nodes.contains(n)) {
                    nodes.remove(n);
                    n.removeAttribute("ui.class");
                }
            }
        }
    }

    /**
     * Check which attribute is selected.
     *
     * @param attribute attribute selected
     * @param operator  operator selected
     * @param value     value entered
     * @param vector    attributes of node
     *
     * @return true, if the node is to be showed, or false
     */
    private boolean checkAttribute(String attribute, String operator, double value, String[] vector) {
        double v;
        boolean toShow = false;
        if (attribute.equals("Direction")) {
            v = Double.parseDouble(vector[2]);
            toShow = checkOperator(operator, v, value);
        }
        if (attribute.equals("Speed")) {
            v = Double.parseDouble(vector[3]);
            toShow = checkOperator(operator, v, value);
        }
        if (attribute.equals("Value")) {
            v = Double.parseDouble(vector[4]);
            toShow = checkOperator(operator, v, value);
        }
        return toShow;
    }

    /**
     * Checks which operator is selected and if there are any mismatches.
     *
     * @param operator operator selected
     * @param v        value of node
     * @param value    value of textField
     *
     * @return true or false
     */
    private boolean checkOperator(String operator, double v, double value) {
        boolean b = false;
        switch (operator) {
            case "=":
                if (v == value) b = true;
                break;
            case ">":
                if (v > value) b = true;
                break;
            case "<":
                if (v < value) b = true;
                break;
            case ">=":
                if (v >= value) b = true;
                break;
            case "<=":
                if (v <= value) b = true;
                break;
            default:
                return false;
        }
        return b;
    }
}