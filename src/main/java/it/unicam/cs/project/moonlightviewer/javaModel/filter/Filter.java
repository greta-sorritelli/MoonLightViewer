package it.unicam.cs.project.moonlightviewer.javaModel.filter;

/**
 * Interface that defines a filter
 *
 * @author Albanese Clarissa, Sorritelli Greta
 */
public interface Filter {

    String getAttribute();

    String getOperator();

    double getValue();
}
