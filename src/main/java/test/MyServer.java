package test;



import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List; 

public class MyServer {
    private ClientHandler ch;
    private int port;
    boolean gameStarted = false;
    boolean gameFinished = false;
    private List<Socket> players;

    private int currentPlayerIndex = 0;

    public MyServer(int port, ClientHandler ch) {
        this.port = port;
        this.ch = ch;
        this.players = new ArrayList<>();
    }

    public void start() {
        gameStarted = false;
        new Thread(() -> startServer()).start();
    }

    private void startServer() {
        try {
            ServerSocket server = new ServerSocket(this.port);
            server.setSoTimeout(1000);
            while (!gameStarted) {
                try {
                    Socket client = server.accept();
                    if(client!=null&&players.size()<=4)
                        players.add(client);
//                    ch.handleClient(client.getInputStream(),client.getOutputStream());
//                    ch.close();
//                    client.close();
                } catch (SocketTimeoutException e) {
                }
            }
            Collections.shuffle(players);
            while (!gameFinished) {
                while(!ch.handleClient(players.get(currentPlayerIndex).getInputStream(), players.get(currentPlayerIndex).getOutputStream())){
                    ch.close();
                }
                currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
                ch.close();
            }
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startGame(){
        gameStarted=true;
    }
    public void close() {
        gameFinished = true;
    }


}
