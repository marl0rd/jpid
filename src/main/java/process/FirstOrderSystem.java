package process;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;

/**
 * Created by marlon on 6/16/14.
 *
 * The transfer function is in form:
 *
 * G(s) =   gain
 *        ---------
 *        tau*s + 1
 *
 */
public class FirstOrderSystem {
    private DoubleProperty input;
    private DoubleProperty output;
    private DoubleProperty gain;
    private DoubleProperty tau;
    private DoubleProperty  samplingTime;

    public FirstOrderSystem() {
        input        = new SimpleDoubleProperty(this, "input", 0.0);
        output       = new SimpleDoubleProperty(this, "output", 0.0);
        gain         = new SimpleDoubleProperty(this, "gaing", 0.0);
        tau          = new SimpleDoubleProperty(this, "tau", 0.0);
        samplingTime = new SimpleDoubleProperty(this, "samplingTime", 0.1);
    }

    public double getInput() {
        return input.get();
    }
    public DoubleProperty inputProperty() {
        return input;
    }
    public void setInput(double input) {
        this.input.set(input);
    }

    public double getOutput() {
        return output.get();
    }
    public DoubleProperty outputProperty() {
        return output;
    }
    public void setOutput(double output) {
        this.output.set(output);
    }

    public double getGain() {
        return gain.get();
    }
    public DoubleProperty gainProperty() {
        return gain;
    }
    public void setGain(double gain) {
        this.gain.set(gain);
    }

    public double getTau() {
        return tau.get();
    }
    public DoubleProperty tauProperty() {
        return tau;
    }
    public void setTau(double tau) {
        this.tau.set(tau);
    }

    public double getSamplingTime() {
        return samplingTime.get();
    }
    public DoubleProperty samplingTimeProperty() {
        return samplingTime;
    }
    public void setSamplingTime(double samplingTime) {
        this.samplingTime.set(samplingTime);
    }
}