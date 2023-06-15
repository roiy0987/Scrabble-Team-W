package Model;


import ViewModel.ScrabbleViewModel;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;


//model
public class GuestModel extends Observable implements ScrabbleModelFacade {

    protected Socket server;
    private String playerName;
    private boolean myTurn;
    private boolean gameOver;
    boolean gameStarted;

    public GuestModel(String name, String ip, int port) throws IOException {
        this.playerName = name;
        gameStarted=false;
        server = new Socket(ip, port);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
        BufferedReader br = new BufferedReader(new InputStreamReader(server.getInputStream()));
        bw.write("Connect:" + name + "\n");
        bw.flush();
        String st = br.readLine();
        System.out.println(name + " Connected!");
        myTurn = false;
        gameOver=false;
        new Thread(this::waitForGameStart).start();
        // Connect to server with name and socket for blabla
    }
    @Override
    public boolean isGameStarted(){
        return gameStarted;
    }
    public void waitForGameStart(){
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(server.getInputStream()));
            String res = br.readLine();// Wait for game to start
            gameStarted=true;
            this.setChanged();
            this.notifyObservers();
            Thread.sleep(2000);
            this.waitForTurn();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void addObserver(ScrabbleViewModel vm) {
        super.addObserver(vm);
    }
    @Override
    public boolean isMyTurn() {
        return myTurn;
    }
    @Override
    public boolean isGameOver(){
        return gameOver;
    }
    public void waitForTurn() throws IOException, InterruptedException {
        BufferedReader br = new BufferedReader(new InputStreamReader(server.getInputStream()));
        while(!myTurn||!gameOver){
            String res = br.readLine(); // GameOver\Update\MyTurn
            switch (res){
                case "Update":
                    this.setChanged();
                    this.notifyObservers();
                    Thread.sleep(2000);
                    break;
                case "GameOver":
                    gameOver=true;
                    this.setChanged();
                    this.notifyObservers();
                    this.server.close();
                    return;
                case "MyTurn":
                    myTurn=true;
                    this.setChanged();
                    this.notifyObservers();
                    return;
                default:
                    break;
            }
        }
    }

    @Override
    public void nextTurn() throws IOException {
        myTurn=false;
        PrintWriter out = new PrintWriter(server.getOutputStream());
        out.println("NextTurn");
        out.flush();
        Thread t = new Thread(()-> {
            try {
                this.waitForTurn();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        t.start();
    }

    @Override
    public boolean submitWord(String word, int row, int col, boolean isVertical) throws IOException {
        // user
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
        BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
        out.write("SubmitWord:" + word + ":" + row + ":" + col + ":" + isVertical +"\n");
        out.flush();
        String response;
        response= in.readLine();
        return response.startsWith("true");
    }

    @Override
    public String getScore() throws IOException {
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
        BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
        out.write("GetScore:\n");
        out.flush();
        String res = in.readLine();
        return res;
        /*
            Michal:104
            Tal:98
            Roie:57
            Michal:104\nTal:98
         */
    }

    @Override
    public Character[][] getBoard() throws IOException, ClassNotFoundException {
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
        BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
        out.write("GetBoard\n");
        out.flush();
        String responseFromHandler = in.readLine();
        String[] lines = responseFromHandler.split(";");
        Character[][] responseToClient = new Character[15][15];
        for (int i = 0; i < responseToClient.length; i++) {
            String[] line = lines[i].split(":");
            for (int j = 0; j < responseToClient[i].length; j++) {
                responseToClient[i][j] = line[j].charAt(0);
            }
        }
        return responseToClient;
    }

    @Override
    public ArrayList<Character> getNewPlayerTiles(int amount) throws IOException, ClassNotFoundException {
        PrintWriter out = new PrintWriter(server.getOutputStream());
        out.println("GetNewTiles:" + amount);
        out.flush();
        BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));

        ArrayList<Character> playerTiles = new ArrayList<>();
        String response = in.readLine();
        for(int i=0;i<response.length();i++){
            playerTiles.add(response.charAt(i));
        }
        return playerTiles;
    }


    @Override
    public ArrayList<Character> startGame()throws IOException, ClassNotFoundException{
        return getNewPlayerTiles(7);
    }

    public boolean getGameOver(){
        return this.gameOver;
    }

}
