package gui;

import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.sql.Timestamp;

/**
 * Created by marlon on 6/16/14.
 */
public class Gui extends AnchorPane{
    private static final int  TRENDING_DATA_LIMIT = 50;
    private static final long SAMPLING_TIME = 500;

    @FXML private TextField                    tankAreaTextField;
    @FXML private TextField                    gravityTextField;
    @FXML private TextField                    obstructionTextField;
    @FXML private TextField                    samplingTimeTextField;
    @FXML private TextField                    kp;
    @FXML private TextField                    Ti;
    @FXML private Button                       applyButton;
    @FXML private CategoryAxis                 categoryAxis;
    @FXML private NumberAxis                   numberAxis;
    @FXML private LineChart<Double, Timestamp> trendings;

    private DoubleProperty            output;
    private long                      lastUpdate;
    private ObjectProperty<Timestamp> timestamp;

    private final XYChart.Series<Double, Timestamp> systemOutput = new XYChart.Series<>();

    public Gui() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Gui.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        output      = new SimpleDoubleProperty(this, "systemOutput", 0.0);
        lastUpdate  = System.currentTimeMillis();
        timestamp   = new ObjectPropertyBase<Timestamp>(new Timestamp(System.currentTimeMillis())) {
            @Override protected void invalidated() {
                if ((System.currentTimeMillis() - lastUpdate) > SAMPLING_TIME) {
                    systemOutput.getData().add(new XYChart.Data<>(output.getValue(), new Timestamp(System.currentTimeMillis())));
                    while (systemOutput.getData().size() > TRENDING_DATA_LIMIT) {
                        systemOutput.getData().remove(0);
                    }
                }
            }
            @Override public Object getBean() {
                return this;
            }
            @Override public String getName() {
                return "timestamp";
            }
        };

        numberAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(numberAxis) {
            @Override
            public String toString(Number object) {
                return String.format("%6.2f", object);
            }
        });
        trendings.getData().add(systemOutput);
    }

}
