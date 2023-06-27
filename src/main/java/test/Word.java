package test;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Word {
    private Tile[] tiles;
	private int row;
    private int col;
    private boolean vertical;


    /**
     * The Word function returns the word that is formed by the tiles in a Word object.
     *
     * @param tiles Store the tiles that make up this word
     * @param row Set the row of the word
     * @param col Set the column of the first tile in a word
     * @param vertical Determine if the word is vertical or horizontal
     *
     * @return A word object
     */
    public Word(Tile[] tiles, int row, int col, boolean vertical) {
        this.tiles = tiles;
        this.col=col;
        this.row=row;
        this.vertical=vertical;
    }
    /**
     * The getTiles function returns the tiles array.
     *
     * @return An array of tiles
     */
    public Tile[] getTiles() {
        return tiles;
    }
    /**
     * The getRow function returns the row of the current position.
     *
     * @return The row number of first letter of word
     */
    public int getRow() {
        return row;
    }
    /**
     * The getCol function returns the column of the current position.
     *
     * @return The column number of first letter of word
     */
    public int getCol() {
        return col;
    }
    /**
     * The isVertical function returns a boolean value that indicates whether the
     * current instance of the class is vertical or not.
     *
     * @return A boolean value of true or false depending on the orientation of the word
     */
    public boolean isVertical() {
        return vertical;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Word word = (Word) o;
        return row == word.row && col == word.col && vertical == word.vertical && Arrays.equals(tiles, word.tiles);
    }
}
