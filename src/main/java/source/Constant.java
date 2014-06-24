package source;

import util.Preferences;

/**
 * One constant value function
 */
public class Constant extends Source implements Runnable{
    private double value;

    public Constant() {
        samplingTime   = Preferences.samplingTime;
        simulationMode = Preferences.simulationMode;
    }

    @Override
    public void run() {
        started = true;
        while (started){
            output = value;
            delay();
        }
    }

    public void delay(){
        // The samplingTime of simulation is based in second, the sleep method is based in milliseconds
        try {
            Thread.sleep((long) ((samplingTime * simulationMode.factor)* 1000));
        } catch (InterruptedException e) {
            started = false;
        }
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
