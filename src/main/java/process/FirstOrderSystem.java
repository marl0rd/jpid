package process;

/**
 * Created by marlon on 6/16/14.
 *
 * The transfer function is in form:
 *
 * G(s) =   zero
 *         ------
 *         s+pole
 *
 */
public class FirstOrderSystem {
    private double zero;
    private double pole;

    public FirstOrderSystem(double zero) {
        this.zero = zero;
    }

    public double getZero() {
        return zero;
    }

    public void setZero(double zero) {
        this.zero = zero;
    }

    public double getPole() {
        return pole;
    }

    public void setPole(double pole) {
        this.pole = pole;
    }
}
