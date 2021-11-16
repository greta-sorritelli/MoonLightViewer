package App.utility.jsonUtility;

import App.javaModel.filter.Filter;
import App.javaModel.filter.FilterGroup;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This interface is responsible to define a JsonLoader.
 */
public interface JsonLoader {

    void saveToJson(ArrayList<Filter> filters, ArrayList<FilterGroup> filterGroups, String name, String theme) throws IOException;

    boolean loadFromJson(String name, TableView<Filter> tableView) throws IOException;
}
