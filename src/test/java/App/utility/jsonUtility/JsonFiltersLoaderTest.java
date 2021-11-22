package App.utility.jsonUtility;

import App.javaModel.filter.Filter;
import App.javaModel.filter.FilterGroup;
import App.javaModel.filter.SimpleFilter;
import App.javaModel.filter.SimpleFilterGroup;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import static App.utility.jsonUtility.Serializer.interfaceSerializer;
import static org.junit.jupiter.api.Assertions.*;

class JsonFiltersLoaderTest {

    FiltersLoader jsonFiltersLoader = new JsonFiltersLoader();

    @Test
    void saveToJsonTest() throws IOException {
        ArrayList<Filter> filters = new ArrayList<>();
        ArrayList<FilterGroup> filterGroups = new ArrayList<>();
        Filter filter = new SimpleFilter("Value", "=",0.0);
        Filter filter1 = new SimpleFilter("Direction",">", 3.0);
        Filter filter2 = new SimpleFilter("Speed","<",5.0);
        filters.add(filter);
        filters.add(filter1);
        filters.add(filter2);
        jsonFiltersLoader.saveToJson(filters,filterGroups,"Filters1");
        assertEquals("Filters1", filterGroups.get(0).getName());
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Filter.class, interfaceSerializer(SimpleFilter.class))
                .registerTypeAdapter(FilterGroup.class, interfaceSerializer(SimpleFilterGroup.class))
                .create();
        Reader reader = Files.newBufferedReader(Paths.get("src/main/resources/json/filters.json"));
        Type filterListType = new TypeToken<ArrayList<FilterGroup>>() {}.getType();
        ArrayList<FilterGroup> fromJson = gson.fromJson(reader,filterListType);
        reader.close();
        assertEquals(fromJson,filterGroups);
    }

    @Test
    void getFromJsonTest() throws IOException {
        resetFile();
        ArrayList<Filter> filters = new ArrayList<>();
        ArrayList<FilterGroup> filterGroups = new ArrayList<>();
        Filter filter = new SimpleFilter("Value", "=",0.0);
        Filter filter1 = new SimpleFilter("Direction",">", 3.0);
        Filter filter2 = new SimpleFilter("Speed","<",5.0);
        filters.add(filter);
        filters.add(filter1);
        filters.add(filter2);
        assertThrows(IOException.class, () -> jsonFiltersLoader.getFromJson("Filters1",filters));
        jsonFiltersLoader.saveToJson(filters,filterGroups,"Filters1");
        assertTrue(jsonFiltersLoader.getFromJson("Filters1",filters));
        assertFalse(jsonFiltersLoader.getFromJson("Filters2",filters));
    }

    @Test
    @AfterEach
    void resetFile() throws IOException {
        new FileWriter("src/main/resources/json/filters.json",false).close();
    }
}