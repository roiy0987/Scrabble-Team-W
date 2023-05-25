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
    boolean gameStarted;
    public boolean finishedGame;

    public MyServer(int port, ClientHandler ch) {
        this.port = port;
        this.ch = ch;
        gameStarted=true;
        finishedGame=false;
    }

    public void start() {
        new Thread(() -> startServer()).start();
    }

    private void startServer() {
        try {
            ServerSocket server = new ServerSocket(this.port);
            server.setSoTimeout(1000);
            while (!gameStarted) {
                try {
                    Socket client = server.accept();
                    new Thread(()-> {
                        try {
                            ch.handleClient(client);
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }).start();
                    //client.close();
                } catch (SocketTimeoutException e) {
                }
            }
            ch.wait();
            ch.close();
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public void close() {
        gameStarted = true;
    }


}
