package test;

import test.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyServer {
    private ClientHandler ch;
    private int port;
    private boolean gameStarted;
    private ServerSocket server;
    private ExecutorService threadPool;

    public MyServer(int port, ClientHandler ch) {
        this.port = port;
        this.ch = ch;
        gameStarted = false;
        threadPool = Executors.newFixedThreadPool(3); // Set maximum of 3 threads in the pool
    }

    public void start() {
        new Thread(() -> startServer()).start();
    }

    private void startServer() {
        try {
            server = new ServerSocket(this.port);
            server.setSoTimeout(1000);
            while (!gameStarted) {
                try {
                    Socket client = server.accept();
                    threadPool.execute(() -> {
                        try {
                            ch.handleClient(client);
                        } catch (IOException | ClassNotFoundException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    //client.close();
                } catch (SocketTimeoutException e) {
                }
            }
            System.out.println("ShutDown");
            threadPool.shutdown(); // Close the thread pool gracefully
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() throws InterruptedException {
        gameStarted = true;
        //Thread.sleep(3000);
        System.out.println("close method");
        try {
            threadPool.shutdownNow(); // Interrupt and stop all threads in the pool immediately
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}
