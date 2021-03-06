package it.unicam.cs.project.moonlightviewer.utility.jsonUtility;

import it.unicam.cs.project.moonlightviewer.javaModel.filter.Filter;
import it.unicam.cs.project.moonlightviewer.javaModel.filter.FilterGroup;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Interface that defines how to load filters on/from a .json file
 *
 * @author Albanese Clarissa, Sorritelli Greta
 */
public interface FiltersLoader {

    String saveToJson(ArrayList<Filter> filters, ArrayList<FilterGroup> filterGroups, String name) throws IOException;

    boolean getFromJson(String name, ArrayList<Filter> filters) throws IOException;
}
