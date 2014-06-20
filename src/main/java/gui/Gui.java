package gui;

import conicaltank.ConicalTankTransferFunction;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import process.SystemSimulator;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ResourceBundle;

/**
 * Created by marlon on 6/16/14.
 */
public class Gui extends AnchorPane {
    private static final int                  TRENDING_DATA_LIMIT        = 50;
    private static final SimpleDateFormat     timeFormat                 = new SimpleDateFormat("mm:ss.SSS");
    private final ConicalTankTransferFunction conicalTank                = new ConicalTankTransferFunction();
    private final SystemSimulator             processSimulator           = new SystemSimulator();
    @FXML private TextField                   heightSetPointTextField;
    @FXML private Button                      newHeightEnteredButton;
    @FXML private MenuItem                    startSimulationMenuItem;
    @FXML private MenuItem                    stopSimulationMenuItem;
    @FXML private Label                       heightOperationPointLabel;
    @FXML private Label                       inflowOperationPointLabel;
    @FXML private Label                       samplingTimeLabel;
    @FXML private Label                       kpLabel;
    @FXML private Label                       kiLabel;
    @FXML private Label                       tauLabel;
    @FXML private Label                       gainLabel;
    @FXML private Label                       inputLabel;
    @FXML private Label                       outputLabel;
    @FXML private CategoryAxis                categoryAxis;
    @FXML private NumberAxis                  numberAxis;
    @FXML private LineChart<String, Double>   trendings;
    private XYChart.Series<String, Double>    outputTrendingSeries;


    public Gui() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Gui.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        initializeLineChart();
        registerListeners();
    }

    private void initializeLineChart() {
        outputTrendingSeries = new XYChart.Series<>();
        numberAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(numberAxis) {
            @Override
            public String toString(Number object) {
                return String.format("%6.2f", object);
            }
        });
        trendings.getData().add(outputTrendingSeries);
    }

    private void registerListeners() {
        startSimulationMenuItem.setOnAction(value -> startSimulation());
        stopSimulationMenuItem.setOnAction(value -> stopSimulation());
        newHeightEnteredButton.setOnAction(value -> recalculate());

        heightOperationPointLabel.textProperty().bind(conicalTank.heightOperationPointProperty().asString());
        inflowOperationPointLabel.textProperty().bind(conicalTank.inflowOperationPointProperty().asString());
        processSimulator.timeStampProperty().addListener((observable, oldValue, newValue) -> updateGui(newValue));
//        kpLabel;
//        kiLabel;
    }

    private void startSimulation(){
        lastUpdate       = System.currentTimeMillis();
        processSimulator.start();
    }

    private void stopSimulation(){
        processSimulator.interrupt();
        processSimulator = new SystemSimulator();
    }

    private void updateGui(Number currentTime){
        Platform.runLater(() -> {
            outputTrendingSeries.getData().add(new XYChart.Data<>(timeFormat.format(Date.from(Instant.now())), processSimulator.getProcess().getOutput()));
            while (outputTrendingSeries.getData().size() > TRENDING_DATA_LIMIT) {
                outputTrendingSeries.getData().remove(0);
            }
            lastUpdate = currentTime.longValue();

            samplingTimeLabel.setText(Double.toString(processSimulator.getProcess().getSamplingTime()));
            tauLabel.setText(Double.toString(processSimulator.getProcess().getTau()));
            gainLabel.setText(Double.toString(processSimulator.getProcess().getGain()));
            inputLabel.setText(Double.toString(processSimulator.getProcess().getInput()));
            outputLabel.setText(Double.toString(processSimulator.getProcess().getOutput()));

        });

    }

    private void recalculate() {
        linealizeInNewSetPoint();
        updateSimulationProcess();
    }

    private void linealizeInNewSetPoint(){
        conicalTank.setInflowOperationPoint(1.0);
        conicalTank.setHeightOperationPoint(Double.parseDouble(heightSetPointTextField.getText()));
    }

    private void updateSimulationProcess(){
        processSimulator.getProcess().setGain(conicalTank.getGain());
        processSimulator.getProcess().setTau(conicalTank.getTau());
    }

}
