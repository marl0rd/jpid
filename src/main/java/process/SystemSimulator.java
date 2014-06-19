package process;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;

/**
 * Created by marlon on 6/17/14.
 *
 *              |   gain     |
 * Input ------>| ---------  |-------> Output
 *              | tau*s + 1  |
 *
 */
public class SystemSimulator extends Thread{

    private FirstOrderSystem process;
    private double[]         vz;
    private LongProperty timeStamp;

    public SystemSimulator() {
        setName("OpenLoopSimulator");
        setDaemon(true);
        process   = new FirstOrderSystem();
        vz        = new double[2];
        timeStamp = new SimpleLongProperty(this, "timestamp", System.currentTimeMillis());
    }

    @Override
    public void run() {
        while(true){
            vz[0] = process.getInput() - ((process.getSamplingTime() - 2 * process.getTau()) / (process.getSamplingTime() + 2 * process.getTau())) * vz[1];
            process.setOutput(((process.getGain() * process.getSamplingTime()) / (process.getSamplingTime() + 2 * process.getTau())) * (vz[0] + vz[1]));

            try {
                Thread.sleep(process.getSamplingTime());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            vz[1] = vz[0];
            timeStamp.set(System.currentTimeMillis());
        }
    }

    public FirstOrderSystem getProcess() {
        return process;
    }
    public void setProcess(FirstOrderSystem process) {
        this.process = process;
    }


}
