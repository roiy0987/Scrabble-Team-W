package Model;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.Integer.parseInt;

//model
public class GuestModel implements ScrabbleModelFacade {

    protected Socket server;
    private String playerName;

    public GuestModel(String name, String ip, int port) throws IOException {
        this.playerName = name;
        server = new Socket(ip, port);
        // Connect to server with name and socket for blabla
    }


    @Override
    public boolean submitWord(String word, int row, int col, boolean isVertical) throws IOException {
        // user
        PrintWriter out = new PrintWriter(server.getOutputStream());
        Scanner in = new Scanner(server.getInputStream());
        out.println("SubmitWord:" + word + ":" + row + ":" + col + ":" + isVertical + ":" + playerName);
        out.flush();
        String response = in.next();
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
    public void nextTurn() {

    }

    public void startGame(){}

}
