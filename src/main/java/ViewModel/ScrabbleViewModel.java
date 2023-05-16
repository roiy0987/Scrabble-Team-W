package ViewModel;

import Model.MyScrabbleModel;
import Model.MyScrabbleModelHost;
import Model.ScrabbleModelFacade;

import java.io.IOException;


//Controller
public class ScrabbleViewModel {
    private char[][] board;
    private ScrabbleModelFacade model;

    public ScrabbleViewModel(String name, String ip, int port, boolean isHost) throws IOException {
        if(isHost)
            model = new MyScrabbleModelHost(name, ip, port);
        else
            model = new MyScrabbleModel(name, ip, port);
        board =  new char[15][15];
    }
}

