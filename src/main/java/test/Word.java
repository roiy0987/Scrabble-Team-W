package test;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Word {
    private Tile[] tiles;
	private int row;
    private int col;
    private boolean vertical;


    public Word(Tile[] tiles, int row, int col, boolean vertical) {
        this.tiles = tiles;
        this.col=col;
        this.row=row;
        this.vertical=vertical;
    }
    public Tile[] getTiles() {
        return tiles;
    }
    public int getRow() {
        return row;
    }
    public int getCol() {
        return col;
    }
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
