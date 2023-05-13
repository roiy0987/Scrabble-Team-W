package test;


import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class BookScrabbleHandler implements ClientHandler {
    private final DictionaryManager dm;
    private Board board;


    public BookScrabbleHandler() {
        dm = DictionaryManager.get();
        board = Board.getBoard();
    }

    public void handleClient(InputStream inFromclient, OutputStream outToClient) throws IOException {
        //TODO
        BufferedReader bf = new BufferedReader(new InputStreamReader(inFromclient));
        String request = bf.readLine();
        String[] requestSplitted = request.split(":");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outToClient));
        ObjectOutputStream out = new ObjectOutputStream(outToClient);
        switch (requestSplitted[0]) {
            case "GetBoard":
                out.writeObject(getBoard());
                out.flush();
                break;
            case "GetNewTiles": // GetNewTiles:{amount}
                out.writeObject(getNewTiles(Integer.parseInt(requestSplitted[1])));
                out.flush();
                break;
            case "GetScore":
                // fetch from DB
                break;
            case "SubmitWord":
                bw.write(submitWord(requestSplitted));
                bw.flush();
                break;
            default:
                break;
        }
        bw.close();
        out.close();
    }

    @Override
    public void close() {

    }

    private String submitWord(String[] requestSplitted) {
        if (dm.query(requestSplitted[1]) || dm.challenge(requestSplitted[1])) {
            // requestSplitted = "SubmitWord:CAT:4:3:true"
            int score = board.tryPlaceWord(
                    stringToWord(requestSplitted[1],
                            Integer.parseInt(requestSplitted[2]),
                            Integer.parseInt(requestSplitted[3]),
                            Boolean.parseBoolean(requestSplitted[4])));
            // DB.store() ***** NEED TO ADD *****
            // true/false
            return "true";
        }
        return "false";
    }

    public Character[][] getBoard(){
        Character[][] updatedBoard = new Character[15][15];
        Tile[][] boardTiles = board.getTiles();
        for(int i = 0 ; i < boardTiles.length ; i++){
            for(int j=0;j<boardTiles[i].length;j++){
                updatedBoard[i][j] = boardTiles[i][j].letter;
            }
        }
        return updatedBoard;
    }

    public ArrayList<Character> getNewTiles(int amount)
    {
        ArrayList<Character> newTiles= new ArrayList<>();
        Tile newTile;
        for(int i=0; i<amount; i++)
        {
            newTile = Tile.Bag.getBag().getRand();
            newTiles.add(newTile.letter);
        }
        return newTiles;
    }

    public Word stringToWord(String word, int row, int col, boolean isVertical) {
        Tile[] t = new Tile[word.length()];
        for(int i=0; i< word.length(); i++)
        {
          t[i]= Tile.Bag.getBag().getLetters()[word.charAt(i)-'A'];
        }
        return new Word(t, row, col, isVertical);
    }

}
