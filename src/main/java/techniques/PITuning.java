package techniques;

import continuous.FirstOrderSystem;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;

/**
 * Created by marlon on 6/20/14.
 *
 * 1) Add a First Order System (Process).
 * 2) Set desired settlingTime and dampingRatio.
 * 3) Get the controller.
 *
 */
public class PITuning {
    // ********** Fields **********//
    private DoubleProperty   dampingRatio;
    private DoubleProperty   settlingTime;
    private DoubleProperty   undampedNaturalFrequency;
    private double           proportionalGain;
    private double           integralGain;
    private double           integralTime;
    private FirstOrderSystem process;

    // ********** Constructor **********//
    public PITuning(FirstOrderSystem process) {
        this.process                  = process;
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
        proportionalGain = ((2.0*dampingRatio.get()*undampedNaturalFrequency.get()) - (1.0/process.getTau())) / (process.getGain()/process.getTau());
        integralTime = (2.0*dampingRatio.get()*undampedNaturalFrequency.get() - (1.0/process.getTau())) / (Math.pow(undampedNaturalFrequency.get(), 2.0));
        integralGain = proportionalGain / integralTime;
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

    public FirstOrderSystem getProcess() {
        return process;
    }
    public void setProcess(FirstOrderSystem process) {
        this.process = process;
    }

    public double getProportionalGain() {
        return proportionalGain;
    }

    public double getIntegralGain() {
        return integralGain;
    }

    public double getIntegralTime() {
        return integralTime;
    }
}
