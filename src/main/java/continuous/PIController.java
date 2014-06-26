package continuous;

/**
 * Created by marlon on 6/19/14.
 *
 *                                 integralGain
 *  output = ( proportionalGain +  -----------  ) * input
 *                                     S
 *
 */
public class PIController extends Controller {
    // ********** Fields **********//
    private double  proportionalGain;
    private double  integralGain;
    private double  integralTime;

    // ********** Constructor **********//
    public PIController() {
        input            = 0.0;
        output           = 0.0;
        proportionalGain = 0.0;
        integralGain     = 0.0;
        integralTime     = 0.0;
        byPassed         = false;
    }

    public double getProportionalGain() {
        return proportionalGain;
    }
    public void setProportionalGain(double proportionalGain) {
        this.proportionalGain = proportionalGain;
    }

    public double getIntegralGain() {
        return integralGain;
    }
    public void setIntegralGain(double integralGain) {
        this.integralGain = integralGain;
    }

    public double getIntegralTime() {
        return integralTime;
    }
    public void setIntegralTime(double integralTime) {
        this.integralTime = integralTime;
    }
}
