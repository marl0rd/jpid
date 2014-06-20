package process;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * Created by marlon on 6/19/14.
 */
public class PISystem {
    private DoubleProperty proportionalGain;
    private DoubleProperty integralGain;
    private DoubleProperty samplingTime;

    public PISystem() {
        proportionalGain = new DoublePropertyBase() {
            @Override
            public Object getBean() {
                return this;
            }

            @Override
            public String getName() {
                return "proportionalGain";
            }
        };
        integralGain     = new DoublePropertyBase() {
            @Override
            public Object getBean() {
                return this;
            }

            @Override
            public String getName() {
                return "integralGain";
            }
        };
        samplingTime     = new SimpleDoubleProperty(this, "samplingTime", 0.01);
    }

    public double getProportionalGain() {
        return proportionalGain.get();
    }
    public DoubleProperty proportionalGainProperty() {
        return proportionalGain;
    }
    public void setProportionalGain(double proportionalGain) {
        this.proportionalGain.set(proportionalGain);
    }

    public double getIntegralGain() {
        return integralGain.get();
    }
    public DoubleProperty integralGainProperty() {
        return integralGain;
    }
    public void setIntegralGain(double integralGain) {
        this.integralGain.set(integralGain);
    }

    public double getSamplingTime() {
        return samplingTime.get();
    }
    public DoubleProperty samplingTimeProperty() {
        return samplingTime;
    }
    public void setSamplingTime(double samplingTime) {
        this.samplingTime.set(samplingTime);
    }
}
