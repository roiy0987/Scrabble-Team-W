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

    /**
     * The GuestModel function is the constructor for the GuestModel class.
     * It takes in a String name, String ip and int port as parameters.
     * The function creates a new socket with the given ip and port, then sends &quot;Connect:name&quot; to server to connect.
     * If server responds with &quot;GameIsFull&quot;, it throws an IOException saying that game is full.
     * If server responds with &quot;NameIsTaken&quot;, it throws an IOException saying that name is taken by another player already connected to game.
     *
     * @param name A String -Set the name of the player
     * @param ip A String -Connect to the server
        public void waitforgamestart() {
            try {
                bufferedreader br = new bufferedreader(new inputstreamreader(server
     * @param port An int -Connect to the server
     *
     */
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
     * The disconnect function is used to disconnect the client from the server.
     * It sends a message to the server that it is disconnecting, and then closes
     * its socket connection with the server.
     *
     */
    @Override
    public void disconnect() { //Need to complete
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
            bw.write("Disconnect:"+this.playerName+"\n");
            bw.flush();
            bw.close();
            if(!server.isClosed())
                this.server.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * The isDisconnected function checks to see if the client is disconnected.
     *
     * @return The value of the disconnect boolean variable
     */
    @Override
    public boolean isDisconnected() {
        return disconnect;
    }


    /**
     * The waitForGameStart function is used to wait for the game to start.
     * It waits until it receives a message from the server that says &quot;GameStarted&quot;.
     * Once this happens, it notifies all of its observers and then calls waitForTurn().
     */
    public void waitForGameStart(){ //Need to complete
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(server.getInputStream()));
            String res = br.readLine();// Wait for game to start
            if(res==null||!res.equals("GameStarted")){
                server.close();
                this.disconnectInvoked();
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

    /**
     * The addObserver function adds a new observer to the list of observers.
     *
     * @param vm ScrabbleViewModel -Pass the view model to the super class
     *
     */
    @Override
    public void addObserver(ScrabbleViewModel vm) {
        super.addObserver(vm);
    }


    /**
     * The isMyTurn function returns a boolean value that indicates whether it is the player's turn.
     *
     * @return A boolean value of true if it is the player's turn and false otherwise
     */
    @Override
    public boolean isMyTurn() {
        return myTurn;
    }
    /**
     * The isGameOver function checks to see if the game is over.
     *
     * @return True if the game is over, false otherwise
     */
    @Override
    public boolean isGameOver(){
        return gameOver;
    }
    /**
     * The waitForTurn function is used to wait for the server to notify the client that it's their turn.
     * The function will continue running until either:
     * 1) It receives a message from the server saying that it's their turn, or
     * 2) The game has ended (in which case, we close our connection with the server).
     */
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
            this.disconnectInvoked();
            throw new RuntimeException(e);
        }

    }

    /**
     * The nextTurn function is called when the current player's turn ends.
     * It sends a message to the server that it is no longer their turn, and then waits for a message from the server
     * indicating that it is now their turn again. This function also sets myTurn to false, so that any further attempts
     * by this client to send messages will be disabled until they receive another &quot;YourTurn&quot; message from the server.
     */
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
            this.disconnectInvoked();
            throw new RuntimeException(e);
        }
    }

    /**
     * The submitWord function is used to submit a word to the game board.
     *
     * @param word String -Pass the word that is being submitted to the server
     * @param row int -Specify the row of the first letter in a word
     * @param col int -Determine the column of the first letter in a word
     * @param isVertical boolean -Determine whether the word is vertical or horizontal
     *
     * @return true if word was successfully placed on board, false if not.
     */
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
            this.disconnectInvoked();
            throw new RuntimeException(e);
        }
    }

    /**
     * The getScore function returns a string containing the current score of all players in the game.
     * The format of this string is as follows with a delimiter of ";" to separate between players.
     * <p>
     * Example: Michal:104;Tal:98;Roie:57;Arik:82
     *
     * @return String
     */
    @Override
    public String getScore() {
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
            out.write("GetScore:\n");
            out.flush();
            return in.readLine();
        }catch (IOException e){
            this.disconnectInvoked();
            throw new RuntimeException(e);
        }
    }

    /**
     * The getBoard function is used to get the board from the server.
     *
     * @return A 2d array of characters
     */
    @Override
    public Character[][] getBoard()  {
        try {
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
        }catch (Exception e){
            this.disconnectInvoked();
        }
        this.disconnectInvoked();
        return null;
    }
    /**
     * The disconnectInvoked function is used to notify the observers that a disconnection has been invoked.
     * It sets the disconnect boolean to true, and notifies all of its observers.
     */
    public void disconnectInvoked(){
        this.disconnect=true;
        this.setChanged();
        this.notifyObservers();
    }

    /**
     * The getNewPlayerTiles function is used to get new tiles for the player.
     *
     * @param amount int -Determine how many tiles the player needs to get from the bag
     *
     * @return An arraylist of characters
     */
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


    /**
     * The startGame function is called when the game begins. It returns an ArrayList of 7 characters, which are the tiles that
     * will be given to a player at the start of a game. The function calls getNewPlayerTiles(7) to accomplish this task.
     *
     * @return An arraylist of character objects
     *
     */
    @Override
    public ArrayList<Character> startGame()throws IOException, ClassNotFoundException{
        return getNewPlayerTiles(7);
    }


}
