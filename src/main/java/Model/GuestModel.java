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
    private boolean disconnect;

    public GuestModel(String name, String ip, int port)throws IOException {
        this.playerName = name;
        gameStarted=false;
        disconnect=false;
        try {
            server = new Socket(ip, port);
        }catch (IOException e){
            throw new IOException("ERROR in connecting to server. Check ip and port!");
        }
        BufferedWriter bw = null;
        bw = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
        BufferedReader br = new BufferedReader(new InputStreamReader(server.getInputStream()));
        bw.write("Connect:" + name + "\n");
        bw.flush();
        String st = br.readLine();
        if(st.equals("GameIsFull")){
            server.close();
            throw new IOException("Game is Full!");
        }
        if(st.equals("NameIsTaken")){
            server.close();
            throw new IOException("Name is already chosen! Please enter a different name");
        }
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

    @Override
    public void disconnect() { //Need to complete
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
            bw.write("Disconnect:"+this.playerName+"\n");
            bw.flush();
            //TODO
            // Need to check if need to close streams before closing the socket
//            disconnect=true;
//            this.setChanged();
//            this.notifyObservers();
            // bw.close();
            this.server.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isDisconnected() {
        return disconnect;
    }


    public void waitForGameStart(){ //Need to complete
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(server.getInputStream()));
            String res = br.readLine();// Wait for game to start
            if(res==null||!res.equals("GameStarted")){
                server.close();
                //TODO
                // Need to close the screen of guest
                this.disconnect=true;
                this.setChanged();
                this.notifyObservers();
                return;
            }
            gameStarted=true;
            this.setChanged();
            this.notifyObservers();
            Thread.sleep(2000);
            this.waitForTurn();
        } catch (IOException | InterruptedException e) {
            System.out.println("Guest Disconnected Successfully!");
            throw new RuntimeException("Socket Closed!");
        }

    }

    @Override
    public void addObserver(ScrabbleViewModel vm) {
        super.addObserver(vm);
    }

    @Override
    public void endGame() { // Need to complete

    }

    @Override
    public boolean isMyTurn() {
        return myTurn;
    }
    @Override
    public boolean isGameOver(){
        return gameOver;
    }
    public void waitForTurn()  {
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(server.getInputStream()));
            while(!myTurn||!gameOver){
                String res = br.readLine(); // GameOver\Update\MyTurn
                if(res==null){
                    continue;
                }
                switch (res){
                    case "Disconnect":
                        this.server.close();
                        this.disconnect=true;
                        this.setChanged();
                        this.notifyObservers();
                        return;
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
        }catch(IOException | InterruptedException e) {
            this.disconnect=true;
            this.setChanged();
            this.notifyObservers();
            throw new RuntimeException(e);
        }

    }

    @Override
    public void nextTurn() {
        myTurn=false;
        try {
            PrintWriter out = new PrintWriter(server.getOutputStream());
            out.println("NextTurn");
            out.flush();
            Thread t = new Thread(this::waitForTurn);
            t.start();
        }catch (IOException e){
            this.disconnect=true;
            this.setChanged();
            this.notifyObservers();
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean submitWord(String word, int row, int col, boolean isVertical)  {
        // user
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
            out.write("SubmitWord:" + word + ":" + row + ":" + col + ":" + isVertical + "\n");
            out.flush();
            String response;
            response = in.readLine();
            return response.startsWith("true");
        }catch (IOException e){
            this.disconnect=true;
            this.setChanged();
            this.notifyObservers();
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getScore() {
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
            out.write("GetScore:\n");
            out.flush();
            return in.readLine();
        }catch (IOException e){
            this.disconnect=true;
            this.setChanged();
            this.notifyObservers();
            throw new RuntimeException(e);
        }

        /*
            Michal:104
            Tal:98
            Roie:57
            Michal:104\nTal:98
         */
    }

    @Override
    public Character[][] getBoard()  {
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
            out.write("GetBoard\n");
            out.flush();
            String responseFromHandler = in.readLine();
            System.out.println(responseFromHandler);
            String[] lines = responseFromHandler.split(";");
            Character[][] responseToClient = new Character[15][15];
            for (int i = 0; i < responseToClient.length; i++) {
                String[] line = lines[i].split(":");
                for (int j = 0; j < responseToClient[i].length; j++) {
                    responseToClient[i][j] = line[j].charAt(0);
                }
            }
            return responseToClient;
        }catch (IOException|ArrayIndexOutOfBoundsException e){
            this.disconnect=true;
            this.setChanged();
            this.notifyObservers();
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<Character> getNewPlayerTiles(int amount) {
        try {
            PrintWriter out = new PrintWriter(server.getOutputStream());
            out.println("GetNewTiles:" + amount);
            out.flush();
            BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));

            ArrayList<Character> playerTiles = new ArrayList<>();
            String response = in.readLine();
            for (int i = 0; i < response.length(); i++) {
                playerTiles.add(response.charAt(i));
            }
            return playerTiles;
        }catch (IOException e){
            this.disconnect=true;
            this.setChanged();
            this.notifyObservers();
            throw new RuntimeException(e);
        }
    }


    @Override
    public ArrayList<Character> startGame()throws IOException, ClassNotFoundException{
        return getNewPlayerTiles(7);
    }

    public boolean getGameOver(){
        return this.gameOver;
    }

}
