package test;


import java.io.*;
import java.util.Arrays;
import java.util.HashMap;

public class BookScrabbleHandler implements ClientHandler {
    private final DictionaryManager dm;
    private Board board;
    private HashMap<String,Integer> wordToScore;

    public BookScrabbleHandler(){
        dm = DictionaryManager.get();
        board = Board.getBoard();
        wordToScore = new HashMap<>();
    }

    public void handleClient(InputStream inFromclient, OutputStream outToClient) throws IOException{
        //TODO
        BufferedReader bf = new BufferedReader(new InputStreamReader(inFromclient));
        String request = bf.readLine();
        String[] requestSplitted = request.split(":");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outToClient));
        switch (requestSplitted[0]) {
            case "GetBoard":

                break;
            case "GetNewTiles":

                break;
            case "GetScore":

                break;
            case "SubmitWord":
                if(dm.query(requestSplitted[1]) || dm.challenge(requestSplitted[1])){
                    // requestSplitted = "SubmitWord:CAT:4:3:true"
                    int score = board.tryPlaceWord(
                            stringToWord(requestSplitted[1],
                            Integer.parseInt(requestSplitted[2]),
                            Integer.parseInt(requestSplitted[3]),
                            Boolean.parseBoolean(requestSplitted[4])));


                }
                break;
            default:
                break;
        }
    }

    public Word stringToWord(String word, int row, int col, boolean isVertical){
        //TODO

        return null;
    }
    public void close(){

    }

}
