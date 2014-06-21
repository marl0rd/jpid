package util;

/**
 * Created by marlon on 6/20/14.
 */
public class Preferences {
    public static enum SimulationMode {
        REAL_TIME(1.0),
        SLOW(10.0),
        VERY_SLOW(100.0),
        FAST(0.1),
        VERY_FAST(0.01);

        public double factor;

        SimulationMode(double factor){
            this.factor = factor;
        }
    }
    public static enum LoopType {CLOSE_LOOP, OPEN_LOOP}

    private static final SimulationMode DEFAULT_SIMULATION_MODE = SimulationMode.SLOW;
    private static final LoopType       DEFAULT_LOOP_TYPE       = LoopType.CLOSE_LOOP;
    private static final double         DEFAULT_SAMPLING_TIME   = 0.1;

    public static SimulationMode simulationMode = DEFAULT_SIMULATION_MODE;
    public static double samplingTime           = DEFAULT_SAMPLING_TIME;
    public static LoopType loopType             = DEFAULT_LOOP_TYPE;

}
