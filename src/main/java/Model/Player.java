package Model;

import java.net.Socket;

public class Player {
    String name;
    Socket socket;
    int score;

    /**
     * The Player function is a constructor for the Player class. It takes in three parameters:
     * name, socket, and score. The name parameter is used to set the player's username;
     * the socket parameter is used to set up a connection between two players; and
     * finally, score sets up an integer value that will be incremented as points are earned by each player.

     *
     * @param name String -Set the name of the player
     * @param socket Socket -Create a socket for the player
     * @param score int -Set the score of the player
     *
     * @return A player object
     *
     */
    Player(String name, Socket socket, int score){
        this.name = name;
        this.socket = socket;
        this.score = score;
    }


}
