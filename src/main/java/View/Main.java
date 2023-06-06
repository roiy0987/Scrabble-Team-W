package View;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) throws IOException {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        View view = new View(stage);

    }

}
