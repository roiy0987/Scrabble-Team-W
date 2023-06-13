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
    //need to add the ability to play with more than 1 host
    public HostModel(String name) throws IOException {
        this.name = name;
        bagIsEmpty = false;
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


    @Override
    public boolean isMyTurn() {
        return myTurn;
    }
    @Override
    public boolean isGameOver(){
        return gameOver;
    }

    @Override
    public void addObserver(ScrabbleViewModel vm) {
        super.addObserver(vm);
    }

    // returns hosts tiles for guest sends appropriate message
    @Override
    public ArrayList<Character> startGame() throws IOException, ClassNotFoundException {
        board = Board.getBoard();
        Collections.shuffle(players);
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
        return getNewPlayerTiles(7);
    }

    private void sendMessage(String message, Player playerReceiver) throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(playerReceiver.socket.getOutputStream()));
        message = message+"\n";
        bw.write(message);
        bw.flush();
    }

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
        // ViewModel should demand new tiles and remove previous ones
        // ViewModel should demand next turn, getBoard, getScore
        numberOfPasses = -1;
        return true;
    }

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
            this.sendMessage("Update", player);
        }
    }

    @Override
    public String getScore() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.players.size(); i++) {
            if (i == this.players.size() - 1) {
                sb.append(this.players.get(i).name).append(":").append(this.players.get(i).score);
                break;
            }
            sb.append(this.players.get(i).name).append(":").append(this.players.get(i).score).append("\n");
        }
        return sb.toString(); // Arik:54'\n'Roie:45'\n'Tal:254
    }

    @Override
    public char[][] getBoard() throws IOException, ClassNotFoundException {
        char[][] b = new char[15][15];
        Character[][] updatedBoard = getBoardToCharacters();
        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < b[i].length; j++) {
                b[i][j] = updatedBoard[i][j];
            }
        }
        return b;
    }

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

    @Override
    public void nextTurn() throws IOException, InterruptedException {
        //TODO
        numberOfPasses++;
        this.turnCounter++;
        if (this.turnCounter >= this.players.size()) {
            this.turnCounter = 0;
            this.round++;
        }
        if (round == 10 || numberOfPasses == this.players.size()) {//finish game
            //TODO
            for (int i = 0; i < this.players.size(); i++) {
                if (this.players.get(i).name.equals(this.name)) {
                    gameOver = true;
                    this.setChanged();
                    this.notifyObservers();
                    continue;
                }
                this.sendMessage("GameOver", this.players.get(i));
            }
            //finishGame
            this.closeClient(); // need to test
            hh.close();
            guestServer.close();
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
    public void closeClient() {
        DictionaryCommunication dc = DictionaryCommunication.getInstance();
        dc.close();
    }


}
