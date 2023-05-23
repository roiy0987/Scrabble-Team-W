package ViewModel;

import Model.GuestModel;
import Model.HostModel;
import Model.ScrabbleModelFacade;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;


//Controller
public class ScrabbleViewModel implements Observer {
    private char[][] board;
    private ScrabbleModelFacade model;

    public ScrabbleViewModel(String name, String ip, int port, boolean isHost) throws IOException {
        if(isHost)
            model = new HostModel(name, ip, port);
        else
            model = new GuestModel(name, ip, port);
        board =  new char[15][15];
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}

