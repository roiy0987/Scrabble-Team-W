package Model;

import java.io.IOException;
import java.util.ArrayList;
//facade
public interface ScrabbleModelFacade {
    boolean submitWord(String word, int row, int col, boolean isVertical, String playerName) throws IOException;
    String getScore() throws IOException;
    char[][] getBoard() throws IOException, ClassNotFoundException;
    ArrayList<Character> getNewPlayerTiles(int amount) throws IOException, ClassNotFoundException;
    void startGame();
    
}
