package Model;

import ViewModel.ScrabbleViewModel;
import test.*;

import java.io.*;
import java.util.*;

public class HostModel extends Observable implements ScrabbleModelFacade {
    private MyServer guestServer;
    private HostHandler hh;

    protected Board board;
    protected List<Player> players;
    protected String name;
    private int round;
    private boolean bagIsEmpty;
    private boolean gameOver;
    private int turnCounter;
    private int numberOfPasses;
    protected boolean myTurn;
    private boolean disconnect;
    public boolean gameStarted;

    /**
     * The HostModel function is the constructor for the HostModel class.
     * It initializes all the variables that are used in this class, and starts a server to listen for incoming connections from guests.
     *
     * @param name String -Set the name of the host
     *
     */
    public HostModel(String name) throws IOException {
        this.name = name;
        disconnect=false;
        bagIsEmpty = false;
        gameStarted=false;
        gameOver = false;
        hh = new HostHandler(this);
        guestServer = new MyServer(5556, hh);
        guestServer.start();
        players = new ArrayList<>();
        players.add(new Player(name, null, 0));
        turnCounter = 0;
        numberOfPasses = 0;
        round = 0;
    }
    /**
     * The isGameStarted function returns a boolean value that indicates whether the game has started or not.
     *
     * @return A boolean value
     */
    @Override
    public boolean isGameStarted(){
        return gameStarted;
    }

    /**
     * The isMyTurn function returns a boolean value that indicates whether it is the player's turn.
     *
     * @return A boolean value
     */
    @Override
    public boolean isMyTurn() {
        return myTurn;
    }
    /**
     * The isGameOver function checks to see if the game is over.
     *
     * @return True if the game is over, and false otherwise
     */
    @Override
    public boolean isGameOver(){
        return gameOver;
    }

    /**
     * The addObserver function adds a new observer to the list of observers.
     *
     * @param vm An ScrabbleViewModel -Pass the view model to the super class
     *
     */
    @Override
    public void addObserver(ScrabbleViewModel vm) {
        super.addObserver(vm);
    }

    /**
     * The startGame function is responsible for starting the game.
     * It shuffles the players, and then sends a message to all of them that the game has started.
     * Then it waits 2 seconds before sending a message to either host or guest depending on who's turn it is first.
     *
     * @return A list of characters
     *
     */
    @Override
    public ArrayList<Character> startGame() throws IOException, ClassNotFoundException {
        board = Board.getBoard();
        Collections.shuffle(players);
        gameStarted=true;
        for(int i=0;i<this.players.size();i++){
            if(players.get(i).name.equals(this.name))
                continue;
            this.sendMessage("GameStarted", players.get(i));
        }
        //Thread sleep
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (players.get(0).name.equals(this.name)) { // if it's host turn
            myTurn = true;
            this.setChanged();
            this.notifyObservers();
            return getNewPlayerTiles(7);
        }
        this.sendMessage("MyTurn", players.get(0)); // if it's a guest turn
        this.setChanged();
        this.notifyObservers();
        return getNewPlayerTiles(7);
    }

    /**
     * The sendMessage function sends a message to the player specified in the parameter.
     *
     *
     * @param message String -Send a message to the player
     * @param playerReceiver Player -Send the message to a specific player
     *
     */
    private void sendMessage(String message, Player playerReceiver) throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(playerReceiver.socket.getOutputStream()));
        message = message+"\n";
        bw.write(message);
        bw.flush();
    }

    /**
     * The submitWord function takes in a word, row, column and boolean value to determine if the word is vertical or horizontal.
     * It then converts the string into a Word object using the stringToWord function. The tryPlaceWord function from Board is called
     * with this new Word object as an argument and returns an integer score which represents how many points were earned by placing that
     * word on the board. If no points are earned (score == 0), then false is returned because it was not possible to place that word on
     * the board at those coordinates. Otherwise, true is returned because it was possible to place that word
     *
     * @param word String -Create a word object to be placed on the board
     * @param row int -Determine the row of the first letter in a word
     * @param col int -Determine the column of the first letter in a word
     * @param isVertical boolean -Determine whether the word is placed vertically or horizontally on the board
     *
     * @return A boolean, which is true if the word was successfully placed on the board
     *
     */
    @Override
    public boolean submitWord(String word, int row, int col, boolean isVertical) throws IOException, ClassNotFoundException {
        Word submittedWord = stringToWord(word, row, col, isVertical);
        int score = board.tryPlaceWord(submittedWord);
        if (score == 0) {
            return false;
        }
        // success - word has been placed on board
        Player p = this.players.get(this.turnCounter);
        p.score += score;
        // remove tiles from player somehow
        this.notifyAllPlayers();
        this.setChanged();
        this.notifyObservers();
        // ViewModel should demand new tiles and remove previous ones
        // ViewModel should demand next turn, getBoard, getScore
        numberOfPasses = -1;
        return true;
    }

    /**
     * The update function is used to notify the observers that a change has been made.
     * This function will be called by the model whenever it needs to update its observers.
     *
     */
    public void update(){
        this.setChanged();
        this.notifyObservers();
    }

    /**
     * The notifyAllPlayers function is used to notify all players of the current state of the game.
     * This function will be called after a player has submitted a word, and it will send an update message to all other players.
     * The update message contains information about the board, score and whose turn it is next.
     *
     * @return A string
     *
     */
    private void notifyAllPlayers() throws IOException {

        for (Player player : this.players) {
            // SubmitWord will return true then viewModel should call getBoard, getScore and nextTurn
            if (this.players.get(this.turnCounter).name.equals(player.name))
                continue;
            if (player.name.equals(this.name)) {
                this.setChanged();
                this.notifyObservers();
                continue;
            }
            if(this.players.get(this.turnCounter)==player)
                continue;
            this.sendMessage("Update", player);
        }
    }

    /**
     * The isDisconnected function checks to see if the client is disconnected.
     *
     * @return A boolean value that indicates whether the client is disconnected
     *
     */
    @Override
    public boolean isDisconnected() {
        return disconnect;
    }

    /**
     * The getScore function returns a string of the players' names and scores.
     *
     * @return A string with the scores of all players in the game
     *
     */
    @Override
    public String getScore() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.players.size(); i++) {
            if (i == this.players.size() - 1) {
                sb.append(this.players.get(i).name).append(":").append(this.players.get(i).score);
                break;
            }
            sb.append(this.players.get(i).name).append(":").append(this.players.get(i).score).append(";");
        }
        return sb.toString(); // Arik:54'\n'Roie:45'\n'Tal:254
    }

    /**
     * The getBoard function is used to return the board as a 2D array of characters.
     *
     * @return A character[][]
     *
     */
    @Override
    public Character[][] getBoard() throws IOException, ClassNotFoundException {
        return getBoardToCharacters();
    }

    /**
     * The getBoardToCharacters function is a helper function that converts the board's tiles into characters.
     *
     *
     * @return A 2d array of characters
     */
    public Character[][] getBoardToCharacters() {
        Character[][] updatedBoard = new Character[15][15];
        Tile[][] boardTiles = board.getTiles();
        for (int i = 0; i < boardTiles.length; i++) {
            for (int j = 0; j < boardTiles[i].length; j++) {
                if (boardTiles[i][j] == null) {
                    updatedBoard[i][j] = '\u0000';
                    continue;
                }
                updatedBoard[i][j] = boardTiles[i][j].letter;
            }
        }
        return updatedBoard;
    }

    /**
     * The stringToWord function takes a string and converts it into a Word object.
     *
     *
     * @param word String -Store the word that is being passed in
     * @param row int -Set the row of the word
     * @param col int -Determine the column of the word
     * @param isVertical boolean -Determine if the word is vertical or horizontal
     *
     * @return A word object
     *
     */
    private Word stringToWord(String word, int row, int col, boolean isVertical) {
        System.out.println(word);
        System.out.println(word.length());
        Tile[] t = new Tile[word.length()];
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == '_') {
                t[i] = null;
                continue;
            }
            t[i] = Tile.Bag.getBag().getLetters()[word.charAt(i) - 'A'];
        }
        return new Word(t, row, col, isVertical);
    }

    /**
     * The getNewPlayerTiles function is used to get a new set of tiles for the player.
     *
     *
     * @param amount int -Determine how many tiles the player will receive
     *
     * @return An arraylist of characters
     *
     */
    @Override
    public ArrayList<Character> getNewPlayerTiles(int amount) throws IOException, ClassNotFoundException {
        if (bagIsEmpty)
            return null;
        ArrayList<Character> list = new ArrayList<>();
        for (int j = 0; j < amount && !bagIsEmpty; j++) {
            Tile t = Tile.Bag.getBag().getRand();
            if (t == null) { // If no more tiles in the bag
                bagIsEmpty = true;
                break;
            }
            list.add(t.letter);
        }
        return list;
    }

    /**
     * The nextTurn function is used to determine which player's turn it is.
     * It also determines when the game ends, and if so, call the endGame function.
     *
     */
    @Override
    public void nextTurn() throws IOException, InterruptedException {
        numberOfPasses++;
        this.turnCounter++;
        if (this.turnCounter >= this.players.size()) {
            this.turnCounter = 0;
            this.round++;
        }
        if (round == 10 || numberOfPasses == this.players.size()) {//finish game
            this.endGame();
            return;
        }
        if (this.players.get(this.turnCounter).name.equals(this.name)) {
            myTurn = true;
            this.setChanged();
            this.notifyObservers();
            return;
        }
        myTurn = false;
        this.sendMessage("MyTurn", this.players.get(this.turnCounter));
    }
    /**
     * The disconnect function is used to notify the other players that this player has disconnected.
     * It also closes the client socket and stops listening for messages from the server.
     */
    @Override
    public void disconnect(){
        disconnect=true;
        this.setChanged();
        this.notifyObservers();
        for(int i=0;i<players.size();i++){
            if(this.players.get(i).name.equals(this.name))
                continue;
            try {
                this.sendMessage("Disconnect",this.players.get(i));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        //finishGame
        this.closeClient();
    }

    /**
     * The endGame function is called when the game has ended.
     * It sends a message to all players that the game is over, and then closes the client.
     */
    private void endGame(){
        for (int i = 0; i < this.players.size(); i++) {
            if (this.players.get(i).name.equals(this.name)) {
                gameOver = true;
                this.setChanged();
                this.notifyObservers();
                continue;
            }
            try {
                this.sendMessage("GameOver", this.players.get(i));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        //finishGame
        this.closeClient();
    }
    /**
     * The closeClient function closes the client's connection to the server.
     * It does this by closing the DictionaryCommunication instance, which in turn
     * closes its socket and input/output streams.
     */
    public void closeClient() {
        DictionaryCommunication dc = DictionaryCommunication.getInstance();
        dc.close();
        hh.close();
        try {
            guestServer.close();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}
