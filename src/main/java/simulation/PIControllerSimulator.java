package simulation;

import continuous.PIController;
import util.Preferences;

/**
 * Created by marlon on 6/19/14.
 *                          _____________________________________________________________
 *                         |                                 controller.integralGain     |
 * controller.input ------>| ( controller.proportionalGain +  ---------------------  )   |-------> controller.output
 *                         |                                           S                 |
 *                          -------------------------------------------------------------
 */
public class PIControllerSimulator extends Thread {
    // ********** Fields **********//
    private final double[]              iz;
    private final double[]              oz;
    private PIController                controller;
    private boolean                     started;
    private double                      samplingTime;
    private Preferences.SimulationSpeed simulationSpeed;

    // ********** Constructor **********//
    public PIControllerSimulator(PIController controller) {
        setName("OpenLoopSimulator");
        setDaemon(true);
        this.samplingTime    = Preferences.samplingTime;
        this.simulationSpeed = Preferences.simulationSpeed;
        this.iz              = new double[2];
        this.oz              = new double[2];
        this.controller      = controller;
    }

    // ********** Methods **********//
    @Override
    public void run() {
        started = true;
        double k1;
        double k2;
        while(started){
            k1 = (2.0 * controller.getProportionalGain() + samplingTime * controller.getIntegralGain()) / 2.0;
            k2 = (samplingTime * controller.getIntegralGain() - 2.0 * controller.getProportionalGain()) / 2.0;

            iz[0] = getController().getInput();
            oz[0] = (k1 * iz[0]) + (k2 * iz[1]) - oz[1];
            controller.setOutput(oz[0]);

            delay();

            iz[1] = iz[0];
            oz[1] = oz[0];

            //******************************** PRINTS ****************************************//
            System.out.println("(Controller)" + "\t" +
                    "Input:"  + getController().getInput()            + "\t" +
                    "k1:"     + k1                                    + "\t" +
                    "k2:"     + k2                                    + "\t" +
                    "iz[0]:"  + iz[0]                                 + "\t" +
                    "oz[0]:"  + oz[0]                                 + "\t" +
                    "iz[1]:"  + iz[1]                                 + "\t" +
                    "oz[1]:"  + oz[1]                                 + "\t" +
                    "kp:"     + getController().getProportionalGain() + "\t" +
                    "ki:"     + getController().getIntegralGain()     + "\t" +
                    "Output:" + getController().getOutput()           + "\t" );
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

    public Preferences.SimulationSpeed getSimulationSpeed() {
        return simulationSpeed;
    }
    public void setSimulationSpeed(Preferences.SimulationSpeed simulationSpeed) {
        this.simulationSpeed = simulationSpeed;
    }
}
