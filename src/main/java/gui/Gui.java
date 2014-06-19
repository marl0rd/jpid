package gui;

import conicaltank.ConicalTankTransferFunction;
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
import java.sql.Timestamp;
import java.util.ResourceBundle;

/**
 * Created by marlon on 6/16/14.
 */
public class Gui extends AnchorPane implements Initializable{
    private static final int  TRENDING_DATA_LIMIT = 50;
    private static final long SAMPLING_TIME = 500;

    @FXML private TextField heightSetPointTextField;
    @FXML private Button    newHeightEnteredButton;
    @FXML private MenuItem  startSimulationMenuItem;
    @FXML private MenuItem  stopSimulationMenuItem;
    @FXML private Label     heightOperationPointLabel;
    @FXML private Label     inflowOperationPointLabel;
    @FXML private Label     samplingTimeLabel;
    @FXML private Label     kpLabel;
    @FXML private Label     kiLabel;
    @FXML private Label     tauLabel;
    @FXML private Label     gainLabel;
    @FXML private Label     inputLabel;
    @FXML private Label     outputLabel;

    @FXML private CategoryAxis                      categoryAxis;
    @FXML private NumberAxis                        numberAxis;
    @FXML private LineChart<Double, Timestamp>      trendings;
    private final XYChart.Series<Double, Timestamp> outputTrendingSeries;

    private ConicalTankTransferFunction conicalTank;
    private SystemSimulator             processSimulator;
    private long                        lastUpdate;

    public Gui() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Gui.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        outputTrendingSeries = new XYChart.Series<>();
        numberAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(numberAxis) {
            @Override
            public String toString(Number object) {
                return String.format("%6.2f", object);
            }
        });
        trendings.getData().add(outputTrendingSeries);

        conicalTank  = new ConicalTankTransferFunction();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        registerListeners();
    }

    private void registerListeners() {
        startSimulationMenuItem.setOnAction(value -> startSimulation());
        stopSimulationMenuItem.setOnAction(value -> stopSimulation());
        newHeightEnteredButton.setOnAction(value -> recalculate());

        heightOperationPointLabel.textProperty().bind(conicalTank.heightOperationPointProperty().asString());
        inflowOperationPointLabel.textProperty().bind(conicalTank.inflowOperationPointProperty().asString());
        samplingTimeLabel.textProperty().bind(processSimulator.getProcess().samplingTimeProperty().asString());
        tauLabel.textProperty().bind(processSimulator.getProcess().tauProperty().asString());
        gainLabel.textProperty().bind(processSimulator.getProcess().gainProperty().asString());
        inputLabel.textProperty().bind(processSimulator.getProcess().inputProperty().asString());
        outputLabel.textProperty().bind(processSimulator.getProcess().outputProperty().asString());
        processSimulator.timeStampProperty().addListener((observable, oldValue, newValue) -> updateTrending(newValue));
//        kpLabel;
//        kiLabel;
    }

    private void startSimulation(){
        lastUpdate       = System.currentTimeMillis();
        processSimulator = new SystemSimulator();
        processSimulator.run();
    }

    private void stopSimulation(){
        processSimulator.interrupt();
    }

    private void updateTrending(Timestamp currentTime){
        if ((System.currentTimeMillis() - lastUpdate) > SAMPLING_TIME) {
            outputTrendingSeries.getData().add(new XYChart.Data<>(processSimulator.getProcess().getOutput(), currentTime));
            while (outputTrendingSeries.getData().size() > TRENDING_DATA_LIMIT) {
                outputTrendingSeries.getData().remove(0);
            }
            lastUpdate = currentTime.getTime();
        }
    }

    private void recalculate() {
        conicalTank.setHeightOperationPoint(Double.parseDouble(heightOperationPointLabel.getText()));
        processSimulator.getProcess().setGain(conicalTank.getGain());
        processSimulator.getProcess().setTau(conicalTank.getTau());
    }

}
