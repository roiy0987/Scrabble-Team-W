package Model;

import test.*;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class HostModel extends Observable implements ScrabbleModelFacade {
    //MyServer dictionaryServer;
    private MyServer guestServer;
    private HostHandler hh;
    private Socket hostClient;
    protected Board board;
    protected List<Player> players;
    protected String name;
    private int round;
    private boolean bagIsEmpty;
    private boolean gameOver;
    private int turnCounter;
    private int numberOfPasses;
    protected boolean myTurn;
    public HostModel(String name, String ip, int port) throws IOException {
        this.name = name;
        bagIsEmpty=false;
        gameOver = false;
        hostClient = new Socket(ip,port);
        hh = new HostHandler(this);
        guestServer = new MyServer(5555,hh);
        new Thread(()->guestServer.start()).start();
        players = new ArrayList<>();
        turnCounter=0;
        numberOfPasses=0;
        round=0;

    }

    public List<Player> getPlayers() {
        return players;
    }

    @Override
    public void startGame() throws IOException, ClassNotFoundException {
        board = Board.getBoard();
        Collections.shuffle(players);
        for (Player player : this.players) {
            if(player.name.equals(this.name))
                continue;
            ObjectOutputStream stream = new ObjectOutputStream(player.socket.getOutputStream());
            stream.writeObject(this.getNewPlayerTiles(7));
            stream.flush();
            stream.close();
        }
        if(players.get(0).name.equals(this.name)) { // if it's host turn
            myTurn = true;
            return;
        }
        this.sendMessage("MyTurn",players.get(0)); // if it's a guest turn
    }

    private void sendMessage(String message,Player playerReceiver) throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(playerReceiver.socket.getOutputStream()));
        bw.write(message);
        bw.flush();
        bw.close();
    }


    @Override
    public boolean submitWord(String word, int row, int col, boolean isVertical) throws IOException, ClassNotFoundException {
        Word submittedWord = stringToWord(word,row,col,isVertical);
        if(!board.boardLegal(submittedWord))
            return false;
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(hostClient.getOutputStream()));
        BufferedReader br = new BufferedReader(new InputStreamReader(hostClient.getInputStream()));
        bw.write(word);
        bw.flush();
        String res = br.readLine();
        if(res.equals("false")) {
            bw.close();
            br.close();
            return false;
        }
        int score = board.tryPlaceWord(submittedWord);
        if(score==0){
            bw.close();
            br.close();
            return false;
        }
        // success - word has been placed on board

        Player p = this.players.get(this.turnCounter);
        p.score+=score;

        // remove tiles from player somehow
        this.notifyAllPlayers();
        // need maybe to add timer aswell, for sure
        // maybe add end game function
        br.close();
        bw.close();
        // ViewModel should demand new tiles and remove previous ones
        // ViewModel should demand next turn, getBoard, getScore
        numberOfPasses=-1;
        return true;
    }

    private void notifyAllPlayers() throws IOException {

        for(Player player: this.players){
            // SubmitWord will return true then viewModel should call getBoard, getScore and nextTurn
            if(this.players.get(this.turnCounter).name.equals(player.name))
                continue;
            if(player.name.equals(this.name)) {
                this.setChanged();
                this.notifyObservers();
                continue;
            }
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(player.socket.getOutputStream()));
            bw.write("Update");
            bw.flush();
            bw.close();
        }
    }

    @Override
    public String getScore() throws IOException {
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<this.players.size();i++){
            if(i==this.players.size()-1) {
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
                updatedBoard[i][j] = boardTiles[i][j].letter;
            }
        }
        return updatedBoard;
    }
    private Word stringToWord(String word, int row, int col, boolean isVertical) {
        Tile[] t = new Tile[word.length()];
        for (int i = 0; i < word.length(); i++) {
            t[i] = Tile.Bag.getBag().getLetters()[word.charAt(i) - 'A'];
        }
        return new Word(t, row, col, isVertical);
    }

    @Override
    public ArrayList<Character> getNewPlayerTiles(int amount) throws IOException, ClassNotFoundException {
        if(bagIsEmpty)
            return null;
        ArrayList<Character> list = new ArrayList<>();
        for (int j = 0; j < amount && !bagIsEmpty; j++) {
            Tile t = Tile.Bag.getBag().getRand();
            if(t==null) { // If no more tiles in the bag
                bagIsEmpty=true;
                break;
            }
            list.add(t.letter);
        }
        return list;
    }

    @Override
    public void nextTurn() throws IOException {
        //TODO
        numberOfPasses++;
        this.turnCounter++;
        if(this.turnCounter==this.players.size())
        {
            this.turnCounter=0;
            this.round++;
        }
        if(round==10||numberOfPasses==this.players.size()){//finish game
            //TODO
            for(int i=0;i<this.players.size();i++){
                if(this.players.get(i).name.equals(this.name)){
                    gameOver=true;
                    this.setChanged();
                    this.notifyObservers();
                    continue;
                }
                this.sendMessage("GameOver",this.players.get(i));
            }
            //finishGame
            hh.notify();
            return;
        }
        if(this.players.get(this.turnCounter).name.equals(this.name)){
            myTurn = true;
            this.setChanged();
            this.notifyObservers();
            return;
        }
        myTurn = false;
        this.sendMessage("MyTurn",this.players.get(this.turnCounter));
    }


}
