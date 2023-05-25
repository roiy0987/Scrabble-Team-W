package Model;


import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Scanner;

import static java.lang.Integer.parseInt;

//model
public class GuestModel extends Observable implements ScrabbleModelFacade {

    protected Socket server;
    private String playerName;
    private ArrayList<Character> tiles;
    private boolean myTurn;
    private boolean gameOver;

    public GuestModel(String name, String ip, int port) throws IOException {
        this.playerName = name;
        server = new Socket(ip, port);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
        BufferedReader br = new BufferedReader(new InputStreamReader(server.getInputStream()));

        for(int i=0; i<5; i++){
            bw.write("Connect:" + name);
            bw.flush();
            bw.close();
            String st = br.readLine();
            if (st==null&&i==4)
                throw new IOException("Failed to connect");
            if(st==null)
                continue;
            if(st.equals("Ok"))
                break;
        }
        br.close();

        tiles = new ArrayList<>();
        myTurn = false;
        gameOver=false;
        this.nextTurn();
        // Connect to server with name and socket for blabla
    }

    public boolean isMyTurn() {
        return myTurn;
    }


    public void waitForTurn() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(server.getInputStream()));
        while(true){
            String res = br.readLine(); // GameOver\Update\MyTurn
            if(res==null)
                continue;
            switch (res){
                case "Update":
                    this.setChanged();
                    this.notifyObservers();
                    break;
                case "GameOver":
                    gameOver=true;
                    this.setChanged();
                    this.notifyObservers();
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
    public boolean submitWord(String word, int row, int col, boolean isVertical) throws IOException {
        // user
        PrintWriter out = new PrintWriter(server.getOutputStream());
        Scanner in = new Scanner(server.getInputStream());
        out.println("SubmitWord:" + word + ":" + row + ":" + col + ":" + isVertical);
        out.flush();
        String response = in.next(); //
        in.close();
        out.close();
        return response.startsWith("true");
    }

    @Override
    public String getScore() throws IOException {
        PrintWriter out = new PrintWriter(server.getOutputStream());
        Scanner in = new Scanner(server.getInputStream());
        out.println("GetScore:");
        out.flush();
        StringBuilder sb = new StringBuilder();
        while (in.hasNext()) {
            sb.append(in.nextLine());
            if (in.hasNext())
                sb.append("\n");
        }
        String res = sb.toString();
        in.close();
        out.close();
        return res;
        /*
            Michal:104
            Tal:98
            Roie:57
            Michal:104\nTal:98
         */
    }

    @Override
    public char[][] getBoard() throws IOException, ClassNotFoundException {
        PrintWriter out = new PrintWriter(server.getOutputStream());
        BufferedInputStream bufferedInputStream = new BufferedInputStream(server.getInputStream());
        ObjectInputStream objectInputStream = new ObjectInputStream(bufferedInputStream);
        out.println("GetBoard:");
        out.flush();
        Character[][] board = null;
        board = (Character[][]) objectInputStream.readObject();
        char[][] responseToClient = new char[15][15];
        for (int i = 0; i < responseToClient.length; i++) {
            for (int j = 0; j < responseToClient[i].length; j++) {
                responseToClient[i][j] = board[i][j];
            }
        }
        objectInputStream.close();
        bufferedInputStream.close();
        out.close();
        return responseToClient;
    }

    @Override
    public ArrayList<Character> getNewPlayerTiles(int amount) throws IOException, ClassNotFoundException {
        PrintWriter out = new PrintWriter(server.getOutputStream());
        BufferedInputStream bufferedInputStream = new BufferedInputStream(server.getInputStream());
        ObjectInputStream objectInputStream = new ObjectInputStream(bufferedInputStream);
        ArrayList<Character> playerTiles;
        out.println("GetNewTiles:" + amount);
        out.flush();
        playerTiles = (ArrayList<Character>) objectInputStream.readObject();
        objectInputStream.close();
        bufferedInputStream.close();
        out.close();
        return playerTiles;
    }

    @Override
    public void nextTurn() throws IOException {
        myTurn=false;
        PrintWriter out = new PrintWriter(server.getOutputStream());
        out.println("NextTurn");
        out.flush();
        out.close();
        Thread t = new Thread(()-> {
            try {
                this.waitForTurn();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        t.start();
    }
    @Override
    public void startGame()throws IOException, ClassNotFoundException{

    }

}
