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
    private Loop               loop;

    @FXML private ChoiceBox<Preferences.ControlType>    controlTypeChoiceBox;
    @FXML private ChoiceBox<Preferences.Source>         sourceChoiceBox;
    @FXML private ChoiceBox<Preferences.ControllerType> controllerChoiceBox;
    @FXML private ChoiceBox<String>                     processChoiceBox;
    @FXML private TextField                             sourceValueTextField;
    @FXML private MenuItem                              startSimulationMenuItem;
    @FXML private MenuItem                              stopSimulationMenuItem;
    @FXML private MenuItem                              preferencesMenuItem;
    @FXML private Label                                 inputValueLabel;
    @FXML private Label                                 samplingTimeLabel;
    @FXML private Label                                 kpLabel;
    @FXML private Label                                 kiLabel;
    @FXML private Label                                 integralTimeLabel;
    @FXML private Label                                 tauLabel;
    @FXML private Label                                 gainLabel;
    @FXML private Label                                 outputValueLabel;
    @FXML private CategoryAxis                          categoryAxis;
    @FXML private NumberAxis                            numberAxis;
    @FXML private LineChart<String, Double>             trendings;
    @FXML private TextField                             dampingRatioTextField;
    @FXML private TextField                             settlingTimeTextField;
    private XYChart.Series<String, Double>              outputTrendingSeries;

    // ********** Constructor **********//
    public GuiController() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Gui.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        process     = new FirstOrderSystem();
        controller  = new PIController();
        piTuning    = new PITuning(controller, process);
        loop        = new Loop(process, controller);

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
        sourceChoiceBox.getItems().addAll(Preferences.Source.values());

    }

    private void registerListeners() {
        // GUI Changes:
        startSimulationMenuItem.setOnAction(value -> startSimulation());
        stopSimulationMenuItem.setOnAction(value -> stopSimulation());
        preferencesMenuItem.setOnAction(value -> showPreferences());

        // The loop inform about new values, so they are displayed in the gui:
        loop.timeStampProperty().addListener(updateGui);
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

        loop.setSamplingTime(Preferences.samplingTime);
        loop.setSimulationMode(Preferences.simulationMode);
        loop.setLoopType(Preferences.loopType);
    }

    private void startSimulation(){
        loop.start();
    }

    private void stopSimulation(){
        loop.interrupt();
        loop = new Loop(process, controller);
    }

    private void updateGui(){
        if(loop.getLoopStarted()){
            // Update Loop visualization
            outputTrendingSeries.getData().add(new XYChart.Data<>(TIME_FORMAT.format(Date.from(Instant.now())), loop.getOutput()));
            while (outputTrendingSeries.getData().size() > TRENDING_DATA_LIMIT) {
                outputTrendingSeries.getData().remove(0);
            }
            consoleListView.getItems().add("I= " + loop.getInput() + ", \t O=" + loop.getOutput());
            while(consoleListView.getItems().size() > CONSOLE_DATA_LIMIT) {
                consoleListView.getItems().remove(0);
            }
        }

        samplingTimeLabel.setText(Double.toString(loop.getSamplingTime()));

        // Update conicalTank visualization
        heightOperationPointLabel.setText(String.valueOf(conicalTank.getHeightOperationPoint()));
        inflowOperationPointLabel.setText(String.valueOf(conicalTank.getInflowOperationPoint()));

        // Update Process visualization
        tauLabel.setText(Double.toString(loop.getProcess().getTau()));
        gainLabel.setText(Double.toString(loop.getProcess().getGain()));
        inputLabel.setText(Double.toString(loop.getProcess().getInput()));
        outputLabel.setText(Double.toString(loop.getProcess().getOutput()));

        // Update Controller visualization
        kpLabel.setText(Double.toString(loop.getController().getProportionalGain()));
        kiLabel.setText(Double.toString(loop.getController().getIntegralGain()));
        integralTimeLabel.setText(Double.toString(loop.getController().getIntegralTime()));
    }

    private InvalidationListener updateGui = new InvalidationListener() {
        @Override
        public void invalidated(Observable observable) {
            updateGui();
            loop.timeStampProperty().removeListener(this);
            loop.timeStampProperty().addListener(this);
        }
    };

    private void recalculate() {
        linealizeInNewSetPoint();
        tuneController();
        loop.setInput(Double.parseDouble(heightSetPointTextField.getText()));
        updateGui();
    }

    private void linealizeInNewSetPoint(){
        conicalTank.setInflowOperationPoint(Double.parseDouble(flowSetPointTextField.getText()));
        conicalTank.setHeightOperationPoint(Double.parseDouble(heightSetPointTextField.getText()));
    }

    private void tuneController() {
        piTuning.setDampingRatio(Double.parseDouble(dampingRatioTextField.getText()));
        piTuning.setSettlingTime(Double.parseDouble(settlingTimeTextField.getText()));
    }

}
