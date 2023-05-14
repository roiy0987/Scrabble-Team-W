package Model;

import test.Board;
import test.Tile;
import test.Word;

import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class InGameData {

    private Board board;
    private HashMap<String, Integer> scores;
    private HashMap<String, ArrayList<Tile>> playerTiles;

    public InGameData() {
        board = Board.getBoard();
        scores = new HashMap<>();
        playerTiles = new HashMap<>();
    }

    public void addPlayer(String playerName) {
        scores.put(playerName, 0);
    }

    public void changeScore(String playerName, int playerScore) {
        scores.put(playerName, playerScore);
    }

    public String getScore() {
        String[] names = (String[]) scores.keySet().toArray();
        String response = "";
        for (int i = 0; i < names.length; i++) {
            response += names[i] + ":" + scores.get(names[i]);
            if (i < names.length - 1)
                response += "\n";
        }
        return response;
    }

    public Character[][] getBoard() {
        Character[][] updatedBoard = new Character[15][15];
        Tile[][] boardTiles = board.getTiles();
        for (int i = 0; i < boardTiles.length; i++) {
            for (int j = 0; j < boardTiles[i].length; j++) {
                updatedBoard[i][j] = boardTiles[i][j].letter;
            }
        }
        return updatedBoard;
    }

    public String submitWord(String[] requestSplitted) {
        String playerName = requestSplitted[5];
        // requestSplitted = "SubmitWord:CAT:4:3:true"
        int playerScore = board.tryPlaceWord(
                stringToWord(requestSplitted[1],
                        Integer.parseInt(requestSplitted[2]),
                        Integer.parseInt(requestSplitted[3]),
                        Boolean.parseBoolean(requestSplitted[4])));

        scores.put(playerName, scores.get(playerName) + playerScore);

        if (playerScore == 0)
            return "false";
        return "true";
    }

    public Word stringToWord(String word, int row, int col, boolean isVertical) {
        Tile[] t = new Tile[word.length()];
        for (int i = 0; i < word.length(); i++) {
            t[i] = Tile.Bag.getBag().getLetters()[word.charAt(i) - 'A'];
        }
        return new Word(t, row, col, isVertical);
    }
}
