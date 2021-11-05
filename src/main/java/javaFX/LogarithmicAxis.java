package javaFX;

import com.sun.javafx.charts.ChartLayoutAnimator;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.chart.ValueAxis;
import javafx.util.Duration;

public class LogarithmicAxis extends ValueAxis<Number> {

    private Object currentAnimationID;
    private final ChartLayoutAnimator animator = new ChartLayoutAnimator(this);
    private final DoubleProperty logUpperBound = new SimpleDoubleProperty();
    private final DoubleProperty logLowerBound = new SimpleDoubleProperty();

    public LogarithmicAxis() {
        super(0.0001, 1000);
        bindLogBoundsToDefaultBounds();
    }

    public LogarithmicAxis(double lowerBound, double upperBound) {
        super(lowerBound, upperBound);
        validateBounds(lowerBound, upperBound);
        bindLogBoundsToDefaultBounds();
    }

    public void setLogarithmicUpperBound(double d) {
        double nd = Math.pow(10, Math.ceil(Math.log10(d)));
        setUpperBound(nd == d ? nd * 10 : nd);
    }

    /**
     * Binds logarithmic bounds with the super class bounds, consider the
     * base 10 logarithmic scale.
     */
    private void bindLogBoundsToDefaultBounds() {
        logLowerBound.bind(new DoubleBinding() {
            {
                super.bind(lowerBoundProperty());
            }
            @Override
            protected double computeValue() {
                return Math.log10(lowerBoundProperty().get());
            }
        });
        logUpperBound.bind(new DoubleBinding() {
            {
                super.bind(upperBoundProperty());
            }
            @Override
            protected double computeValue() {
                return Math.log10(upperBoundProperty().get());
            }
        });
    }

    /**
     * Validates the bounds by throwing an exception if the values are not
     * conform to the mathematics log interval: [0,Double.MAX_VALUE]
     *
     */
    private void validateBounds(double lowerBound, double upperBound) throws IllegalLogarithmicRangeException {
        if (lowerBound < 0 || upperBound < 0 || lowerBound > upperBound) {
            throw new IllegalLogarithmicRangeException(
                    "The logarithmic range should be in [0,Double.MAX_VALUE] and the lowerBound should be less than the upperBound");
        }
    }

    /**
     * It is used to get the list of minor tick marks position to display on the axis.
     * It's based on the number of minor tick and the logarithmic formula.
     *
     */
    @Override
    protected List<Number> calculateMinorTickMarks() {
        List<Number> minorTickMarksPositions = new ArrayList<>();
        return minorTickMarksPositions;
    }

    //Then, the calculateTickValues method

    /**
     * It is used to calculate a list of all the data values for each tick mark in range,
     * represented by the second parameter. Displays one tick each power of 10.
     *
     */
    @Override
    protected List<Number> calculateTickValues(double length, Object range) {
        LinkedList<Number> tickPositions = new LinkedList<>();
        if (range != null) {
            double lowerBound = ((double[]) range)[0];
            double upperBound = ((double[]) range)[1];

            for (double i = Math.log10(lowerBound); i <= Math.log10(upperBound); i++) {
                tickPositions.add(Math.pow(10, i));
            }

            if (!tickPositions.isEmpty()) {
                if (tickPositions.getLast().doubleValue() != upperBound) {
                    tickPositions.add(upperBound);
                }
            }
        }

        return tickPositions;
    }

    /**
     * The getRange provides the current range of the axis. A basic
     * implementation is to return an array of the lowerBound and upperBound
     * properties defined into the ValueAxis class.
     *
     */
    @Override
    protected double[] getRange() {
        return new double[]{
                getLowerBound(),
                getUpperBound()
        };
    }

    /**
     * The getTickMarkLabel is only used to convert the number value to a string
     * that will be displayed under the tickMark.
     *
     */
    @Override
    protected String getTickMarkLabel(Number value) {
        NumberFormat formatter = NumberFormat.getInstance();
        formatter.setMaximumIntegerDigits(10);
        formatter.setMinimumIntegerDigits(1);
        return formatter.format(value);
    }

    /**
     * Updates the range when data are added into the chart.
     * There is two possibilities, the axis is animated or not. The
     * simplest case is to set the lower and upper bound properties directly
     * with the new values.
     *
     */
    @Override
    protected void setRange(Object range, boolean animate) {
        if (range != null) {
            final double[] rangeProps = (double[]) range;
            final double lowerBound = rangeProps[0];
            final double upperBound = rangeProps[1];
            final double oldLowerBound = getLowerBound();
            setLowerBound(lowerBound);
            setUpperBound(upperBound);
            if (animate) {
                animator.stop(currentAnimationID);
                currentAnimationID = animator.animate(
                        new KeyFrame(Duration.ZERO,
                                new KeyValue(currentLowerBound, oldLowerBound)
                        ),
                        new KeyFrame(Duration.millis(700),
                                new KeyValue(currentLowerBound, lowerBound)
                        )
                );
            } else {
                currentLowerBound.set(lowerBound);
            }
        }
    }

    @Override
    public Number getValueForDisplay(double displayPosition) {
        double delta = logUpperBound.get() - logLowerBound.get();
        if (getSide().isVertical()) {
            return Math.pow(10, (((displayPosition - getHeight()) / -getHeight()) * delta) + logLowerBound.get());
        } else {
            return Math.pow(10, (((displayPosition / getWidth()) * delta) + logLowerBound.get()));
        }
    }

    @Override
    public double getDisplayPosition(Number value) {
        double delta = logUpperBound.get() - logLowerBound.get();
        double deltaV = Math.log10(value.doubleValue()) - logLowerBound.get();
        if (getSide().isVertical()) {
            return (1. - ((deltaV) / delta)) * getHeight();
        } else {
            return ((deltaV) / delta) * getWidth();
        }
    }

    /**
     * Exception to be thrown when a bound value isn't supported by the
     * logarithmic axis<br>
     *
     */
    public static class IllegalLogarithmicRangeException extends RuntimeException {

        public IllegalLogarithmicRangeException(String message) {
            super(message);
        }
    }
}