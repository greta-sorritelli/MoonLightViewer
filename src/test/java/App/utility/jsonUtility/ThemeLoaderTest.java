package App.utility.jsonUtility;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ThemeLoaderTest {

    static ThemeLoader themeLoader = new JsonThemeLoader();
    static String generalTheme;
    static String graphTheme;

    @Test
    void saveToJsonTest() throws IOException {
        themeLoader.setGeneralTheme(generalTheme);
        themeLoader.setGraphTheme(graphTheme);
        themeLoader.saveToJson();
        Gson gson = new Gson();
        Reader reader = Files.newBufferedReader(Paths.get("src/main/resources/json/theme.json"));
        Type theme = new TypeToken<JsonThemeLoader>() {}.getType();
        JsonThemeLoader fromJson = gson.fromJson(reader, theme);
        assertEquals(fromJson.getGeneralTheme(),generalTheme);
        assertEquals(fromJson.getGraphTheme(),graphTheme);
    }

    @Test
    @BeforeAll
    static void getThemeFromJsonTest() throws IOException, URISyntaxException {
        ThemeLoader loader = JsonThemeLoader.getThemeFromJson();
        generalTheme = loader.getGeneralTheme();
        graphTheme = loader.getGraphTheme();
        themeLoader.setGeneralTheme("css/darkTheme.css");
        themeLoader.setGraphTheme("url('file://src/main/resources/css/graphDarkTheme.css')");
        themeLoader.saveToJson();
        ThemeLoader loader1 = JsonThemeLoader.getThemeFromJson();
        assertEquals(loader1.getGeneralTheme(),"css/darkTheme.css" );
        assertEquals(loader1.getGraphTheme(), "url('file://src/main/resources/css/graphDarkTheme.css')");
    }

    @Test
    @AfterEach
    void resetFile() throws IOException {
        themeLoader.setGeneralTheme(generalTheme);
        themeLoader.setGraphTheme(graphTheme);
        themeLoader.saveToJson();
    }
}