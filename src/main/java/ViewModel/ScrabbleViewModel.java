package ViewModel;

import Model.GuestModel;
import Model.HostModel;
import Model.ScrabbleModelFacade;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;


//Controller
public class ScrabbleViewModel implements Observer {
    private char[][] board; // data binding
    private ArrayList<Character> tiles; // data binding
    public StringProperty score; //data binding
    private ScrabbleModelFacade model;
    public BooleanProperty myTurn;
    public BooleanProperty gameOver;
    /*
        TODO
        [ ] Data binding tiles and board.
        [ ] addObserver - check another way to do.
        [ ] Submit word - understand how do we get the word.
     */


    public ScrabbleViewModel(ScrabbleModelFacade m) throws IOException {
        model = m;
        this.score = new SimpleStringProperty();
        if(m instanceof HostModel){
            ((HostModel) model).addObserver(this);
        }
        else{
            ((GuestModel) model).addObserver(this);
        }
        board =  new char[15][15];
    }
    public void startGame() {
        try {
            tiles = model.startGame();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void submitWord(){

//            char[][] currentBoard = model.getBoard();
//            StringBuilder sb = new StringBuilder();
//            for(int i=0;i<currentBoard.length;i++){
//                for(int j=0;j<currentBoard[i].length;j++){
//                    if(currentBoard[i][j]!=board[i][j]){
//
//                    }
//                }
//            }
        try {
            //TODO
            //How we get Word from view with row,col,isVertical
            //How we data bind the board and tiles
            String word="CAT";
            if(model.submitWord(word,1,2,true)){ // Example
                model.getBoard();
                score.set(model.getScore());
                for(int i=0;i<word.length();i++){
                    this.tiles.remove(this.tiles.indexOf(word.charAt(i)));
                }
                model.getNewPlayerTiles(word.length());
                myTurn.set(false);
                model.nextTurn();
            }
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
    public void skipTurn(){
        try {
            model.nextTurn();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        try {
            model.getBoard(); // data binding some how
            score.set(model.getScore());
            if(model.isMyTurn()){
                myTurn.set(true);
            }
            if(model.isGameOver()){
                gameOver.set(true);
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

