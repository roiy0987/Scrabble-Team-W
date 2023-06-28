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

    /**
     * The getMainPage function is a static function that returns the singleton instance of the MainPageController class.
     * This function is used to ensure that only one instance of this class exists at any given time, and it also allows
     * for easy access to this object from other classes.  The getMainPage function takes no parameters and returns an object
     * of type MainPageController.  If there are no instances of the MainPageController class currently in existence, then a new one will be created before being returned by this method.
     *
     * @return A mainpagecontroller object
     */
    public static MainPageController getMainPage() throws IOException {
        if(singleton_instace == null)
            singleton_instace = new MainPageController();
        return singleton_instace;
    }

    /**
     * The getScene function is used to load the main-page.fxml file and return a Scene object
     * that can be used in the Main class to set up the stage for this scene.
     *
     * @return A scene object
     */
    public Scene getScene() throws IOException {
        FXMLLoader fxmlLoader = null;
        String fxmlPath = "src/main/resources/ui/fxml/main-page.fxml";
        fxmlLoader = new FXMLLoader(new File(fxmlPath).toURI().toURL());
        Scene scene = new Scene(fxmlLoader.load(), Screen.getPrimary().getVisualBounds().getWidth(), Screen.getPrimary().getVisualBounds().getHeight());
        scene.getStylesheets().add(getClass().getResource("/ui/css/main-page.css").toExternalForm());
        return scene;
    }

    /**
     * The joinGame function is called when the user clicks on the &quot;Join Game&quot; button.
     * It creates a new GuestModel object, which will attempt to connect to a server at
     * the IP address and port number specified by the user in their respective text fields.
     *
     */
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

    /**
     * The hostGame function is called when the host button is clicked.
     * It creates a new HostModel object, which will be used to create a ScrabbleViewModel object.
     * The ScrabbleViewModel object will then be used to swap scenes and start the game.
     *
     */
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

    /**
     * The editParameters function allows the user to edit their nickname, IP address and port number.
     * The function is called when the &quot;EDIT&quot; button is clicked. When this happens, all three text fields are set to be editable by the user.
     * If they click on &quot;SAVE&quot;, then all three text fields are no longer editable by the user and their changes will be saved in those respective variables for future use.
     *
     */
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

    /**
     * The setPage function is used to set the stage of the current page.
     *
     * @param stage Set the stage for the scene
     */
    public void setPage(Stage stage){
        this.stage = stage;
    }

    /**
     * The swapScenes function is used to swap the current scene with a new one.
     */
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


