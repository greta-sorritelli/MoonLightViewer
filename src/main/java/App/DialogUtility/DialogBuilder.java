package App.DialogUtility;

import javafx.animation.PauseTransition;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * Class that implements {@link Dialog} to show dialog window
 */
public class DialogBuilder implements Dialog {

    private String theme = "css/lightTheme.css";

    public DialogBuilder(String theme) {
        this.theme = theme;
    }

    /**
     * Dialog for a warning message
     *
     * @param message message
     */
    @Override
    public void warning(String message) {
        Alert warn = new Alert(Alert.AlertType.WARNING);
        generateDialog(message, warn);
    }

    /**
     * Dialog for an info message
     *
     * @param message message
     */
    @Override
    public void info(String message) {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        generateDialog(message, info);
    }

    /**
     * Dialog for an error message
     *
     * @param message message
     */
    @Override
    public void error(String message) {
        Alert err = new Alert(Alert.AlertType.ERROR);
        generateDialog(message, err);
    }

    /**
     * Generate a dialog window that closes after some seconds
     *
     * @param message message to show
     * @param dialog  type of dialog
     */
    private void generateDialog(String message, Alert dialog) {
        dialog.setHeaderText(null);
        dialog.setContentText(message);
        dialog.initStyle(StageStyle.UNDECORATED);

            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.getStylesheets().add(theme);
            dialogPane.getStyleClass().add("dialog");

        PauseTransition delay = new PauseTransition(Duration.seconds(5));
        delay.setOnFinished(e -> dialog.close());
        dialog.setOnShown(e -> delay.playFromStart());
        dialog.showAndWait();
    }
}
