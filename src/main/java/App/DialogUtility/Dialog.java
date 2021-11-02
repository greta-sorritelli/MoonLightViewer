package App.DialogUtility;

/**
 * Interface for a dialog window
 */
public interface Dialog {

    /**
     * Dialog for a warning message
     * @param title title
     * @param message message
     */
    void warning(String title, String message);

    /**
     * Dialog for an info message
     * @param title title
     * @param message message
     */
    void info(String title, String message);

    /**
     * Dialog for an error message
     * @param title title
     * @param message message
     */
    void error(String title, String message);

}
