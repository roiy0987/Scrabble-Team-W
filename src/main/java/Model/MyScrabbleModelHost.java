package Model;

import test.BookScrabbleHandler;
import test.MyServer;

import java.io.IOException;
import java.net.ServerSocket;

public class MyScrabbleModelHost extends MyScrabbleModel{
    private ServerSocket guestServer;
    private MyServer myServer;
    public MyScrabbleModelHost(String name, String ip, int port) throws IOException {
        super(name, ip, port);
        myServer = new MyServer(port+1,new BookScrabbleHandler());
        guestServer = new ServerSocket(port);
    }

}
