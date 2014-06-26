package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import util.Preferences;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import static util.Preferences.SimulationSpeed;
import static util.Preferences.LoopType;

/**
 * Created by marlon on 6/20/14.
 */
public class PreferencesController extends GridPane implements Initializable{
    @FXML private ChoiceBox<SimulationSpeed> simulationModeCheckBox;
    @FXML private ChoiceBox<LoopType>        loopTypeChoiceBox;
    @FXML private TextField                  samplingTimeTextField;

    public PreferencesController() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Preferences.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        simulationModeCheckBox.getItems().addAll(SimulationSpeed.values());
        loopTypeChoiceBox.getItems().addAll(LoopType.values());
        simulationModeCheckBox.getSelectionModel().select(Preferences.simulationSpeed);
        loopTypeChoiceBox.getSelectionModel().select(Preferences.loopType);
        samplingTimeTextField.setText(Double.toString(Preferences.samplingTime));
    }

    public SimulationSpeed getSelectedSimulationMode() {
        return simulationModeCheckBox.getSelectionModel().getSelectedItem();
    }

    public double getSelectedSamplingTime() {
        double selectedSamplingTime;
        try{
            selectedSamplingTime = Double.parseDouble(samplingTimeTextField.getText());
        } catch (Exception e){
            System.out.println("The user has selected a wrong sampling time. Setting the default....");
            selectedSamplingTime = Preferences.samplingTime;
        }
        return selectedSamplingTime;
    }

    public LoopType getSelectedLoopType(){
        return loopTypeChoiceBox.getSelectionModel().getSelectedItem();
    }
}
