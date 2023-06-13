package View;

import Model.GuestModel;
import Model.HostModel;
import ViewModel.ScrabbleViewModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCombination;
import javafx.stage.Screen;
import javafx.stage.Stage;
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

    Stage stage;


    public void joinGame() throws IOException {
        System.out.println("Join clicked!");
        System.out.println(Integer.parseInt(port.getText()));
        GuestModel guest = null;
        try{
            guest = new GuestModel(nickname.getText(), ip.getText(), Integer.parseInt(port.getText()));
        }catch (IOException e){
            e.printStackTrace();
            dialog.setContentText("ERROR in connecting to server. Check ip and port! #!");
        }
        if(guest!=null){
            System.out.println("11111");
            vm = new ScrabbleViewModel(guest);
            this.setNextScene();
        }
    }

    public void hostGame() throws IOException {
        System.out.println("Host clicked!");
        HostModel host = null;
        try{
            host = new HostModel(nickname.getText());
        }catch (IOException e){
            e.printStackTrace();
            dialog.setContentText("ERROR in connecting to server. Check ip and port! #2");
        }
        if(host!=null){
            vm = new ScrabbleViewModel(host);
            isHost = true;
            this.setNextScene();
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

    private void setNextScene() throws IOException {
        FXMLLoader fxmlLoader = null;
        String fxmlPath = "src/main/resources/ui/fxml/loading-page.fxml";
        fxmlLoader = new FXMLLoader(new File(fxmlPath).toURI().toURL());
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(getClass().getResource("/ui/css/loading-page.css").toExternalForm());
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        WaitingPageController wp = fxmlLoader.getController();
        wp.setIsHost(isHost);
        wp.setViewModel(vm);
        wp.setStage(stage);
    }


}


