package App.DialogUtility;

import javafx.scene.control.Alert;

public class DialogBuilder implements Dialog{

    @Override
    public void warning(String title, String message) {
        Alert warn = new Alert(Alert.AlertType.WARNING);
        warn.setTitle(title);
        warn.setContentText(message);
        warn.show();
    }

    @Override
    public void info(String title, String message) {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle(title);
        info.setContentText(message);
        info.show();
    }

    @Override
    public void error(String title, String message) {
        Alert err = new Alert(Alert.AlertType.ERROR);
        err.setTitle(title);
        err.setContentText(message);
        err.show();
    }
}
