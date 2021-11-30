package it.unicam.cs.project.moonlightviewer.utility.exceptions;

/**
 * Exception to be thrown when a bound value isn't supported by the logarithmic axis
 *
 * @author Albanese Clarissa, Sorritelli Greta
 */
public class IllegalLogarithmicRangeException extends RuntimeException {

    public IllegalLogarithmicRangeException(String message) {
            super(message);
        }
}
