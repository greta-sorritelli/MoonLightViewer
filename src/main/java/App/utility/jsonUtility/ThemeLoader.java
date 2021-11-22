package App.utility.jsonUtility;

import java.io.IOException;

public interface ThemeLoader {

    String getGeneralTheme();

    void setGeneralTheme(String generalTheme);

    String getGraphTheme();

    void setGraphTheme(String graphTheme);

    void saveToJson() throws IOException;
}
