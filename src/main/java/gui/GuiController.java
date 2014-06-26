package gui;

import continuous.FirstOrderSystem;
import continuous.PController;
import continuous.PIController;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import process.ConicalTankProcess;
import simulation.*;
import source.Constant;
import source.Source;
import techniques.PITuning;
import util.Preferences;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;

/**
 * Created by marlon on 6/16/14.
 */
public class GuiController extends AnchorPane {
    // ********** Static Fields **********//
    private static final int                  TRENDING_DATA_LIMIT        = 50;
    private static final SimpleDateFormat     TIME_FORMAT                = new SimpleDateFormat("mm:ss.SSS");

    // ********** Fields **********//
    private Source           source;
    private FirstOrderSystem process;
    private PIController     controller;
    private Simulator        simulator;

    @FXML private ChoiceBox<Preferences.ControlType> controlTypeChoiceBox;
    @FXML private ChoiceBox<Preferences.Source>      sourceChoiceBox;
    @FXML private ChoiceBox<Preferences.Controller>  controllerChoiceBox;
    @FXML private ChoiceBox<Preferences.Process>     processChoiceBox;
    @FXML private TextField                          sourceValueTextField;
    @FXML private MenuItem                           startSimulationMenuItem;
    @FXML private MenuItem                           stopSimulationMenuItem;
    @FXML private MenuItem                           preferencesMenuItem;
    @FXML private Label                              inputValueLabel;
    @FXML private Label                              samplingTimeLabel;
    @FXML private Label                              kpLabel;
    @FXML private Label                              kiLabel;
    @FXML private Label                              tauLabel;
    @FXML private Label                              gainLabel;
    @FXML private Label                              outputValueLabel;
    @FXML private CategoryAxis                       categoryAxis;
    @FXML private NumberAxis                         numberAxis;
    @FXML private LineChart<String, Double>          trendings;
    @FXML private TextField                          dampingRatioTextField;
    @FXML private TextField                          settlingTimeTextField;
    private XYChart.Series<String, Double>           outputTrendingSeries;

    // ********** Constructor **********//
    public GuiController() {
        // Load the FXML
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Gui.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        initializeGraphics();
        registerListeners();
    }

    // ********** Methods **********//
    private void initializeGraphics() {
        //LineCharts
        outputTrendingSeries = new XYChart.Series<>();
        numberAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(numberAxis) {
            @Override
            public String toString(Number object) {
                return String.format("%6.2f", object);
            }
        });
        trendings.getData().add(outputTrendingSeries);

        // ChoiceBoxes
        controlTypeChoiceBox.getItems().addAll(Preferences.ControlType.values());
        controlTypeChoiceBox.getSelectionModel().select(Preferences.controlType);
        sourceChoiceBox.getItems().addAll(Preferences.Source.values());
        sourceChoiceBox.getSelectionModel().select(Preferences.source);
        controllerChoiceBox.getItems().addAll(Preferences.Controller.values());
        controllerChoiceBox.getSelectionModel().select(Preferences.controller);
        processChoiceBox.getItems().addAll(Preferences.Process.values());
        processChoiceBox.getSelectionModel().select(Preferences.Process.CONICAL_TANK);
    }

    private void registerListeners() {
        // GUI Changes:
        startSimulationMenuItem.setOnAction(value -> startSimulation());
        stopSimulationMenuItem.setOnAction(value -> stopSimulation());
        preferencesMenuItem.setOnAction(value -> showPreferences());
    }

    private void showPreferences() {
        PreferencesController preferencesController = new PreferencesController();
        Scene scene = new Scene(preferencesController);
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.setScene(scene);
        stage.showAndWait();
        // Refresh new Preferences
        Preferences.samplingTime   = preferencesController.getSelectedSamplingTime();
        Preferences.simulationSpeed = preferencesController.getSelectedSimulationMode();
        Preferences.loopType       = preferencesController.getSelectedLoopType();

        simulator.setSamplingTime(Preferences.samplingTime);
        simulator.setSimulationSpeed(Preferences.simulationSpeed);
        simulator.setLoopType(Preferences.loopType);
    }

    private void startSimulation(){
        switch (sourceChoiceBox.getSelectionModel().getSelectedItem()) {
            case CONSTANT:
                source = new Constant(Double.parseDouble(sourceValueTextField.getText()));
                break;
            case PUSE_GENERATOR:
                //TODO
                break;
            case RAMP:
                //TODO
                break;
            case RANDOM:
                //TODO
                break;
            case SINE_WAVE:
                //TODO
                break;
            case STEP:
                //TODO
                break;
        }

        switch (processChoiceBox.getSelectionModel().getSelectedItem()) {
            case NONE:
                process.setGain(1.0);
                process.setTau(0.0);
                break;
            case SIMPLE_FIRST_ORDER:
                process.setGain(1.0);
                process.setTau(1.0);
                break;
            case CONICAL_TANK:
                ConicalTankProcess conicalTankProcess = new ConicalTankProcess();
                conicalTankProcess.setHeightOperationPoint(5.0);
                conicalTankProcess.setInflowOperationPoint(1.0);
                process = conicalTankProcess;
                break;
        }

        switch (controllerChoiceBox.getSelectionModel().getSelectedItem()) {
            case NONE:
                controller = new PIController();
                controller.setByPassed(true);
                break;
            case P:
                //TODO
                break;
            case PI:
                // Create the controller
                controller = new PIController();
                // Create a PITuning instance and configure it
                PITuning piTuning = new PITuning(process);
                piTuning.setDampingRatio(Double.parseDouble(dampingRatioTextField.getText()));
                piTuning.setSettlingTime(Double.parseDouble(settlingTimeTextField.getText()));
                // Set the calculate gains in the controller
                controller.setProportionalGain(piTuning.getProportionalGain());
                controller.setIntegralGain(piTuning.getIntegralGain());
                break;
            case PID:
                //TODO
                break;
        }

        simulator = new Simulator(source, controller, process);
        simulator.timeStampProperty().addListener(updateGui);
        simulator.start();
    }

    private void stopSimulation(){
        simulator.interrupt();
    }

    private void updateGui(){
        if(simulator.getState().equals(Thread.State.RUNNABLE)){
            // Update Loop visualization
            outputTrendingSeries.getData().add(new XYChart.Data<>(TIME_FORMAT.format(Date.from(Instant.now())), simulator.getOutput()));
            while (outputTrendingSeries.getData().size() > TRENDING_DATA_LIMIT) {
                outputTrendingSeries.getData().remove(0);
            }
        }

        samplingTimeLabel.setText(Double.toString(simulator.getSamplingTime()));

        // Update Process visualization
        tauLabel.setText(Double.toString(simulator.getProcess().getTau()));
        gainLabel.setText(Double.toString(simulator.getProcess().getGain()));
        inputValueLabel.setText(Double.toString(simulator.getSource().getOutput()));
        outputValueLabel.setText(Double.toString(simulator.getProcess().getOutput()));

        // Update Controller visualization
        kpLabel.setText(Double.toString(simulator.getController().getProportionalGain()));
        kiLabel.setText(Double.toString(simulator.getController().getIntegralGain()));
    }

    private InvalidationListener updateGui = new InvalidationListener() {
        @Override
        public void invalidated(Observable observable) {
            updateGui();
            simulator.timeStampProperty().removeListener(this);
            simulator.timeStampProperty().addListener(this);
        }
    };

}
