package continuous;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;

/**
 * Created by marlon on 6/20/14.
 */
public class PITuning {
    // ********** Fields **********//
    private DoubleProperty   dampingRatio;
    private DoubleProperty   settlingTime;
    private DoubleProperty   undampedNaturalFrequency;
    private PIController     controller;
    private FirstOrderSystem process;

    // ********** Constructor **********//
    public PITuning(PIController controller, FirstOrderSystem process) {
        this.process                  = process;
        this.controller               = controller;
        this.dampingRatio             = new DoublePropertyBase(0.0) {
            @Override protected void invalidated() {
                recalculate();
                set(get());
            }

            @Override public Object getBean() {
                return this;
            }
            @Override public String getName() {
                return "dampingRatio";
            }
        };
        this.settlingTime             = new DoublePropertyBase(0.0) {
            @Override protected void invalidated() {
                recalculate();
                set(get());
            }

            @Override public Object getBean() {
                return this;
            }
            @Override public String getName() {
                return "settlingTime";
            }
        };
        this.undampedNaturalFrequency = new DoublePropertyBase(0.0) {
            @Override protected void invalidated() {
                recalculate();
                set(get());
            }

            @Override public Object getBean() {
                return this;
            }
            @Override public String getName() {
                return "undampedNaturalFrequency";
            }
        };
    }

    // ********** Methods **********//
    private void recalculate() {
        undampedNaturalFrequency.set(4.0/(dampingRatio.get() * settlingTime.get()));
        controller.setProportionalGain(((2.0*dampingRatio.get()*undampedNaturalFrequency.get()) - (1.0/process.getTau())) / (process.getGain()/process.getTau()));
        controller.setIntegralTime((2.0*dampingRatio.get()*undampedNaturalFrequency.get() - (1.0/process.getTau())) / (Math.pow(undampedNaturalFrequency.get(), 2.0)));
        controller.setIntegralGain(controller.getProportionalGain() / controller.getIntegralTime());
    }

    // ********** Setters and Getters **********//
    public double getDampingRatio() {
        return dampingRatio.get();
    }
    public DoubleProperty dampingRatioProperty() {
        return dampingRatio;
    }
    public void setDampingRatio(double dampingRatio) {
        this.dampingRatio.set(dampingRatio);
    }

    public double getSettlingTime() {
        return settlingTime.get();
    }
    public DoubleProperty settlingTimeProperty() {
        return settlingTime;
    }
    public void setSettlingTime(double settlingTime) {
        this.settlingTime.set(settlingTime);
    }

    public double getUndampedNaturalFrequency() {
        return undampedNaturalFrequency.get();
    }
    public DoubleProperty undampedNaturalFrequencyProperty() {
        return undampedNaturalFrequency;
    }
    public void setUndampedNaturalFrequency(double undampedNaturalFrequency) {
        this.undampedNaturalFrequency.set(undampedNaturalFrequency);
    }

    public PIController getController() {
        return controller;
    }
    public void setController(PIController controller) {
        this.controller = controller;
    }

    public FirstOrderSystem getProcess() {
        return process;
    }
    public void setProcess(FirstOrderSystem process) {
        this.process = process;
    }
}
