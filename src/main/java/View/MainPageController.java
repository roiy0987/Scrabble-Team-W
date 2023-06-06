package View;

import Model.GuestModel;
import Model.HostModel;
import ViewModel.ScrabbleViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
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



    public void joinGame() throws IOException {
        System.out.println("Join clicked!");
        System.out.println(Integer.parseInt(port.getText()));
        try{
            GuestModel guest = new GuestModel(nickname.toString(), ip.getText(), Integer.parseInt(port.getText()));
        }catch (IOException e){
            e.printStackTrace();
            dialog.setContentText("ERROR in connecting to server. Check ip and port! #!");
        }

//        vm = new ScrabbleViewModel(nickname,ip, port, false);
        // Perform your desired actions here
    }

    public void hostGame() throws IOException {
        System.out.println("Host clicked!");
        try{
            HostModel host = new HostModel(nickname.toString(),"localhost",Integer.parseInt(port.getText()));
        }catch (IOException e){
            e.printStackTrace();
            dialog.setContentText("ERROR in connecting to server. Check ip and port! #2");
        }
//        vm = new ScrabbleViewModel(nickname,"localhost", 8889, true);
    }


}


