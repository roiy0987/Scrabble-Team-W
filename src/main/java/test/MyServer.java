package test;

import test.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

            //threadPool.shutdown(); // Close the thread pool gracefully
            Thread.sleep(5000);
            if(!threadPool.isTerminated()) {
                System.out.println("there are some threads not closed");
            }
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() throws InterruptedException {
        gameStarted = true;
        Thread.sleep(3000);
        try {
            threadPool.shutdown();
            System.out.println(threadPool.awaitTermination(3,TimeUnit.SECONDS));
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}
