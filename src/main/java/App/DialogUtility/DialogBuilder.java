package App.DialogUtility;

import javafx.scene.control.Alert;

/**
 * Class that implements {@link Dialog} to show dialog window
 */
public class DialogBuilder implements Dialog{


    /**
     * Dialog for a warning message
     * @param title title
     * @param message message
     */
    @Override
    public void warning(String title, String message) {
        Alert warn = new Alert(Alert.AlertType.WARNING);
        warn.setTitle(title);
        warn.setContentText(message);
        warn.show();
    }

    /**
     * Dialog for an info message
     * @param title title
     * @param message message
     */
    @Override
    public void info(String title, String message) {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle(title);
        info.setContentText(message);
        info.show();
    }

    /**
     * Dialog for an error message
     * @param title title
     * @param message message
     */
    @Override
    public void error(String title, String message) {
        Alert err = new Alert(Alert.AlertType.ERROR);
        err.setTitle(title);
        err.setContentText(message);
        err.show();
    }
}
