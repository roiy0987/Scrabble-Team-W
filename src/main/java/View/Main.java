package View;

import Model.HostModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.stage.Screen;
import javafx.stage.Stage;
import test.BookScrabbleHandler;
import test.MyServer;

import java.io.File;
import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) throws IOException {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        MyServer s = new MyServer(8887,new BookScrabbleHandler());
        s.start();
        FXMLLoader fxmlLoader = null;
        String fxmlPath = "src/main/resources/ui/fxml/main-page.fxml";
        fxmlLoader = new FXMLLoader(new File(fxmlPath).toURI().toURL());
        Scene scene = new Scene(fxmlLoader.load(), Screen.getPrimary().getVisualBounds().getWidth(), Screen.getPrimary().getVisualBounds().getHeight());
        scene.getStylesheets().add(getClass().getResource("/ui/css/main-page.css").toExternalForm());
        MainPageController mp = fxmlLoader.getController();
        mp.setPage(stage);
        stage.setFullScreen(true);
//        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setScene(scene);
        stage.show();
    }

}
