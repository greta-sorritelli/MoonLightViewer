package App.utility.jsonUtility;

import App.javaModel.filter.Filter;
import App.javaModel.filter.FilterGroup;
import App.javaModel.filter.SimpleFilter;
import App.javaModel.filter.SimpleFilterGroup;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;

import static App.utility.jsonUtility.Serializer.interfaceSerializer;

/**
 * This class implements the {@link FiltersLoader} interface and is responsible to save and import filters.
 */
public class JsonFiltersLoader implements FiltersLoader {

    /**
     * Saves filters in a Json file.
     *
     * @param filters       filters to save
     * @param filterGroups  filterGroups already present
     * @param name          name of filters to save
     * @return              a string for dialog
     */
    public String saveToJson(ArrayList<Filter> filters, ArrayList<FilterGroup> filterGroups, String name) throws IOException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Filter.class, interfaceSerializer(SimpleFilter.class))
                .registerTypeAdapter(FilterGroup.class, interfaceSerializer(SimpleFilterGroup.class))
                .create();
        File file = new File("src/main/resources/json/filters.json");
        if (file.length() != 0)
            readJsonFile(filterGroups,gson);
        return writeJsonFile(gson,filters,filterGroups,name);
    }

    /**
     * Reads an arrayList of filters in the Json File.
     *
     * @param filterGroups  filterGroups already present
     * @param gson          gson instance
     */
    private void readJsonFile(ArrayList<FilterGroup> filterGroups, Gson gson) throws IOException {
        ArrayList<FilterGroup> fromJson = getListFromJson(gson);
        if(fromJson != null) {
            if (!filterGroups.toString().equals(fromJson.toString()))
                filterGroups.addAll(fromJson);
        }
    }

    /**
     * Takes filtersGroup from Json file
     *
     * @param gson gson instance
     */
    private ArrayList<FilterGroup> getListFromJson(Gson gson) throws IOException {
        Reader reader = Files.newBufferedReader(Paths.get("src/main/resources/json/filters.json"));
        Type filterListType = new TypeToken<ArrayList<FilterGroup>>() {}.getType();
        ArrayList<FilterGroup> fromJson = gson.fromJson(reader,filterListType);
        reader.close();
        return fromJson;
    }

    /**
     * Writes filters in a Json file.
     *
     * @param gson         gson instance
     * @param filters      filters to write
     * @param filterGroups filterGroups already present
     * @param name         name of filters to save
     * @return             a string for dialog
     */
    private String writeJsonFile(Gson gson, ArrayList<Filter> filters,ArrayList<FilterGroup> filterGroups, String name) throws IOException {
        String checkGroup;
        Writer writer = Files.newBufferedWriter(Paths.get("src/main/resources/json/filters.json"));
        FilterGroup filterGroup = new SimpleFilterGroup(name, filters);
        boolean filterGroupPresent = filterGroups.stream().anyMatch(f -> f.equals(filterGroup));
        boolean filtersPresent = filterGroups.stream().anyMatch(f -> f.getFilters().equals(filterGroup.getFilters()));
        boolean namePresent = filterGroups.stream().anyMatch(f -> f.getName().equals(name));
        if(!(filtersPresent || namePresent || filterGroupPresent)) {
            filterGroups.add(filterGroup);
            checkGroup = name + " filter saved with success!";
        } else
            checkGroup = checkFilterGroup(namePresent,filterGroupPresent);
        gson.toJson(filterGroups, writer);
        writer.close();
        return checkGroup;
    }

    /**
     * Check the filterGroup when user save filters on file.
     * @param namePresent          boolean that say if the name of filters is already present
     * @param filterGroupPresent   boolean that say if the filterGroup is already present
     * @return                     a string for dialog
     */
    private String checkFilterGroup(boolean namePresent, boolean filterGroupPresent){
        String checkGroup;
        if(filterGroupPresent)
            checkGroup = "Name and filters already present!";
        else if(namePresent)
            checkGroup ="Name already present!";
        else
            checkGroup ="Filters already present!";
        return checkGroup;
    }

    /**
     * Takes the filtersGroup searched by the user from a Json file.
     *
     * @param name      name of filters
     * @param filters   list of filters
     * @return  true if file contains filters, else false
     */
    public boolean getFromJson(String name, ArrayList<Filter> filters) throws IOException {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Filter.class, interfaceSerializer(SimpleFilter.class))
                    .registerTypeAdapter(FilterGroup.class, interfaceSerializer(SimpleFilterGroup.class))
                    .create();
            ArrayList<FilterGroup> fromJson = getListFromJson(gson);
            if(fromJson != null){
                Optional<FilterGroup> filterGroup = fromJson.stream().filter(f -> f.getName().equals(name)).findFirst();
                filterGroup.ifPresent(group -> filters.addAll(group.getFilters()));
                return filterGroup.isPresent();
            } else
                throw new IOException("File is empty");
    }
}