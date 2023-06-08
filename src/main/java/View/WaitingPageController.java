package View;

import ViewModel.ScrabbleViewModel;
import javafx.beans.binding.Bindings;
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
        // Retrieve the list property from the ViewModel
        ObservableList<String> originalList = vm.getScores();
        // Create a new ObservableList to store the modified data
        ObservableList<String> modifiedList = FXCollections.observableArrayList();
        // Apply changes to the data in the list (e.g., split inner string and modify values)
        for (String item : originalList) {
            String[] parts = item.split(":"); // Split the inner string
            modifiedList.add(parts[0]);
        }
        // Set the modified list to the items property of the ListView
        playersList.setItems(modifiedList);

        System.out.println();
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
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        BoardController bc = fxmlLoader.getController();
        bc.setViewModel(vm);
    }

    public void setIsHost(boolean isHost){
        this.host = isHost;
    }


}
