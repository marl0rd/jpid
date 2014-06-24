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
    private FirstOrderSystem    transferFunction;


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
        transferFunction = new FirstOrderSystem();
    }


    // ********** Methods **********//
    private void recalculate(){

        double alpha = ((9/2) *
                (OBSTRUCTION * Math.pow(HEIGHT,2) * Math.sqrt(2*GRAVITY) * Math.pow(heightOperationPoint.get(), -5/2)) /
                (2 * Math.PI * Math.pow(RADIUS,2))) -
                ((6 * Math.pow(HEIGHT,2) * inflowOperationPoint.get() * Math.pow(heightOperationPoint.get(),-3)) /
                (Math.PI * Math.pow(RADIUS,2)));

        double beta = (3 * Math.pow(HEIGHT,2) * Math.pow(heightOperationPoint.get(),-2)) /
                (Math.PI * Math.pow(RADIUS,2));

        transferFunction.setGain(beta / alpha);
        transferFunction.setTau(1 / alpha);
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

    public FirstOrderSystem getTransferFunction() {
        return transferFunction;
    }
    public void setTransferFunction(FirstOrderSystem transferFunction) {
        this.transferFunction = transferFunction;
    }
}
