package simulation;

import continuous.FirstOrderSystem;
import continuous.PIController;
import javafx.application.Platform;
import javafx.beans.property.*;
import util.Preferences;


/**
 * Created by marlon on 6/20/14.
 */
public class Loop extends Thread {
    // ********** Fields **********//
    private double                     input;
    private double                     output;
    private double                     error;
    private FirstOrderSystem process;
    private PIController controller;
    private FirstOrderSimulator        processThread;
    private PISimulator                controllerThread;
    private LongProperty               timeStamp;
    private double                     samplingTime;
    private Preferences.SimulationMode simulationMode;
    private BooleanProperty            processStarted;
    private BooleanProperty            controllerStarted;
    private BooleanProperty            loopStarted;
    private Preferences.LoopType       loopType;

    // ********** Constructor **********//
    public Loop(FirstOrderSystem process, PIController controller) {
        setName("LoopSimulator");
        setDaemon(true);
        this.process           = process;
        this.controller        = controller;
        this.processThread     = new FirstOrderSimulator(this.process);
        this.controllerThread  = new PISimulator(this.controller);
        this.input             = 0.0;
        this.output            = 0.0;
        this.error             = 0.0;
        this.processStarted    = new SimpleBooleanProperty(this, "processStarted", false);
        this.controllerStarted = new SimpleBooleanProperty(this, "controllerStarted", false);
        this.loopStarted       = new SimpleBooleanProperty(this, "loopStarted", false);
        this.loopType          = Preferences.LoopType.CLOSE_LOOP;
        this.simulationMode    = Preferences.simulationMode;
        this.timeStamp         = new SimpleLongProperty(this, "timestamp", System.currentTimeMillis());
        this.samplingTime      = Math.min(processThread.getSamplingTime(), controllerThread.getSamplingTime());
    }

    // ********** Methods **********//
    @Override
    public void run() {
        processThread.start();
        processStarted.set(true);

        controllerThread.start();
        controllerStarted.set(true);

        loopStarted.set(true);
        while(loopStarted.get()){
            System.out.print("I=" + this.input + "\t");

            error = input - getProcessThread().getProcess().getOutput();
            if(loopType == Preferences.LoopType.CLOSE_LOOP){
                controllerThread.getController().setInput(this.error);
                processThread.getProcess().setInput(controllerThread.getController().getOutput());
            } else {
                processThread.getProcess().setInput(this.input);
            }

            System.out.print("C=" + processThread.getProcess().getOutput() + "\t");
            output = processThread.getProcess().getOutput();
            System.out.print("0=" + this.output + "\n");
            delay();
        }
    }

    public void delay(){
        // The samplingTime of simulation is based in second, the sleep method is based in milliseconds
        try {
            Thread.sleep((long) ((samplingTime * simulationMode.factor)* 1000));
        } catch (InterruptedException e) {
            loopStarted.set(false);
            processThread.setStarted(false);
            controllerThread.setStarted(false);
        }
        Platform.runLater(() -> {
            setTimeStamp(System.currentTimeMillis());
        });
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

    public double getError() {
        return error;
    }
    public void setError(double error) {
        this.error = error;
    }

    public FirstOrderSystem getProcess() {
        return process;
    }
    public void setProcess(FirstOrderSystem process) {
        this.process = process;
    }

    public PIController getController() {
        return controller;
    }
    public void setController(PIController controller) {
        this.controller = controller;
    }

    public FirstOrderSimulator getProcessThread() {
        return processThread;
    }
    public void setProcessThread(FirstOrderSimulator processThread) {
        this.processThread = processThread;
    }

    public PISimulator getControllerThread() {
        return controllerThread;
    }
    public void setControllerThread(PISimulator controllerThread) {
        this.controllerThread = controllerThread;
    }

    public double getSamplingTime() {
        return samplingTime;
    }
    public void setSamplingTime(double samplingTime) {
        this.samplingTime = samplingTime;
        this.processThread.setSamplingTime(samplingTime);
        this.controllerThread.setSamplingTime(samplingTime);
    }

    public Preferences.SimulationMode getSimulationMode() {
        return simulationMode;
    }
    public void setSimulationMode(Preferences.SimulationMode simulationMode) {
        this.simulationMode = simulationMode;
        this.processThread.setSimulationMode(simulationMode);
        this.controllerThread.setSimulationMode(simulationMode);
    }

    public boolean getProcessStarted() {
        return processStarted.get();
    }
    public BooleanProperty processStartedProperty() {
        return processStarted;
    }
    public void setProcessStarted(boolean processStarted) {
        this.processStarted.set(processStarted);
    }

    public boolean getControllerStarted() {
        return controllerStarted.get();
    }
    public BooleanProperty controllerStartedProperty() {
        return controllerStarted;
    }
    public void setControllerStarted(boolean controllerStarted) {
        this.controllerStarted.set(controllerStarted);
    }

    public boolean getLoopStarted() {
        return loopStarted.get();
    }
    public BooleanProperty loopStartedProperty() {
        return loopStarted;
    }
    public void setLoopStarted(boolean loopStarted) {
        this.loopStarted.set(loopStarted);
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
