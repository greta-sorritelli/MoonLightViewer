package it.unicam.cs.project.moonlightviewer.javaFX.controllers;

import it.unicam.cs.project.moonlightviewer.utility.dialogUtility.DialogBuilder;
import javafx.scene.control.Slider;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Thread.sleep;

/**
 * Class that performs animation for a slider
 *
 * @author Albanese Clarissa, Sorritelli Greta
 */
public class RunnableSlider implements Runnable {

    private final Slider slider;
    private volatile boolean shutdown = false;

    public void shutdown() {
        this.shutdown = true;
    }

    public void restart() {
        this.shutdown = false;
    }

    public RunnableSlider(Slider slider) {
        this.slider = slider;
    }

    @Override
    public void run() {
        if (!slider.isDisabled()) {
            AtomicReference<Double> x = new AtomicReference<>(slider.getValue());
            new Thread(() -> {
                try {
                    while (!shutdown) {
                        for (double i = x.get(); i <= slider.getMax(); i += 0) {
                            if(shutdown)
                                break;
                            slider.adjustValue(i);
                            sleep(500);
                            if((i < slider.getMax()) && ((i + slider.getMajorTickUnit()) > slider.getMax()))
                                i = slider.getMax();
                            else i += slider.getMajorTickUnit();
                        }
                        x.set(slider.getMin());
                    }
                } catch (InterruptedException e) {
                    DialogBuilder d = new DialogBuilder(Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResource("css/lightTheme.css")).toString());
                    d.error(e.getMessage());
                }
            }).start();
        }
    }
}
