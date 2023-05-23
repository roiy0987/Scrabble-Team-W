package Model;

import java.net.Socket;

public class Player {
    String name;
    Socket socket;
    int score;

    Player(String name, Socket socket, int score){
        this.name = name;
        this.socket = socket;
        this.score = score;
    }


}
