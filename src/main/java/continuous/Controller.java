package continuous;

/**
 * Created by marlon on 6/24/14.
 */
public class Controller {
    // ********** Fields **********//
    protected double  input;
    protected double  output;
    protected boolean byPassed;

    // ********** Constructor **********//
    public Controller() {
    }

    // ********** Setters and Getters **********//
    public double getInput() {
        return input;
    }
    public void setInput(double input) {
        this.input = input;
    }

    public double getOutput() {
        if(byPassed){
            return input;
        } else {
            return output;
        }
    }
    public void setOutput(double output) {
        this.output = output;
    }

    public boolean isByPassed() {
        return byPassed;
    }
    public void setByPassed(boolean byPassed) {
        this.byPassed = byPassed;
    }
}
