package continuous;

/**
 * Created by marlon on 6/16/14.
 *
 * The transfer function is in form:
 *
 *      output         gain
 *      ------   =   ---------
 *       input       tau*S + 1
 *
 */
public class FirstOrderSystem {
    // ********** Fields **********//
    private double input;
    private double output;
    private double gain;
    private double tau;

    // ********** Constructor **********//
    public FirstOrderSystem() {
        input  = 0.0;
        output = 0.0;
        gain   = 0.0;
        tau    = 0.0;
    }

    // ********** Setters and Getters **********//
    public double getInput() {
        return input;
    }
    public void setInput(double input) {
        this.input = input;
    }

    public double getOutput() {
        return output;
    }
    public void setOutput(double output) {
        this.output = output;
    }

    public double getGain() {
        return gain;
    }
    public void setGain(double gain) {
        this.gain = gain;
    }

    public double getTau() {
        return tau;
    }
    public void setTau(double tau) {
        this.tau = tau;
    }
}