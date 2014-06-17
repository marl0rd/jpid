package process;

/**
 * Created by marlon on 6/11/14.
 */
public class SecondOrderSystem extends Thread {

    public static enum Order{FIRST, SECOND, THIRD}

    private double      output;
    private double      input;
//    private double      tau;
//    private double      timeConstant;
//    private double      delayTime;
//    private double      riseTime;
//    private double      peakTime;
//    private double      maximumOvershoot;
//    private double      settlingTime;
    private double      currentTime;
    private boolean     simulationActivated;
    private double[]    delayedInput;
    private double[]    delayedOutput;
    private double      samplingTime;
    private long        period;

    public SecondOrderSystem(String name) {
        super(name);
        input               = 0;
        output              = 0;
        currentTime         = System.currentTimeMillis();
        simulationActivated = true;
        delayedInput        = new double[1];
        delayedOutput       = new double[1];
        samplingTime        = 0.01;
        period              = (long) (samplingTime*10000);
    }

    @Override
    public void run() {
        while(simulationActivated){
            output = -(delayedOutput[0]*(samplingTime - 35.14)/(samplingTime + 35.14)) +
                    (input*((0.66*samplingTime)/(samplingTime + 35.14))) +
                    (delayedInput[0]*((0.66*samplingTime)/(samplingTime+35.14)));

            try {
                Thread.sleep(period);
            } catch (InterruptedException e) {
                e.printStackTrace();
                simulationActivated = false;
            }

            delayedOutput[0] = output;
            delayedInput[0]  = input;
            System.out.println("input:" + input + ". \t" + "output:" + output);
        }
    }

}
