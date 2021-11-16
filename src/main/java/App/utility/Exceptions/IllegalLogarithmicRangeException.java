package App.utility.Exceptions;

    /**
     * Exception to be thrown when a bound value isn't supported by the
     * logarithmic axis<br>
     *
     */
    public class IllegalLogarithmicRangeException extends RuntimeException {

        public IllegalLogarithmicRangeException(String message) {
            super(message);
        }
    }
