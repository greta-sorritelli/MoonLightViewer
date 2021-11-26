package App.utility.jsonUtility;

import App.javaModel.filter.Filter;
import App.javaModel.filter.FilterGroup;

import java.io.IOException;
import java.util.ArrayList;

/**
 * An Interface that defines how to load filters on or from a .json file
 *
 * @author Albanese Clarissa, Sorritelli Greta
 */
public interface FiltersLoader {

    String saveToJson(ArrayList<Filter> filters, ArrayList<FilterGroup> filterGroups, String name) throws IOException;

    boolean getFromJson(String name, ArrayList<Filter> filters) throws IOException;
}
