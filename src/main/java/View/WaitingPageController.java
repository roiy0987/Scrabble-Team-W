package View;

import ViewModel.ScrabbleViewModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class WaitingPageController {

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
        playersList.itemsProperty().bind(vm.getScores());
        System.out.println();
        if(!host){
            startGame.setStyle("-fx-opacity:0");
        }
    }

    public void startGame() throws IOException {
        FXMLLoader fxmlLoader = null;
        String fxmlPath = "src/main/resources/ui/fxml/board-page.fxml";
        fxmlLoader = new FXMLLoader(new File(fxmlPath).toURI().toURL());
        Scene scene = new Scene(fxmlLoader.load(), Screen.getPrimary().getVisualBounds().getWidth(), Screen.getPrimary().getVisualBounds().getHeight());
        stage.setScene(scene);
        BoardController bc = fxmlLoader.getController();
        bc.setViewModel(vm);
    }

    public void setIsHost(boolean isHost){
        this.host = isHost;
    }


}
