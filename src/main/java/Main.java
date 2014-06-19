import gui.Gui;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by marlon on 6/13/14.
 */
public class Main extends Application {
    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Gui gui = new Gui();
        Scene scene = new Scene(gui);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
