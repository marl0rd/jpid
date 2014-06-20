package process;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;

/**
 * Created by marlon on 6/17/14.
 *
 *                      |   gain     |
 * process.input ------>| ---------  |-------> process.output
 *                      | tau*s + 1  |
 *
 */
public class FirstOrderSimulator extends Thread{

    private static final double DEFAULT_SAMPLING_TIME = 0.01;
    private final double[]      vz;
    private FirstOrderSystem    process;
    private final LongProperty  timeStamp;
    private boolean             started;
    private double              samplingTime;

    public FirstOrderSimulator(FirstOrderSystem process) {
        setName("OpenLoopSimulator");
        setDaemon(true);
        this.process        = process;
        this.samplingTime   = DEFAULT_SAMPLING_TIME;
        this.vz             = new double[2];
        this.timeStamp      = new SimpleLongProperty(this, "timestamp", System.currentTimeMillis());
    }

    @Override
    public void run() {
        while(started){
            vz[0] = process.getInput() - ((samplingTime- 2 * process.getTau()) / (samplingTime + 2 * process.getTau())) * vz[1];
            process.setOutput(((process.getGain() * samplingTime) / (samplingTime + 2 * process.getTau())) * (vz[0] + vz[1]));
            delay();
            vz[1] = vz[0];
        }
    }

    public void delay(){
        // The samplingTime of process is based in second, the sleep method is based in milliseconds
        try {
            Thread.sleep((long) (samplingTime * 1000));
        } catch (InterruptedException e) {
            started = false;
        }
        timeStamp.set(System.currentTimeMillis());
    }

    public FirstOrderSystem getProcess() {
        return process;
    }
    public void setProcess(FirstOrderSystem process) {
        this.process = process;
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
}
