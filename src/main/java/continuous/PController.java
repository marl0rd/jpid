package continuous;

/**
 * Created by marlon on 6/24/14.
 *
 *
 *  output = proportionalGain * input
 *
 *
 */
public class PController extends Controller {
    // ********** Fields **********//
    private double  proportionalGain;

    // ********** Constructor **********//
    public PController() {
        input            = 0.0;
        output           = 0.0;
        proportionalGain = 0.0;
        byPassed         = false;
    }

    // ********** Setters and Getters **********//
    public double getProportionalGain() {
        return proportionalGain;
    }
    public void setProportionalGain(double proportionalGain) {
        this.proportionalGain = proportionalGain;
    }
}
