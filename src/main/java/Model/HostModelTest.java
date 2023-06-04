package Model;

import test.Board;
import test.BookScrabbleHandler;
import test.MyServer;
import test.Tile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class HostModelTest {

    HostModel host;
    MyServer s;
    BookScrabbleHandler bsh;

    public void init() throws IOException, InterruptedException, ClassNotFoundException {
        bsh = new BookScrabbleHandler();
        s = new MyServer(8887,bsh);
        s.start();
        Thread.sleep(4000);
        host = new HostModel("Host", "localhost", 8887);
    }

    public void testStartGame() throws IOException, ClassNotFoundException {
        ArrayList<Character> list = host.startGame();
        if(!host.myTurn || list.size() != 7||host.board==null)
            System.out.println("Error in start game");
    }

    public void submitWord() throws IOException, ClassNotFoundException {
        if(!host.submitWord("HORN",7,5, false) && host.players.get(0).score == 14)
            System.out.println("Error in submit word");
        if(host.submitWord("SDF",7,7, false))
            System.out.println("Error in submit word #2");
        if(!host.submitWord("FA_M",5,7, true) && host.players.get(0).score == 23)
            System.out.println("Error in submit word #3");
        if(!host.submitWord("PASTE",9,5,false) && host.players.get(0).score == 48)
            System.out.println("Error in submit word #3");
        if(!host.submitWord("_OB",8,7, false) && host.players.get(0).score == 66)
            System.out.println("Error in submit word #3");
        if(!host.submitWord("BIT",10,4, false) && host.players.get(0).score == 88)
            System.out.println("Error in submit word #3");
        System.out.println(host.players.get(0).score);

    }

    public void testGetScore() throws IOException, ClassNotFoundException {
        if(!host.getScore().equals("Host:88"))
            System.out.println("Error in get score #1");
        System.out.println("*****TEST GET SCORE END*****");
    }

    // '\u0000' = null in char language
    public void testGetBoard() throws IOException, ClassNotFoundException {
        char[][] board = host.getBoard();
        char[][] expected = {
                {'\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000'},
                {'\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000'},
                {'\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000'},
                {'\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000'},
                {'\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000'},
                {'\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000','F', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000'},
                {'\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000','A', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000'},
                {'\u0000', '\u0000', '\u0000', '\u0000', '\u0000','H', 'O', 'R', 'N', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000'},
                {'\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000','M', 'O', 'B', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000'},
                {'\u0000', '\u0000', '\u0000', '\u0000', '\u0000','P', 'A', 'S', 'T', 'E', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000'},
                {'\u0000', '\u0000', '\u0000', '\u0000', 'B', 'I', 'T', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000'},
                {'\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000'},
                {'\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000'},
                {'\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000'},
                {'\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000'},
        };
        Board.getBoard().printBoard();
        for(int i=0;i<expected.length;i++){
            for(int j=0;j<expected[i].length;j++){
                if(expected[i][j] != board[i][j])
                    System.out.println("Error in board comparing");
            }
        }
    }

    public void testGetNewPlayerTiles() throws IOException, ClassNotFoundException {
        ArrayList<Character> list = new ArrayList<>();
        for(int i=0; i<12; i++){
            list = host.getNewPlayerTiles(7);
            if(list.size() != 7){
                System.out.println("Error in getNewPlayerTiles");
                break;
            }
        }
        list = host.getNewPlayerTiles(5);
        if(list.size() != 5)
            System.out.println("Error in getNewPlayerTiles");
        list = host.getNewPlayerTiles(7);
        if(list.size() != 2)
            System.out.println("Error in getNewPlayerTiles");
    }

    public void testNextTurn() throws IOException, InterruptedException {
        host.nextTurn();
        host.nextTurn();

    }
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        HostModelTest test = new HostModelTest();
        test.init();
        test.testStartGame();
        test.submitWord();
        test.testGetScore();
        test.testGetBoard();
        test.testGetNewPlayerTiles();
        test.testNextTurn();
        test.bsh.close();
        Thread.sleep(2000);
        //test.host.closeClient();
        Thread.sleep(2000);
        test.s.close();

    }


}
