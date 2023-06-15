package Model;

import ViewModel.ScrabbleViewModel;

import java.io.IOException;
import java.util.ArrayList;
//facade
public interface ScrabbleModelFacade {
    boolean submitWord(String word, int row, int col, boolean isVertical) throws IOException, ClassNotFoundException;
    String getScore() throws IOException;
    Character[][] getBoard() throws IOException, ClassNotFoundException;
    ArrayList<Character> getNewPlayerTiles(int amount) throws IOException, ClassNotFoundException;
    void nextTurn() throws IOException, InterruptedException;
    ArrayList<Character> startGame() throws IOException, ClassNotFoundException;
    boolean isMyTurn();
    boolean isGameOver();
    void addObserver(ScrabbleViewModel vm);

    boolean isGameStarted();
}
