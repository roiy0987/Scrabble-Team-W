package ViewModel;


import Model.ScrabbleModelFacade;
import javafx.beans.property.*;
import javafx.collections.FXCollections;

import java.io.IOException;
import java.util.*;
import java.util.Observer;


//Controller
public class ScrabbleViewModel implements Observer {
    private ObjectProperty<char[][]> boardProperty;// data binding
    private ListProperty<Character> tiles; // data binding
    private ListProperty<String> scores; // data binding
    private ScrabbleModelFacade model;
    public BooleanProperty myTurn; // data binding
    public BooleanProperty gameOver; // data binding

    
    public ScrabbleViewModel(ScrabbleModelFacade m) throws IOException {
        model = m;
        m.addObserver(this);
        scores = new SimpleListProperty<>(FXCollections.observableArrayList());
        tiles=new SimpleListProperty<>(FXCollections.observableArrayList());
        boardProperty = new SimpleObjectProperty<>();
        myTurn= new SimpleBooleanProperty();
        gameOver = new SimpleBooleanProperty();
    }
    public ListProperty<Character> getTiles(){
        return tiles;
    }

    public ObjectProperty<char[][]> getBoard(){
        return boardProperty;
    }
    public void startGame() {
        try {
            tiles.setAll(model.startGame());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private String getDownWord(int row,int col){
        StringBuilder sb = new StringBuilder();
        while(row!=15){
            if(boardProperty.get()[row][col]=='\u0000')
                break;
            sb.append(boardProperty.get()[row][col]);
            row++;
        }
        return sb.toString();
    }
    private String getRightWord(int row,int col){
        StringBuilder sb = new StringBuilder();
        while(col!=15){
            if(boardProperty.get()[row][col]=='\u0000')
                break;
            sb.append(boardProperty.get()[row][col]);
            col++;
        }
        return sb.toString();
    }
    private String getLeftWord(int row,int col){
        StringBuilder sb = new StringBuilder();
        int originalCol=col;
        while(col!=-1){
            if(boardProperty.get()[row][col]=='\u0000'){
                col+=1;
                break;
            }
            col--;
        }
        sb.append(col).append(":");
        while(col!=originalCol){
            sb.append(boardProperty.get()[row][col]);
            col++;
        }
        sb.append(this.getRightWord(row,col));
        return sb.toString();
    }
    private String getUpWord(int row,int col){
        StringBuilder sb = new StringBuilder();
        int originalRow=row;
        while(row!=-1){
            if(boardProperty.get()[row][col]=='\u0000'){
                row+=1;
                break;
            }
            row--;
        }
        sb.append(row).append(":");
        while(row!=originalRow){
            sb.append(boardProperty.get()[row][col]);
            row++;
        }
        sb.append(this.getDownWord(row,col));
        return sb.toString();
    }


    private String getDirectionOfWord(int row,int col){
        if(row!=0&&boardProperty.get()[row-1][col]!='\u0000'){
            return "up";
        }
        if(col!=0&&boardProperty.get()[row][col-1]!='\u0000'){
            return "left";
        }
        if(row!=14&&boardProperty.get()[row+1][col]!='\u0000'){
            return "down";
        }
        if(col!=14&&boardProperty.get()[row][col+1]!='\u0000'){
            return "right";
        }
        return "error";
    }
    private String getWord(){
        char[][] currentBoard;
        try {
            currentBoard = model.getBoard();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        StringBuilder sb = new StringBuilder();
        int firstTileSubmittedRow=-1;
        int firstTileSubmittedCol=-1;
        for(int i=0;i<currentBoard.length;i++){
            for(int j=0;j<currentBoard[i].length;j++){
                if(currentBoard[i][j]!=boardProperty.get()[i][j]){
                    firstTileSubmittedRow=i;
                    firstTileSubmittedCol=j;
                    break;
                }
            }
        }
        if(firstTileSubmittedRow==-1||firstTileSubmittedCol==-1) //check if player just clicked without placing nothing
            return null;
        String directionOfWord = getDirectionOfWord(firstTileSubmittedRow,firstTileSubmittedCol);
        String[] splittedAnswer;
        switch(directionOfWord){
            case "down":
                sb.append(getDownWord(firstTileSubmittedRow,firstTileSubmittedCol)).append(":")
                .append(firstTileSubmittedRow).append(":")
                .append(firstTileSubmittedCol).append(":")
                .append("true");
                break;
            case "right":
                sb.append(getRightWord(firstTileSubmittedRow,firstTileSubmittedCol)).append(":")
                .append(firstTileSubmittedRow).append(":")
                .append(firstTileSubmittedCol).append(":")
                .append("false");
                break;
            case "left":
                splittedAnswer = getLeftWord(firstTileSubmittedRow,firstTileSubmittedCol).split(":"); // Col:Word
                sb.append(splittedAnswer[1]).append(":")
                .append(firstTileSubmittedRow).append(":")
                .append(splittedAnswer[0]).append(":")
                .append("false");
                break;
            case "up":
                splittedAnswer = getUpWord(firstTileSubmittedRow,firstTileSubmittedCol).split(":"); // Row:Word
                sb.append(splittedAnswer[1]).append(":")
                .append(splittedAnswer[0]).append(":")
                .append(firstTileSubmittedCol).append(":")
                .append("true");
                break;
            default:
                return null;
        }
        return sb.toString();
    }

    public void submitWord(){
        String answer = getWord();
        if(answer==null)
            return;
        String [] splittedAnswer = answer.split(":");
        // Example: "CAT:1:2:true"
        try {
            if(model.submitWord(splittedAnswer[0],
                    Integer.parseInt(splittedAnswer[1]),
                        Integer.parseInt(splittedAnswer[2]),
                            Boolean.parseBoolean(splittedAnswer[3]))){
                for(int i=0;i<splittedAnswer[0].length();i++){
                    this.tiles.remove(this.tiles.indexOf(splittedAnswer[0].charAt(i)));
                }
                tiles.addAll(model.getNewPlayerTiles(splittedAnswer[0].length()));
                myTurn.set(false);
                model.nextTurn();
                this.update(null,null);
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

    public ListProperty<String> getScores()  {
        try {
            String score= model.getScore();
            String[] scoreSplit = score.split("\n");
            scores.addAll(Arrays.asList(scoreSplit));
            return scores;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        try {
            boardProperty.set(model.getBoard());
            scores.set(getScores());
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

