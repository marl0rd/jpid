package continuous;

/**
 * Created by marlon on 6/19/14.
 */
public class PIController {
    // ********** Fields **********//
    private double input;
    private double output;
    private double proportionalGain;
    private double integralGain;
    private double integralTime;
    private double samplingTime;

    // ********** Constructor **********//
    public PIController() {
        input            = 0.0;
        output           = 0.0;
        proportionalGain = 0.0;
        integralGain     = 0.0;
        integralTime     = 0.0;
        samplingTime     = 0.0;
    }

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

    public double getProportionalGain() {
        return proportionalGain;
    }
    public void setProportionalGain(double proportionalGain) {
        this.proportionalGain = proportionalGain;
    }

    public double getIntegralGain() {
        return integralGain;
    }
    public void setIntegralGain(double integralGain) {
        this.integralGain = integralGain;
    }

    public double getIntegralTime() {
        return integralTime;
    }
    public void setIntegralTime(double integralTime) {
        this.integralTime = integralTime;
    }

    public double getSamplingTime() {
        return samplingTime;
    }
    public void setSamplingTime(double samplingTime) {
        this.samplingTime = samplingTime;
    }
}
