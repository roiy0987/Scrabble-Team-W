package View;

import ViewModel.ScrabbleViewModel;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import java.io.IOException;

public class GameOverPageController {
    MainPageController mp;
    Stage stage;
    ScrabbleViewModel vm;
    @FXML
    ListView playersList;

    public GameOverPageController() throws IOException {
        mp = MainPageController.getMainPage();
    }

    public void leaveGame() throws IOException {
        stage.setScene(mp.getScene());
        stage.setFullScreen(true);
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }

    public void setVm(ScrabbleViewModel vm){
        this.vm = vm;
    }

    public void setPlayersList(ObservableList<String> list){
        list.sort((a,b)->{
            return b.split(":")[1].compareTo(a.split(":")[1]);
        });
        this.playersList.getItems().addAll(list);
    }

}
