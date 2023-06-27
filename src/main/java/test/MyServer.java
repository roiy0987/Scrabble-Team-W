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
    private boolean stop;
    private ServerSocket server;
    private ExecutorService threadPool;


    /**
     * The MyServer function is a constructor for the MyServer class.
     * It initializes the port number, client handler and stop boolean to be used by the server.
     * It also creates a thread pool of cached threads that will be used by the server to handle clients.
     *
     * @param port int -Set the port number that the server will listen to
     * @param ch ClientHandler -Handle the client's request
     *
     * @return MyServer Object
     *
     */
    public MyServer(int port, ClientHandler ch) {
        this.port = port;
        this.ch = ch;
        stop = false;
        threadPool = Executors.newCachedThreadPool();
    }

    /**
     * The start function starts the server in a new Thread.
     *
     */
    public void start() {
        new Thread(() -> startServer()).start();
    }

    /**
     * The startServer function is responsible for starting the server.
     * It creates a new ServerSocket and sets its timeout to 1000 milliseconds.
     * Then, it enters a loop that waits for clients to connect, and when they do,
     * it handles them with the handleClient function from the ClientHandler facade.
     *
     */
    private void startServer() {
        try {
            server = new ServerSocket(this.port);
            server.setSoTimeout(1000);
            while (!stop) {
                try {
                    Socket client = server.accept();
                    threadPool.execute(() -> ch.handleClient(client));
                } catch (SocketTimeoutException e) {}
            }
            // Delay in order to guest to recieve the message
            Thread.sleep(3000);
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * The close function is used to close the thread pool.
     * It sets the stop variable to true, and then shuts down
     * the thread pool and end the server by exiting the loop in the startServer function. If there are any threads that are still running,
     * it will wait for them to finish before shutting down.
     *
     */
    public void close() throws InterruptedException {
        stop = true;
        try {
            threadPool.shutdown();
            threadPool.awaitTermination(3,TimeUnit.SECONDS);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}
