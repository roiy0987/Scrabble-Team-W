package View;

import Model.GuestModel;
import Model.HostModel;
import ViewModel.ScrabbleViewModel;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;

public class MainPageController {

    ScrabbleViewModel vm;
    @FXML
    TextField nickname;
    @FXML
    Button editButton;
    @FXML
    TextField ip;
    @FXML
    TextField port;
    @FXML
    DialogPane dialog;

    private TextField textField;

    private boolean isHost = false;
    private BooleanProperty disconnect;

    Stage stage;

    private static MainPageController singleton_instace = null;

    public static MainPageController getMainPage() throws IOException {
        if(singleton_instace == null)
            singleton_instace = new MainPageController();
        return singleton_instace;
    }

    public Scene getScene() throws IOException {
        FXMLLoader fxmlLoader = null;
        String fxmlPath = "src/main/resources/ui/fxml/main-page.fxml";
        fxmlLoader = new FXMLLoader(new File(fxmlPath).toURI().toURL());
        Scene scene = new Scene(fxmlLoader.load(), Screen.getPrimary().getVisualBounds().getWidth(), Screen.getPrimary().getVisualBounds().getHeight());
        scene.getStylesheets().add(getClass().getResource("/ui/css/main-page.css").toExternalForm());
        return scene;
    }

    public void joinGame() throws IOException {
        System.out.println("Join clicked!");
        System.out.println(Integer.parseInt(port.getText()));
        GuestModel guest = null;
        try{
            guest = new GuestModel(nickname.getText(), ip.getText(), Integer.parseInt(port.getText()));
        }catch (IOException e){
            e.printStackTrace();
            dialog.setContentText(e.getMessage());
        }
        if(guest!=null){
            System.out.println("11111");
            vm = new ScrabbleViewModel(guest);
            this.swapScenes();
        }
    }

    public void hostGame() throws IOException {
        System.out.println("Host clicked!");
        HostModel host = null;
        try{
            host = new HostModel(nickname.getText());
        }catch (IOException e){
            e.printStackTrace();
            dialog.setContentText(e.getMessage());
        }
        if(host!=null){
            vm = new ScrabbleViewModel(host);
            isHost = true;
            this.swapScenes();
        }
    }

    public void editParameters() {
        dialog.setContentText("");
        if(editButton.getText().equals("EDIT")){
            System.out.println("Edit nickname clicked! "+ nickname.getText());
            nickname.setEditable(true);
            ip.setEditable(true);
            port.setEditable(true);
            editButton.setText("SAVE");
        }else {
            System.out.println("Save nickname clicked! "+ nickname.getText());
            nickname.setEditable(false);
            ip.setEditable(false);
            port.setEditable(false);
            editButton.setText("EDIT");
        }
    }

    public void setPage(Stage stage){
        this.stage = stage;
    }

    private void swapScenes() throws IOException {
        FXMLLoader fxmlLoader = null;
        disconnect = new SimpleBooleanProperty(false);
        disconnect.bindBidirectional(this.vm.getDisconnect());
        String fxmlPath = "src/main/resources/ui/fxml/loading-page.fxml";
        fxmlLoader = new FXMLLoader(new File(fxmlPath).toURI().toURL());
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(getClass().getResource("/ui/css/loading-page.css").toExternalForm());
        WaitingPageController wp = fxmlLoader.getController();
        wp.setStage(stage);
        wp.setIsHost(isHost);
        disconnect.addListener((observable, oldValue, newValue)->{
            if(newValue){
                Platform.runLater(()->stage.close());

            }
        });
        stage.setOnCloseRequest((WindowEvent event) -> {
            event.consume(); // Consume the event to prevent automatic window closure
            // Show a confirmation dialog
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to exit?");
            alert.showAndWait()
                    .filter(response -> response == ButtonType.OK) // Handle the user's choice
                    .ifPresent(response ->{
                        Platform.runLater(()->{
                            if(isHost)
                            {
                                System.out.println("Host Disconnected!");
                                this.vm.disconnect();
                                stage.close();
                                return;
                            }
                            this.vm.disconnect();
                            System.out.println("Guest Disconnected!");
                            stage.close();
                        });
                    } );
        });
        wp.setViewModel(vm);
        stage.setScene(scene);
        stage.setFullScreen(true);
//        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
    }

}


