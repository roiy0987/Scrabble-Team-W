package View;

import ViewModel.ScrabbleViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.io.IOException;

public class View {

    ScrabbleViewModel vm;

    @FXML
    TextField nickname;

    @FXML
    Button editButton;

    private TextField textField;
    public void joinGame() throws IOException {
        System.out.println("Join clicked!");
//        vm = new ScrabbleViewModel(nickname,ip, port, false);
        // Perform your desired actions here
    }

    public void hostGame() throws IOException {
        System.out.println("Host clicked!");
//        vm = new ScrabbleViewModel(nickname,"localhost", 8889, true);
    }

    public void editNickname() {
        System.out.println("Edit nickname clicked! "+ nickname.getText());
        if(editButton.getText().equals("EDIT")){
            nickname.setEditable(true);
            editButton.setText("SAVE");
        }else {
            nickname.setEditable(false);
            editButton.setText("EDIT");
        }

    }
//        vm = new ScrabbleViewModel(nickname,ip, port, false);
        // Perform your desired actions here
}


