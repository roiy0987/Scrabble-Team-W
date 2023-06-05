package ViewModel;

import Model.GuestModel;
import Model.HostModel;
import Model.ScrabbleModelFacade;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;


//Controller
public class ScrabbleViewModel implements Observer {
    private char[][] board;
    private ArrayList<Character> tiles;
    private ScrabbleModelFacade model;

    public ScrabbleViewModel(String name, String ip, int port, boolean isHost) throws IOException {
        if(isHost)
            model = new HostModel(name, ip, port);
        else
            model = new GuestModel(name, ip, port);
        board =  new char[15][15];
    }
    public void startGame() throws IOException, ClassNotFoundException {
        model.startGame();
        tiles = model.getNewPlayerTiles(7);
    }

    @Override
    public void update(Observable o, Object arg) {
        GuestModel gs = (GuestModel) o;
        try {
            model.getBoard();
            model.getScore();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if(gs.isMyTurn()){
            return;
        }
        if(gs.isMyTurn()){
            return;
        }
    }

    public ArrayList<Character> getTiles(){
        return this.tiles;
    }
}

