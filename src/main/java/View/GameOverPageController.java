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

    /**
     * The GameOverPageController function is the controller for the GameOverPage.fxml file.
     * It allows for a user to return to the main menu, or exit out of the game entirely.
     *
     * @return A gameoverpagecontroller object
     *
     */
    public GameOverPageController() throws IOException {
        mp = MainPageController.getMainPage();
    }

    /**
     * The leaveGame function is called when the user clicks on the &quot;Leave Game&quot; button.
     * It returns to the main menu scene and sets it to full screen.
     */
    public void leaveGame() throws IOException {
        stage.setScene(mp.getScene());
        stage.setFullScreen(true);
    }

    /**
     * The setStage function is used to set the stage of the application.
     *
     * @param stage Set the stage
     */
    public void setStage(Stage stage){
        this.stage = stage;
    }

    /**
     * The setVm function is used to set the ScrabbleViewModel object that will be used by this class.
     *
     * @param vm Set the vm variable to the one that was passed in
     */
    public void setVm(ScrabbleViewModel vm){
        this.vm = vm;
    }

    /**
     * The setPlayersList function takes in an ObservableList of Strings and sets the playersList
     * variable to that list. It then sorts the list by score, with highest scores first.
     *
     * @param list Set the playerslist observablelist
     */
    public void setPlayersList(ObservableList<String> list){
        list.sort((a,b)->{
            return b.split(":")[1].compareTo(a.split(":")[1]);
        });
        this.playersList.getItems().addAll(list);
    }

}
