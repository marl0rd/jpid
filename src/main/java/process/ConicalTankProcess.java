package process;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import continuous.FirstOrderSystem;

/**
 * Created by marlon on 6/18/14.
 *
 */
public class ConicalTankProcess extends FirstOrderSystem{

    // ********** Fields **********//
    private static final double HEIGHT                = 100.0;
    private static final double RADIUS                = 40.0;
    private static final double GRAVITY               = 9.8;
    private static final double OBSTRUCTION           = 1.5;
    private DoubleProperty      heightOperationPoint;
    private DoubleProperty      inflowOperationPoint;


    // ********** Constructor **********//
    public ConicalTankProcess() {
        this.heightOperationPoint = new DoublePropertyBase(0.0001) {
            @Override protected void invalidated() {
                recalculate();
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
    }


    // ********** Methods **********//
    private void recalculate(){
        double k = (Math.pow(RADIUS, 2) * Math.PI ) / (Math.pow(HEIGHT, 2));

        double alpha = ((3 * OBSTRUCTION * Math.sqrt(2*GRAVITY) * Math.pow(getHeightOperationPoint(),-5/2)) / (2 * k)) -
                ((2 * getInflowOperationPoint() * Math.pow(getHeightOperationPoint(), -3)) / (k));

        double beta = Math.pow(getHeightOperationPoint(), -2) / k;

        setGain(beta / alpha);
        setTau(1 / alpha);
    }

    // ********** Setters and Getters **********//
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
