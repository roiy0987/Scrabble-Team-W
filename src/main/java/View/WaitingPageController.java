package View;

import ViewModel.ScrabbleViewModel;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCombination;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class WaitingPageController implements Observer {

    @FXML
    ListView playersList;
    @FXML
    Button startGame;
    Stage stage;

    boolean host = false;

    ScrabbleViewModel vm;

    public WaitingPageController(){}

    public void setStage(Stage stage){
        this.stage = stage;
    }

    public void setViewModel(ScrabbleViewModel vm){
        this.vm = vm;
        vm.addObserver(this);
        playersList.getItems().clear();
        playersList.itemsProperty().bind(vm.getScores());
        System.out.println(vm.getScores());
        if(!host){
            startGame.setStyle("-fx-opacity:0");
        }
    }

    public void startGame() throws IOException {
        FXMLLoader fxmlLoader = null;
        String fxmlPath = "src/main/resources/ui/fxml/board-page.fxml";
        fxmlLoader = new FXMLLoader(new File(fxmlPath).toURI().toURL());
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(getClass().getResource("/ui/css/board-page.css").toExternalForm());
        vm.startGame();
        BoardController bc = fxmlLoader.getController();
        bc.setViewModel(vm);
        bc.initWindow();
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
    }

    public void setIsHost(boolean isHost){
        this.host = isHost;

    }

    @Override
    public void update(Observable o, Object arg) {
        try {
            System.out.println("123");
            this.startGame();
        } catch (IOException e) {
            System.out.println("123");
        }
    }
}
