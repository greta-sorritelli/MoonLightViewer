package it.unicam.cs.project.moonlightviewer.utility.dialogUtility;

/**
 * Interface for a dialog window
 *
 * @author Albanese Clarissa, Sorritelli Greta
 */
public interface Dialog {

    /**
     * Dialog for a warning message
     * @param message message
     */
    void warning(String message);

    /**
     * Dialog for an info message
     * @param message message
     */
    void info(String message);

    /**
     * Dialog for an error message
     * @param message message
     */
    void error(String message);

}
