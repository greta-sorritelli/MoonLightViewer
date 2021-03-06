package it.unicam.cs.project.moonlightviewer.javaModel.chart;

import it.unicam.cs.project.moonlightviewer.javaModel.graph.TimeGraph;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Class that implements the {@link ChartBuilder} interface and builds a simple chart from a {@link TimeGraph}
 *
 * @author Albanese Clarissa, Sorritelli Greta
 */
public class SimpleChartBuilder implements ChartBuilder {

    private final ArrayList<ArrayList<String>> attributes = new ArrayList<>();

    private final ArrayList<Series<Number, Number>> listLinear = new ArrayList<>();

    private final ArrayList<Series<Number, Number>> listLog = new ArrayList<>();

    public ArrayList<Series<Number, Number>> getListLinear() {
        return listLinear;
    }

    public ArrayList<Series<Number, Number>> getListLog() {
        return listLog;
    }

    public ArrayList<ArrayList<String>> getAttributes() {
        return attributes;
    }

    public void addAttributes(String[] attributes) {
        ArrayList<String> a = new ArrayList<>(Arrays.stream(attributes).toList());
        this.attributes.add(a);
    }

    /**
     * Clears the lists of series
     */
    @Override
    public void clearList() {
        listLinear.clear();
        listLog.clear();
    }

    /**
     * Reads a file and create series
     *
     * @param file file to read
     *
     * @return ArrayList of series
     */
    @Override
    public ArrayList<Series<Number, Number>> createSeriesForConstantChart(File file) throws IOException {
        FileInputStream fIn = new FileInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(fIn));
        String line = br.readLine();
        String[] attributes = line.split(", ");
        int columns = ((attributes.length - 1) / 5) + 1;
        int rows = calculateRows(br);
        fIn.getChannel().position(0);
        br = new BufferedReader(new InputStreamReader(fIn));
        Double[][] matrix = new Double[rows][columns];
        populateMatrix(br, columns, matrix);
        return createSeriesFromMatrix(matrix);
    }

    /**
     * Builds a matrix for the values of a constant chart
     *
     * @param br      bufferedReader
     * @param columns columns of matrix
     * @param matrix  matrix
     */
    private void populateMatrix(BufferedReader br, int columns, Double[][] matrix) throws IOException {
        String line;
        int rows;
        rows = 0;
        while ((line = br.readLine()) != null) {
            String[] array = line.split(", ");
            int index = 0;
            for (int i = 0; i < columns; i++) {
                matrix[rows][i] = Double.valueOf(array[index]);
                index += 5;
            }
            rows++;
        }
    }

    private int calculateRows(BufferedReader br) throws IOException {
        int rows = 0;
        do
            rows++;
        while (br.readLine() != null);
        return rows;
    }

    /**
     * Creates series from a matrix of values
     *
     * @param matrix matrix
     *
     * @return ArrayList of series
     */
    private ArrayList<Series<Number, Number>> createSeriesFromMatrix(Double[][] matrix) {
        ArrayList<Series<Number, Number>> list = new ArrayList<>();
        //per ogni colonna
        for (int column = 0; column < matrix[0].length - 1; column++) {
            Series<Number, Number> series = new Series<>();
            series.setName("Node " + column);
            //per ogni riga
            for (int row = 0; row < matrix.length; row++) {
                if (row != 0)
                    series.getData().add(new Data<>(matrix[row][0], matrix[row - 1][column + 1]));
                series.getData().add(new Data<>(matrix[row][0], matrix[row][column + 1]));
            }
            list.add(series);
        }
        return list;
    }

    /**
     * Gets all nodes info and create a relative series for each
     *
     * @param timeGraph a {@link TimeGraph}
     *
     * @return a list of all series
     */
    public List<Series<Number, Number>> getSeriesFromNodes(List<TimeGraph> timeGraph) {
        List<Series<Number, Number>> series = new ArrayList<>();
        int totSeries = timeGraph.get(0).getGraph().getNodeCount();
        ArrayList<Double> times = new ArrayList<>();
        timeGraph.forEach(timeGraph1 -> times.add(timeGraph1.getTime()));
        int node = 0;
        while (node < totSeries) {
            Series<Number, Number> seriesX = new Series<>();
            seriesX.setName("Node " + node);
            addData(seriesX, times, timeGraph, node);
            series.add(seriesX);
            node++;
        }
        return series;
    }

    /**
     * Creates series of a chart from a file of a static graph
     *
     * @param line  line to read
     * @param list  list of series
     * @param first boolean
     *
     * @return ArrayList of series
     */
    @Override
    public ArrayList<Series<Number, Number>> getSeriesFromStaticGraph(String line, ArrayList<Series<Number, Number>> list, boolean first) {
        int index = 5;
        int node = 0;
        Series<Number, Number> series = null;
        String[] attributes = line.split(", ");
        double time = Double.parseDouble(attributes[0]);
        checkFirst(first, attributes);
        for (int i = 0; i < 5; i++) {
            int finalNode = node;
            series = getSeries(list, node, series, finalNode);
            double variable = Double.parseDouble(StringUtils.substringBefore(attributes[index], "]"));
            series.getData().add(new Data<>(time, variable));
            node++;
            index += 5;
        }
        return list;
    }

    /**
     * Creates and returns a series if it doesn't exist or returns the existing series
     *
     * @param list      list of series
     * @param node      id of node
     * @param series    series
     * @param finalNode id of final node
     *
     * @return series
     */
    private Series<Number, Number> getSeries(ArrayList<Series<Number, Number>> list, int node, Series<Number, Number> series, int finalNode) {
        if (list.stream().noneMatch(numberNumberSeries -> numberNumberSeries.getName().equals("Node " + finalNode))) {
            series = new Series<>();
            series.setName("Node " + node);
            list.add(series);
        } else {
            Optional<Series<Number, Number>> series1 = list.stream().filter(numberNumberSeries -> numberNumberSeries.getName().equals("Node " + finalNode)).findFirst();
            if (series1.isPresent())
                series = series1.get();
        }
        return series;
    }

    /**
     * Checks if the attributes have already been added
     *
     * @param first      boolean
     * @param attributes attributes of node
     */
    private void checkFirst(boolean first, String[] attributes) {
        if (first) {
            ArrayList<String> timeAttributes = new ArrayList<>(Arrays.asList(attributes));
            this.attributes.add(timeAttributes);
        }
    }

    /**
     * Adds data to the chart from an array of attributes
     *
     * @param series     list of series
     * @param attributes attributes of node
     */
    @Override
    public void addLineData(List<Series<Number, Number>> series, String[] attributes) {
        int index = 5;
        int node = 0;
        Series<Number, Number> numberSeries = null;
        double time = Double.parseDouble(attributes[0]);
        for (int i = 0; i < 5; i++) {
            int finalNode = node;
            Optional<Series<Number, Number>> series1 = series.stream().filter(numberNumberSeries -> numberNumberSeries.getName().equals("Node " + finalNode)).findFirst();
            if (series1.isPresent())
                numberSeries = series1.get();
            double variable = Double.parseDouble(StringUtils.substringBefore(attributes[index], "]"));
            numberSeries.getData().add(new Data<>(time, variable));
            node++;
            index += 5;
        }
    }

    /**
     * Add all data of a series
     *
     * @param series     series to add data
     * @param times      list of time instants
     * @param timeGraph  list of {@link TimeGraph}
     * @param seriesNode id of a node in a graph
     */
    private void addData(Series<Number, Number> series, ArrayList<Double> times, List<TimeGraph> timeGraph, int seriesNode) {
        for (double t : times) {
            for (TimeGraph graph : timeGraph) {
                if (graph.getTime() == t) {
                    String attributes = graph.getGraph().getNode(seriesNode).getAttribute("time" + t).toString();
                    String[] a = attributes.split(", ");
                    double variable = Double.parseDouble(StringUtils.substringBefore(a[4], "]"));
                    series.getData().add(new Data<>(t, variable));
                }
            }
        }
    }
}