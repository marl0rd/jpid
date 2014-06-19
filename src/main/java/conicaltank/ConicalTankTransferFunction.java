package conicaltank;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;

/**
 * Created by marlon on 6/18/14.
 *
 * G(s) =   gain
 *        ---------
 *        tau*s + 1
 */
public class ConicalTankTransferFunction {

    // PROPERTIES //
    private static final double HEIGHT      = 10.0;
    private static final double RADIUS      = 5.0;
    private static final double GRAVITY     = 9.8;
    private static final double OBSTRUCTION = 1.5;

    private DoubleProperty heightOperationPoint;
    private DoubleProperty inflowOperationPoint;

    private double gain;
    private double tau;

    // CONSTRUCTOR //
    public ConicalTankTransferFunction() {
        this.heightOperationPoint = new DoublePropertyBase(0.0001) {
            @Override protected void invalidated() {
                set(get());
            }
            @Override public Object getBean() {
                return this;
            }
            @Override public String getName() {
                return "heightOperationPoint";
            }
        };
        this.inflowOperationPoint = new DoublePropertyBase(1.0) {
            @Override
            protected void invalidated() {
                recalculate();
                set(get());
            }
            @Override public Object getBean() {
                return this;
            }
            @Override public String getName() {
                return "inflowOperationPoint";
            }
        };
        recalculate();
    }

    // METHODS //
    private void recalculate(){
        double alpha = ((9/2) *
                (OBSTRUCTION * Math.pow(HEIGHT,2) * Math.sqrt(2*GRAVITY) * Math.pow(heightOperationPoint.get(), -5/2)) /
                (2 * Math.PI * Math.pow(RADIUS,2))) -
                ((6 * Math.pow(HEIGHT,2) * inflowOperationPoint.get() * Math.pow(heightOperationPoint.get(),-3)) /
                (Math.PI * Math.pow(RADIUS,2)));

        double beta = (3 * Math.pow(HEIGHT,2) * Math.pow(heightOperationPoint.get(),-2)) /
                (Math.PI * Math.pow(RADIUS,2));

        gain = beta / alpha;
        tau  = 1 / alpha;
    }

    // SETTERS AND GETTERS //
    public double getGain() {
        recalculate();
        return gain;
    }
    public double getTau() {
        recalculate();
        return tau;
    }


    public double getHeightOperationPoint() {
        return heightOperationPoint.get();
    }
    public DoubleProperty heightOperationPointProperty() {
        return heightOperationPoint;
    }
    public void setHeightOperationPoint(double heightOperationPoint) {
        this.heightOperationPoint.set(heightOperationPoint);
    }

    public double getInflowOperationPoint() {
        return inflowOperationPoint.get();
    }
    public DoubleProperty inflowOperationPointProperty() {
        return inflowOperationPoint;
    }
    public void setInflowOperationPoint(double inflowOperationPoint) {
        this.inflowOperationPoint.set(inflowOperationPoint);
    }
}
