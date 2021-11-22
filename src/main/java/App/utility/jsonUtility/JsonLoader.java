package App.utility.jsonUtility;

import App.javaModel.filter.Filter;
import App.javaModel.filter.FilterGroup;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This interface is responsible to define a JsonLoader.
 */
public interface JsonLoader {

    String saveToJson(ArrayList<Filter> filters, ArrayList<FilterGroup> filterGroups, String name) throws IOException;

    boolean getFromJson(String name, ArrayList<Filter> filters) throws IOException;
}
