package techniques;

import continuous.FirstOrderSystem;

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
    private double   dampingRatio;
    private double   settlingTime;
    private double   undampedNaturalFrequency;
    private double   proportionalGain;
    private double   integralGain;
    private double   integralTime;
    private FirstOrderSystem process;

    // ********** Constructor **********//
    public PITuning(FirstOrderSystem process) {
        this.process                  = process;
        this.dampingRatio             = 0.0;
        this.settlingTime             = 0.0;
        this.undampedNaturalFrequency = 0.0;
    }

    // ********** Methods **********//
    private void recalculate() {
        System.out.println(process);
        undampedNaturalFrequency = 4.0 / (dampingRatio * settlingTime);
        proportionalGain = ((2.0*dampingRatio*undampedNaturalFrequency) - (1.0/process.getTau())) / (process.getGain()/process.getTau());
        integralTime = (2.0*dampingRatio*undampedNaturalFrequency - (1.0/process.getTau())) / (Math.pow(undampedNaturalFrequency, 2.0));
        integralGain = proportionalGain / integralTime;
    }

    // ********** Setters and Getters **********//
    public double getDampingRatio() {
        return dampingRatio;
    }
    public void setDampingRatio(double dampingRatio) {
        this.dampingRatio = dampingRatio;
    }

    public double getSettlingTime() {
        return settlingTime;
    }
    public void setSettlingTime(double settlingTime) {
        this.settlingTime = settlingTime;
    }

    public double getUndampedNaturalFrequency() {
        return undampedNaturalFrequency;
    }
    public void setUndampedNaturalFrequency(double undampedNaturalFrequency) {
        this.undampedNaturalFrequency = undampedNaturalFrequency;
    }

    public FirstOrderSystem getProcess() {
        return process;
    }
    public void setProcess(FirstOrderSystem process) {
        this.process = process;
    }

    public double getProportionalGain() {
        recalculate();
        return proportionalGain;
    }

    public double getIntegralGain() {
        recalculate();
        return integralGain;
    }

    public double getIntegralTime() {
        recalculate();
        return integralTime;
    }
}
