package gui;

import continuous.FirstOrderSystem;
import continuous.PIController;
import continuous.PITuning;
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
import simulation.*;
import source.Source;
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
    private static final int                  CONSOLE_DATA_LIMIT         = 50;
    private static final SimpleDateFormat     TIME_FORMAT                = new SimpleDateFormat("mm:ss.SSS");

    // ********** Fields **********//
    private Source             source;
    private FirstOrderSystem   process;
    private PIController       controller;
    private PITuning           piTuning;
    private Simulator          simulator;

    @FXML private ChoiceBox<Preferences.ControlType> controlTypeChoiceBox;
    @FXML private ChoiceBox<Preferences.Source>      sourceChoiceBox;
    @FXML private ChoiceBox<Preferences.Controller>  controllerChoiceBox;
    @FXML private ChoiceBox<String>                  processChoiceBox;
    @FXML private TextField                          sourceValueTextField;
    @FXML private MenuItem                           startSimulationMenuItem;
    @FXML private MenuItem                           stopSimulationMenuItem;
    @FXML private MenuItem                           preferencesMenuItem;
    @FXML private Label                              inputValueLabel;
    @FXML private Label                              samplingTimeLabel;
    @FXML private Label                              kpLabel;
    @FXML private Label                              kiLabel;
    @FXML private Label                              integralTimeLabel;
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

        simulator  = new Simulator(process, controller);
        process    = new FirstOrderSystem();
        controller = new PIController();
        piTuning   = new PITuning(controller, process);

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
        processChoiceBox.getItems().addAll("ConicalTank");
        processChoiceBox.getSelectionModel().select(0);
    }

    private void registerListeners() {
        // GUI Changes:
        startSimulationMenuItem.setOnAction(value -> startSimulation());
        stopSimulationMenuItem.setOnAction(value -> stopSimulation());
        preferencesMenuItem.setOnAction(value -> showPreferences());

        // The loop inform about new values, so they are displayed in the gui:
        simulator.timeStampProperty().addListener(updateGui);
    }

    private void showPreferences() {
        PreferencesController preferencesController = new PreferencesController();
        Scene scene = new Scene(preferencesController);
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.setScene(scene);
        stage.showAndWait();
        // Refresh new Preferences
        Preferences.samplingTime   = preferencesController.getSelectedSamplingTime();
        Preferences.simulationMode = preferencesController.getSelectedSimulationMode();
        Preferences.loopType       = preferencesController.getSelectedLoopType();

        simulator.setSamplingTime(Preferences.samplingTime);
        simulator.setSimulationMode(Preferences.simulationMode);
        simulator.setLoopType(Preferences.loopType);
    }

    private void startSimulation(){
        simulator.start();
    }

    private void stopSimulation(){
        simulator.interrupt();
        simulator = new Simulator(process, controller);
    }

    private void updateGui(){
        if(simulator.getLoopStarted()){
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
        inputValueLabel.setText(Double.toString(simulator.getProcess().getInput()));
        outputValueLabel.setText(Double.toString(simulator.getProcess().getOutput()));

        // Update Controller visualization
        kpLabel.setText(Double.toString(simulator.getController().getProportionalGain()));
        kiLabel.setText(Double.toString(simulator.getController().getIntegralGain()));
        integralTimeLabel.setText(Double.toString(simulator.getController().getIntegralTime()));
    }

    private InvalidationListener updateGui = new InvalidationListener() {
        @Override
        public void invalidated(Observable observable) {
            updateGui();
            simulator.timeStampProperty().removeListener(this);
            simulator.timeStampProperty().addListener(this);
        }
    };

    private void recalculate() {
        tuneController();
        updateGui();
    }

    private void tuneController() {
        piTuning.setDampingRatio(Double.parseDouble(dampingRatioTextField.getText()));
        piTuning.setSettlingTime(Double.parseDouble(settlingTimeTextField.getText()));
    }

}
