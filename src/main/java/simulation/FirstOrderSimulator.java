package simulation;

import continuous.FirstOrderSystem;
import util.Preferences;

import java.io.Console;

/**
 * Created by marlon on 6/17/14.
 *                          ____________
 *                         |   gain     |
 * simulation.input ------>| ---------  |-------> simulation.output
 *                         | tau*s + 1  |
 *                          ------------
 *
 */
public class FirstOrderSimulator extends Thread{
    // ********** Fields **********//
    private final double[]             iz;
    private final double[]             oz;
    private FirstOrderSystem           process;
    private boolean                    started;
    private double                     samplingTime;
    private Preferences.SimulationSpeed simulationSpeed;

    // ********** Constructor **********//
    public FirstOrderSimulator(FirstOrderSystem process) {
        setName("ProcessSimulator");
        setDaemon(true);
        this.process        = process;
        this.samplingTime   = Preferences.samplingTime;
        this.simulationSpeed = Preferences.simulationSpeed;
        this.iz             = new double[2];
        this.oz             = new double[2];
    }

    // ********** Methods **********//
    @Override
    public void run() {
        started = true;
        double k1;
        double k2;
        while(started){
            k1 = (process.getGain() * samplingTime) / (samplingTime + (2*process.getTau()));
            k2 = (samplingTime - (2*process.getTau())) / (samplingTime + (2*process.getTau()));

            iz[0] = process.getInput();
            oz[0] = (k1 * iz[1]) + (k1 * iz[0]) - (k2*oz[1]);

            process.setOutput(oz[0]);

            delay();

            iz[1] = iz[0];
            oz[1] = oz[0];
        }
    }

    public void delay(){
        // The samplingTime of simulation is based in second, the sleep method is based in milliseconds
        try {
            Thread.sleep((long) ((samplingTime * simulationSpeed.factor)* 1000));
        } catch (InterruptedException e) {
            started = false;
        }
    }

    // ********** Setters and Getters **********//
    public FirstOrderSystem getProcess() {
        return process;
    }
    public void setProcess(FirstOrderSystem process) {
        this.process = process;
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

    public Preferences.SimulationSpeed getSimulationSpeed() {
        return simulationSpeed;
    }
    public void setSimulationSpeed(Preferences.SimulationSpeed simulationSpeed) {
        this.simulationSpeed = simulationSpeed;
    }
}
