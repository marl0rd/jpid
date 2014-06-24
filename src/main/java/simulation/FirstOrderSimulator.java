package simulation;

import continuous.FirstOrderSystem;
import util.Preferences;

/**
 * Created by marlon on 6/17/14.
 *
 *                         |   gain     |
 * simulation.input ------>| ---------  |-------> simulation.output
 *                         | tau*s + 1  |
 *
 */
public class FirstOrderSimulator extends Thread{
    // ********** Fields **********//
    private final double[]             vz;
    private FirstOrderSystem process;
    private boolean                    started;
    private double                     samplingTime;
    private Preferences.SimulationMode simulationMode;

    // ********** Constructor **********//
    public FirstOrderSimulator(FirstOrderSystem process) {
        setName("ProcessSimulator");
        setDaemon(true);
        this.process        = process;
        this.samplingTime   = Preferences.samplingTime;
        this.simulationMode = Preferences.simulationMode;
        this.vz             = new double[2];
    }

    // ********** Methods **********//
    @Override
    public void run() {
        started = true;
        while(started){
            vz[0] = process.getInput() - ((samplingTime- 2.0 * process.getTau()) / (samplingTime + 2.0 * process.getTau())) * vz[1];
            System.out.println("vcz[0]" + vz[0]);
            process.setOutput(((process.getGain() * samplingTime) / (samplingTime + 2.0 * process.getTau())) * (vz[0] + vz[1]));
            delay();
            vz[1] = vz[0];
            System.out.println("vcz[1]" + vz[1]);
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

    public Preferences.SimulationMode getSimulationMode() {
        return simulationMode;
    }
    public void setSimulationMode(Preferences.SimulationMode simulationMode) {
        this.simulationMode = simulationMode;
    }
}
