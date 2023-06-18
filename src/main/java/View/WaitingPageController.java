package View;

import ViewModel.ScrabbleViewModel;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
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
    private BooleanProperty disconnect;

    public WaitingPageController(){}

    public void setStage(Stage stage){
        this.stage = stage;
    }

    public void setViewModel(ScrabbleViewModel vm){
        this.vm = vm;
        disconnect = new SimpleBooleanProperty(false);
        disconnect.bindBidirectional(this.vm.getDisconnect());
        disconnect.addListener((observable, oldValue, newValue)->{
            if(newValue){
                Platform.runLater(()->stage.close());
            }
        });
        if(host){
            playersList.getItems().clear();
            playersList.itemsProperty().bind(vm.getScores());
        }else{
            startGame.setStyle("-fx-opacity:0");
            vm.getGameStartedProperty().addListener((observable, oldValue, newValue)->{
                if(newValue) {
                    Platform.runLater(()->{
                        try {
                            this.startGame();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            });
        }

    }

    public void startGame() throws IOException {
        vm.startGame();
        swapScene();
    }

    private void swapScene() throws IOException {
        FXMLLoader fxmlLoader = null;
        String fxmlPath = "src/main/resources/ui/fxml/board-page.fxml";
        fxmlLoader = new FXMLLoader(new File(fxmlPath).toURI().toURL());
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(getClass().getResource("/ui/css/board-page.css").toExternalForm());
        BoardController bc = fxmlLoader.getController();
        bc.setStage(stage);
        bc.setViewModel(vm);
        bc.initWindow();
        stage.setOnCloseRequest((WindowEvent event) -> {
            event.consume(); // Consume the event to prevent automatic window closure

            // Show a confirmation dialog
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to exit?");
            alert.showAndWait()
                    .filter(response -> response == ButtonType.OK) // Handle the user's choice
                    .ifPresent(response ->{
                        Platform.runLater(()->{
                            System.out.println("Disconnected!");
                            vm.disconnect();
                            stage.close();
                        });
                    } );
        });
        stage.setScene(scene);
        stage.setFullScreen(true);
//        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
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
