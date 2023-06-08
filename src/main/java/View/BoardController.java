package View;

import ViewModel.ScrabbleViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Collections;

public class BoardController {

    ScrabbleViewModel vm;
    Stage stage;

    @FXML
    ListView playerTiles;
    @FXML
    ListView score;
    @FXML
    GridPane board;

    public void setViewModel(ScrabbleViewModel vm){
        this.vm = vm;
        playerTiles.itemsProperty().bindBidirectional(vm.getTiles());
        score.itemsProperty().bindBidirectional(vm.getScores());
    }

    public void submitWord(){
        vm.submitWord();
    }

    public void skipTurn(){
        vm.skipTurn();
    }

    public void shuffleTiles(){
        Collections.shuffle(playerTiles.getItems());
    }

    public void setVm(ScrabbleViewModel vm){
        this.vm = vm;
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }






}
