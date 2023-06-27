package ViewModel;

import Model.ScrabbleModelFacade;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import java.io.IOException;
import java.util.*;
import java.util.Observer;


//Controller
public class ScrabbleViewModel implements Observer {
    private final ObjectProperty<Character[][]> boardProperty;// data binding
    private final ListProperty<Character> tiles; // data binding
    private final ListProperty<String> scores; // data binding
    private final ScrabbleModelFacade model;
    public final BooleanProperty myTurn; // data binding
    public final BooleanProperty gameOver; // data binding
    private final BooleanProperty gameStarted;
    private Character[][] prevBoard;
    private final BooleanProperty disconnect;

    /**
     * The ScrabbleViewModel function is the constructor for the ScrabbleViewModel class.
     * It takes in a ScrabbleModelFacade object and sets it to be an instance variable of this class.
     * It also adds itself as an observer to the model, initializes all of its properties, and creates a 2D array that will hold previous board states.
     *
     * @param m Create a reference to the model
     *
     * @return An instance of the scrabbleviewmodel class
     */
    public ScrabbleViewModel(ScrabbleModelFacade m) throws IOException {
        model = m;
        m.addObserver(this);
        scores = new SimpleListProperty<>(FXCollections.observableArrayList());
        tiles=new SimpleListProperty<>(FXCollections.observableArrayList());
        boardProperty = new SimpleObjectProperty<>();
        myTurn= new SimpleBooleanProperty();
        gameOver = new SimpleBooleanProperty();
        gameStarted= new SimpleBooleanProperty(false);
        disconnect = new SimpleBooleanProperty(false);
        prevBoard = new Character[15][15];
        for(int i=0;i<prevBoard.length;i++){
            Arrays.fill(prevBoard[i], '\u0000');
        }
    }

    /**
     * The getTiles function returns the tiles property.
     *
     * @return A listproperty of type character
     */
    public ListProperty<Character> getTiles(){
        return tiles;
    }

    /**
     * The getGameOver function returns the gameOver property.
     *
     * @return Booleanproperty
     */
    public BooleanProperty getGameOver(){
        return this.gameOver;
    }

    /**
     * The getBoard function returns the boardProperty object.
     *
     * @return An ObjectProperty
     */
    public ObjectProperty<Character[][]> getBoard(){
        return boardProperty;
    }

    /**
     * The getPrevBoard function is used to get the previous board from the model.
     *
     * @return Character[][]
     */
    public Character[][] getPrevBoard(){
        try {
            this.prevBoard = this.model.getBoard();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return this.prevBoard;
    }

    /**
     * The startGame function is used to start the game.
     * It sets the boolean value of gameStarted to true, and then calls the model's startGame function.
     */
    public void startGame() {
        try {
            gameStarted.set(true);
            if(tiles.size()==7)
                return;
            tiles.setAll(model.startGame());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * The getDisconnect function returns the disconnect property.
     *
     * @return A booleanproperty
     */
    public BooleanProperty getDisconnect() {
        return disconnect;
    }

    /**
     * The disconnect function disconnects the client from the server.
     *
     * @return A boolean value
     */
    public void disconnect(){
        this.model.disconnect();
    }

    /**
     * The endGame function is called when the game ends.
     * It sets the model's endGame variable to true, which will cause the view to stop updating and display a message saying that the game has ended.
     *
     * @return A boolean
     */
    public void endGame(){
        this.model.endGame();
    }

    /**
     * The getDownWord function takes in a row and column number as parameters,
     * and returns the word that is formed by going down from that position.
     *
     * @param row Specify the row of the first letter of the word
     * @param col Specify the column of the first letter of the word
     *
     * @return A string of the letters in the word that is formed when
     */
    private String getDownWord(int row,int col){
        StringBuilder sb = new StringBuilder();
        while(row!=15 && row > -1){
            if(boardProperty.get()[row][col]=='\u0000')
                break;
            if(boardProperty.get()[row][col]==prevBoard[row][col]&&prevBoard[row][col]!='\u0000')
            {
                sb.append('_');
                row++;
                continue;
            }
            sb.append(boardProperty.get()[row][col]);
            row++;
        }
        return sb.toString();
    }

    /**
     * The getRightWord function takes in a row and column number as parameters,
     * and returns the word that is to the right of that position.
     *
     * @param row Specify the row of the first letter of the word
     * @param col Specify the column of the first letter of the word
     *
     * @return A string of letters that are to the right of the current tile
     */
    private String getRightWord(int row,int col){
        StringBuilder sb = new StringBuilder();
        while(col!=15 && col > -1){
            if(boardProperty.get()[row][col]=='\u0000')
                break;
            if(boardProperty.get()[row][col]==prevBoard[row][col]&&prevBoard[row][col]!='\u0000'){
                sb.append('_');
                col++;
                continue;
            }
            sb.append(boardProperty.get()[row][col]);
            col++;
        }
        return sb.toString();
    }

    /**
     * The getLeftWord function takes in a row and column number, and returns the word to the left of that position.
     *
     * @param row Specify the row of the first letter of the word
     * @param col Specify the column of the first letter of the word
     *
     * @return The word to the left of the tile
     */
    private String getLeftWord(int row,int col){
        StringBuilder sb = new StringBuilder();
        int originalCol=col;
        while(col!=-1 && col<15){
            if(boardProperty.get()[row][col]=='\u0000'){
                col+=1;
                break;
            }
            col--;
        }
        sb.append(col).append(":");
        while(col!=originalCol){
            sb.append('_');
            col++;
        }
        sb.append(this.getRightWord(row,col));
        return sb.toString();
    }

    /**
     * The getUpWord function takes in a row and column number, and returns the word that is above the letter at that position.
     *
     * @param row Specify the row of the first letter of the word
     * @param col Specify the column of the first letter of the word
     *
     * @return The word above the current cell
     */
    private String getUpWord(int row,int col){
        StringBuilder sb = new StringBuilder();
        int originalRow=row;
        while(row!=-1 && row<15){
            if(boardProperty.get()[row][col]=='\u0000'){
                row+=1;
                break;
            }
            row--;
        }
        sb.append(row).append(":");
        while(row!=originalRow){
            sb.append('_');
            row++;
        }
        sb.append(this.getDownWord(row,col));
        return sb.toString();
    }

    /**
     * The getDirectionOfWord function takes in a row and column number, and returns the direction of the word that is being placed.
     *
     * @param row Specify the row of the first letter of the word
     * @param col Specify the column of the first letter of the word
     *
     * @return The direction of the word
     */
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

    /**
     * The getWord function is used to get the word that was just submitted by the player.
     * It does this by comparing the current board with a copy of it, and then finding out which tiles were changed.
     * Then, it finds out in which direction (up/down/left/right) did the player place his tiles.
     * After that, it gets all of those letters and returns them as a string along with their coordinates on the board (row:col).
     *
     * @return A string in the form:
     */
    private String getWord(){
        Character[][] currentBoard;
        try {
            currentBoard = model.getBoard();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        StringBuilder sb = new StringBuilder();
        int firstTileSubmittedRow=-1;
        int firstTileSubmittedCol=-1;
        boolean br=false;
        for(int i=0;!br&&i<currentBoard.length;i++){
            for(int j=0;j<currentBoard[i].length;j++){
                if(currentBoard[i][j]!=boardProperty.get()[i][j]&&currentBoard[i][j]!='\u0000'&&boardProperty.get()[i][j]!='\u0000')
                    return null;
                if(currentBoard[i][j]!=boardProperty.get()[i][j]){
                    firstTileSubmittedRow=i;
                    firstTileSubmittedCol=j;
                    br=true;
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

    /**
     * The submitWord function is used to submit a word that the player has created.
     * It takes in no parameters and returns a boolean value of true if the word was successfully submitted,
     * or false if it wasn't. The function first gets the answer from getWord(), which is then split into an array of strings,
     * with each string being one part of the answer (the word itself, its starting row position on boardProperty, its starting column position on boardProperty, and whether it's horizontal or vertical). Then we try to submit this information using model.submitWord(). If this succeeds (i.e., if model returns true
     *
     * @return True if the word is valid and false otherwise
     *
     */
    public boolean submitWord(){
        String answer = getWord();
        if(answer==null)
            return false;
        String [] splittedAnswer = answer.split(":");
        // Example: "CAT:1:2:true"
        try {
            if(model.submitWord(splittedAnswer[0],
                    Integer.parseInt(splittedAnswer[1]),
                    Integer.parseInt(splittedAnswer[2]),
                    Boolean.parseBoolean(splittedAnswer[3]))){
                System.out.println(tiles.toString());
                int counter = 0;
                for(int i=0;i<splittedAnswer[0].length();i++){
                    if(splittedAnswer[0].charAt(i)=='_')
                        continue;
                    tiles.remove(tiles.get(tiles.get().indexOf(splittedAnswer[0].charAt(i))));
                    counter++;
                }
                ArrayList<Character> newPlayerTiles = model.getNewPlayerTiles(counter);
                System.out.println(tiles.toString());
                System.out.println(newPlayerTiles.toString());
                tiles.addAll(newPlayerTiles);
                System.out.println(tiles.toString());

                Platform.runLater(()->{
                    try {
                        boardProperty.set(model.getBoard());
                        this.getScores();
                        myTurn.set(false);
                        model.nextTurn();
                    } catch (IOException | ClassNotFoundException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
                Thread.sleep(1000);
                return true;
            }
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
    public void skipTurn() throws IOException, InterruptedException {
        model.nextTurn();

    }

    /**
     * The getScores function is a getter function that returns the scores property.
     *
     * @return A listproperty&lt;string&gt;
     */
    public ListProperty<String> getScores()  {
        try {
            System.out.println("getScore invoked");
            //Thread.sleep(1500);
            String score= model.getScore();
            System.out.println(score);
            String[] scoreSplit = score.split(";");
            scores.clear();
            scores.addAll(Arrays.asList(scoreSplit));
            return scores;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * The getGameStartedProperty function returns the gameStarted property.
     *
     * @return A booleanproperty
     */
    public BooleanProperty getGameStartedProperty(){
        return this.gameStarted;
    }

    @Override
    public void update(Observable o, Object arg) {
        // Before startGame WaitForPlayers -> player connected -> update ScoreList in host
        // Guest -> startGame
        if(model.isDisconnected()){
            this.disconnect.set(true);
            return;
        }
        if(!gameStarted.get()&&!model.isGameStarted()&&!model.isGameOver())
        {
            //Host
            Platform.runLater(()->{
                this.getScores();
            });
            return;
        }
        if(!gameStarted.get()&&model.isGameStarted()){
            //Guest
            Platform.runLater(()->{
                try {
                    boardProperty.set(model.getBoard());
                    prevBoard = boardProperty.get();
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                this.getScores();
            });
            this.startGame();
            return;
        }

        if(model.isGameOver()){
            System.out.println("GAME OVER");
            gameOver.set(true);
            return;
        }

        Platform.runLater(()->{
            try {
                prevBoard = boardProperty.get();
                boardProperty.set(model.getBoard());
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            this.getScores();
        });
        if(model.isMyTurn()){
            myTurn.set(true);
        }
    }

    /**
     * The setPrevBoard function sets the boardProperty to the previous board.
     * This is used in conjunction with the undo function, which calls this function.
     *
     * @return Void
     */
    public void setPrevBoard() {
        Platform.runLater(()->{
            this.boardProperty.set(prevBoard);
        });
    }
}

