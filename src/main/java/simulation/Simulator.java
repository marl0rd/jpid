package simulation;

import continuous.FirstOrderSystem;
import continuous.PIController;
import javafx.application.Platform;
import javafx.beans.property.*;
import source.Constant;
import source.Source;
import util.Preferences;

/**
 * Created by marlon on 6/20/14.
 *
 * Dummy simulator:
 *     Only simulates First-Order Systems with PIController.
 *     __________                ______________         ___________________
 *    |          |  +    error  |              |       |                   |
 *    |  Source  |---( ) ------>| PIController |------>| FirstOrderProcess |----------> Output
 *    |__________|    |-        |______________|       |___________________|     |
 *                    |                                                          |
 *                    ------------------------------------------------------------
 *
 */

public class Simulator extends Thread {
    // ********** Fields **********//
    private Source                      source;
    private PIControllerSimulator       controllerThread;
    private FirstOrderSimulator         processThread;
    private double                      output;
    private double                      error;

    private Preferences.SimulationSpeed simulationSpeed;
    private Preferences.LoopType        loopType;
    private LongProperty                timeStamp;
    private double                      samplingTime;

    // ********** Constructor **********//
    public Simulator() {
        this(new Constant(1.0), new PIController(), new FirstOrderSystem());
    }

    public Simulator(Source source, PIController controller, FirstOrderSystem process) {
        setName("LoopSimulator");
        setDaemon(true);
        this.source            = source;
        this.controllerThread  = new PIControllerSimulator(controller);
        this.processThread     = new FirstOrderSimulator(process);
        this.output            = 0.0;
        this.error             = 0.0;

        this.simulationSpeed   = Preferences.simulationSpeed;
        this.loopType          = Preferences.LoopType.CLOSE_LOOP;
        this.timeStamp         = new SimpleLongProperty(this, "timestamp", System.currentTimeMillis());
        this.samplingTime      = Math.min(processThread.getSamplingTime(), Math.min(controllerThread.getSamplingTime(), source.getSamplingTime()));
    }

    // ********** Methods **********//
    @Override
    public void run() {
        new Thread(source).start();
        controllerThread.start();
        processThread.start();
        boolean loopStarted = true;
        while(loopStarted){
            //******************************** LOOP ****************************************//
            if(loopType == Preferences.LoopType.CLOSE_LOOP){
                // Get the error
                error = source.getOutput() - processThread.getProcess().getOutput();
                // Set the error as controller's input
                controllerThread.getController().setInput(this.error);
                // Set the controller output to the process
                processThread.getProcess().setInput(controllerThread.getController().getOutput());
                // Get the process output
                output = processThread.getProcess().getOutput();
            } else {
                // Set source directly in the process
                processThread.getProcess().setInput(source.getOutput());
                // Get the process output
                output = processThread.getProcess().getOutput();
            }


            //******************************** DELAY ****************************************//
            // The samplingTime of simulation is based in second, the sleep method is based in milliseconds
            try {
                Thread.sleep((long) ((samplingTime * simulationSpeed.factor)* 1000));
            } catch (InterruptedException e) {
                loopStarted = false;
                processThread.setStarted(false);
                controllerThread.setStarted(false);
            }
            Platform.runLater(() -> setTimeStamp(System.currentTimeMillis()));

            //******************************** PRINTS ****************************************//
            System.out.println("Input:" + source.getOutput() + "\t" +
                               "Error:" + error + "\t" +
                               "Output:" + processThread.getProcess().getOutput());
        }
    }

    // ********** Setters and Getters **********//
    public Source getSource() {
        return source;
    }
    public void setSource(Source source) {
        this.source = source;
    }

    public PIController getController() {
        return controllerThread.getController();
    }
    public void setController(PIController controller) {
        this.controllerThread.setController(controller);
    }

    public FirstOrderSystem getProcess() {
        return processThread.getProcess();
    }
    public void setProcess(FirstOrderSystem process) {
        this.processThread.setProcess(process);
    }

    public double getOutput() {
        return output;
    }
    public void setOutput(double output) {
        this.output = output;
    }

    public double getError() {
        return error;
    }
    public void setError(double error) {
        this.error = error;
    }

    public double getSamplingTime() {
        return samplingTime;
    }
    public void setSamplingTime(double samplingTime) {
        this.samplingTime = samplingTime;
        this.processThread.setSamplingTime(samplingTime);
        this.controllerThread.setSamplingTime(samplingTime);
    }

    public Preferences.SimulationSpeed getSimulationSpeed() {
        return simulationSpeed;
    }
    public void setSimulationSpeed(Preferences.SimulationSpeed simulationSpeed) {
        this.simulationSpeed = simulationSpeed;
        this.processThread.setSimulationSpeed(simulationSpeed);
        this.controllerThread.setSimulationSpeed(simulationSpeed);
    }

    public Preferences.LoopType getLoopType() {
        return loopType;
    }
    public void setLoopType(Preferences.LoopType loopType) {
        this.loopType = loopType;
    }

    public long getTimeStamp() {
        return timeStamp.get();
    }
    public LongProperty timeStampProperty() {
        return timeStamp;
    }
    public void setTimeStamp(long timeStamp) {
        this.timeStamp.set(timeStamp);
    }

}
