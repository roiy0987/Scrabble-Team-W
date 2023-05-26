package Model;

import test.BookScrabbleHandler;
import test.MyServer;
import java.io.IOException;

public class HostModelTest {

    HostModel host;
    MyServer s;

    public void init() throws IOException, InterruptedException, ClassNotFoundException {
        s = new MyServer(6666,new BookScrabbleHandler());
        s.start();
        Thread.sleep(5000);
        host = new HostModel("Host", "localhost", 6666);
        host.startGame();
    }

    public  void startGame(){

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

    public void testGetBoard() throws IOException, ClassNotFoundException {
        char[][] board = host.getBoard();
        char[][] expected = {
                {'\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000'},
                {'\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000'},
                {'\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000'},
                {'\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000'},
                {'\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000'},
                {'\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000'},
                {'\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000'},
                {'\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000'},
                {'\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000'},
                {'\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000'},
                {'\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000'},
                {'\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000'},
                {'\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000'},
                {'\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000'},
                {'\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000'},
        };
    }


    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        HostModelTest test = new HostModelTest();
        test.init();
        test.submitWord();
        test.testGetScore();
//        test.testGetBoard();
//        submitWord();


    }


}
