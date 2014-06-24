package simulation;

import continuous.PIController;
import util.Preferences;

/**
 * Created by marlon on 6/19/14.
 */
public class PISimulator extends Thread {
    // ********** Fields **********//
    private final double[]             vz;
    private PIController               controller;
    private boolean                    started;
    private double                     samplingTime;
    private Preferences.SimulationMode simulationMode;

    // ********** Constructor **********//
    public PISimulator(PIController controller) {
        setName("OpenLoopSimulator");
        setDaemon(true);
        this.controller     = controller;
        this.samplingTime   = Preferences.samplingTime;
        this.simulationMode = Preferences.simulationMode;
        this.vz             = new double[2];
    }

    // ********** Methods **********//
    @Override
    public void run() {
        started = true;
        while(started){
            vz[0] = controller.getInput() - vz[1];
            controller.setOutput(((1.0 / 2.0) * (2.0 * controller.getProportionalGain() + samplingTime * controller.getIntegralGain()) * vz[0]) +
                    ((1.0 / 2.0) * (samplingTime * controller.getIntegralGain() - 2.0 * controller.getProportionalGain()) * vz[1]));
            delay();
            vz[1] = vz[0];
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

    // ********** Setters and Getters **********//
    public PIController getController() {
        return controller;
    }
    public void setController(PIController controller) {
        this.controller = controller;
    }

    public double getSamplingTime() {
        return samplingTime;
    }
    public void setSamplingTime(double samplingTime) {
        this.samplingTime = samplingTime;
    }

    public boolean isStarted() {
        return started;
    }
    public void setStarted(boolean started) {
        this.started = started;
    }

    public Preferences.SimulationMode getSimulationMode() {
        return simulationMode;
    }
    public void setSimulationMode(Preferences.SimulationMode simulationMode) {
        this.simulationMode = simulationMode;
    }
}
