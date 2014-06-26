package source;

import util.Preferences;

/**
 * Created by marlon on 6/24/14.
 */
public class Source implements Runnable{
    protected double                      samplingTime;
    protected Preferences.SimulationSpeed simulationSpeed;
    protected double                      output;
    protected boolean                     started;

    public Source() {
    }

    @Override
    public void run() {
    }

    public double getSamplingTime() {
        return samplingTime;
    }
    public void setSamplingTime(double samplingTime) {
        this.samplingTime = samplingTime;
    }

    public Preferences.SimulationSpeed getSimulationSpeed() {
        return simulationSpeed;
    }
    public void setSimulationSpeed(Preferences.SimulationSpeed simulationSpeed) {
        this.simulationSpeed = simulationSpeed;
    }

    public double getOutput() {
        return output;
    }
    public void setOutput(double output) {
        this.output = output;
    }

    public boolean isStarted() {
        return started;
    }
    public void setStarted(boolean started) {
        this.started = started;
    }
}
