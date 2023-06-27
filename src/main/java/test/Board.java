package test;

import java.util.ArrayList;

public class Board {
    private Tile[][] tiles;
    private int amountOfWords;
    
    public static Board board = null;
    private ArrayList<Word> boardWords;
    /**    
     * The Board function is a constructor for the Board class. It initializes all the tiles in the board to null, and sets
     * amountOfWords to 0.
     *
     * @return A board object
     *
     */
    private Board(){
        this.amountOfWords=0;
        this.tiles=new Tile[15][15];
        for(int i=0;i<15;i++){
            for(int j=0;j<15;j++)
                this.tiles[i][j]=null;
        }
        this.boardWords=new ArrayList<Word>();
    }
    /**    
     * The getTiles function returns a copy of the tiles array.
     *
     * @return A copy of the tiles array
     *
     */
    public Tile[][] getTiles(){
        return this.tiles.clone();
    }
    /**    
     * The isWordThere function checks if the word is there in the board.
     *
     * @param word Word -Get the length of the word
     * @param row int -Check if the word is in the same row as the letter
     * @param col int -Check if the column of the word is equal to the col parameter
     * @param isVertical boolean -Determine whether the word is vertical or horizontal
     *
     * @return True if the word is in the board
     *
     */
    public boolean isWordThere(Word word,int row,int col,boolean isVertical){
        int length = word.getTiles().length;
        int startLetterRow=word.getRow();
        int startLetterCol= word.getCol();
        if(isVertical){
            if(startLetterCol!=col) return false;
            if(startLetterRow>row)
                return false;
            if(startLetterRow+length<row)
                return false;
            return true;
        }
        if(startLetterRow!=row)return false;
        if(startLetterCol>col)
            return false;
        if(startLetterCol+length<col)
            return false;
        return true;
    }
    /**    
     * The boardLegal function checks if the word is legal to be placed on the board.
     * It checks if it's connected to other words, and that it doesn't overlap with any letters already on the board.
     *
     * @param word Word -Get the row, column, and tiles of the word
     *
     * @return True if the word is legal on the board and false otherwise
     */
    public boolean boardLegal(Word word){
        int col = word.getCol();
        int row = word.getRow();
        int length = word.getTiles().length;
        boolean isConnected=false;
        Tile[] wordTiles=word.getTiles();
        boolean isVertical = word.isVertical();
        if(row<0||col<0||row>14||col>14||length>15)
            return false;
        if(isVertical){
            if(row+length>15)
                return false;
            if(this.amountOfWords==0)
                return isWordThere(word,7,7,word.isVertical());
            for (int i=row;i<row+length;i++)
            {
                if(wordTiles[i-row]==null){
                    if(this.tiles[i][col]==null)
                        return false;
                    if(tiles[i][col+1]!=null||tiles[i][col-1]!=null)
                        isConnected=true;
                    else
                        return false;
                    continue;
                }
                if(tiles[i][col]!=null&&wordTiles[i-row].letter>='A'&&wordTiles[i-row].letter<='Z')// need to check with option of FA_M...
                    return false;
                if(col==14)
                {
                    if(tiles[i][col-1]!=null)
                        isConnected=true;
                    continue;
                }
                if(col==0)
                {
                    if(tiles[i][col+1]!=null)
                        isConnected=true;
                    continue;
                }
                if(tiles[i][col+1]!=null||tiles[i][col-1]!=null)
                    isConnected=true;
            }
            return isConnected;
        }
        if(col+length>15)
            return false;
        if(this.amountOfWords==0)
            return isWordThere(word,7,7,word.isVertical());
        for (int i=col;i<col+length;i++)
        {
            if(wordTiles[i-col]==null){
                if(this.tiles[row][i]==null)
                    return false;
                if(tiles[row+1][i]!=null||tiles[row-1][i]!=null)
                    isConnected=true;
                else
                    return false;
                continue;
            }
            if(tiles[row][i]!=null&&wordTiles[i-col].letter>='A'&&wordTiles[i-col].letter<='Z')
                return false;
            if(row==14)
            {
                if(tiles[row-1][i]!=null)
                    isConnected=true;
                continue;
            }
            if(row==0)
            {
                if(tiles[row+1][i]!=null)
                    isConnected=true;
                continue;
            }
            if(tiles[row+1][i]!=null||tiles[row-1][i]!=null)
                isConnected=true;
        }
        return isConnected;
    }
    /**    
     * The dictionaryLegal function checks if the word is a valid English word.
     *
     * @param word Word -Get the tiles from the word, and to see if it is vertical or horizontal
     *
     * @return True if the word is in the dictionary, false otherwise
     *
     */
    public boolean dictionaryLegal(Word word){
        DictionaryCommunication dc = DictionaryCommunication.getInstance();
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<word.getTiles().length;i++) {
            if(word.getTiles()[i]==null){
                if(word.isVertical()){
                    sb.append(board.tiles[word.getRow()+i][word.getCol()].letter);
                    continue;
                }
                sb.append(board.tiles[word.getRow()][word.getCol()+i].letter);
                continue;
            }
            sb.append(word.getTiles()[i].letter);
        }
        if(!dc.checkIfWordValid(sb.toString()))
            return false;
        return true;
    }
    /**    
     * The getBoard function is a static function that returns the Singleton board object.
     *
     * @return The board object
     *
     */
    public static Board getBoard(){
        if(board==null)
            board=new Board();
        return board;
    }
    /**    
     * The getFirstLetterRow function takes in a row and column number
     * and returns the first letter that is connected from there in that column.
     *
     * @param row int -Get the row of the first letter in a word
     * @param col int -Determine the column of the first letter in a word
     * @return An int
     *
     */
    public int getFirstLetterRow(int row,int col){
        int firstLetterRow=row;
        for(int i=firstLetterRow-1;i>0;i--)
            if(this.tiles[i][col]==null)
                return i+1;
        return 0;
    }
    /**    
     * The tileVerticalWord function takes in a row and column number, and returns the word that is formed by the tiles
     * vertically above or below it. If there are no tiles above or below it, then null is returned.
     *
     * @param row int-Determine the row of the first letter in a word
     * @param col int-Determine the column of the first letter in a word
     *
     * @return A word object that represents the word formed by
     *
     */
    public Word tileVerticalWord(int row,int col){
        int i=1;
        int len=verticalLength(row,col);
        int firstLetterRow=getFirstLetterRow(row,col);
        if(len==1) return null;
        Tile[] t = new Tile[len];
        for(int j=0;j<len;j++)
        {
            t[j]=this.tiles[firstLetterRow+j][col];
        }
        Word w = new Word(t,firstLetterRow,col,true);
        return w;
    }
    /**    
     * The verticalLength function returns the length of a vertical line of tiles
     * starting at (row,col) and extending in the positive row direction.  If there is no tile at (row,col), then 0 is returned.
     *
     * @param row int -Determine the row of the tile that is being checked
     * @param col int -Determine the column of the tile
     *
     * @return The number of tiles in a vertical line starting at the given row and column
     *
     */
    public int verticalLength(int row,int col){
        int i=1;
        int len=1;
        if(row>0&&row<14){
            for(;row+i<15;i++){
                if(this.tiles[row+i][col]==null)
                    break;
                len++;
            }
            for(i=1;row-i>0;i++){
                if(this.tiles[row-i][col]==null)
                    break;
                len++;
            }
            return len;
        }
        if(row==0){
            for(;row+i<15;i++){
                if(this.tiles[row+i][col]==null)
                    break;
                len++;
            }
            return len;
        }
        for(;row-i>0;i++){
            if(this.tiles[row-i][col]==null)
                break;
            len++;
        }
        return len;
    }
    /**    
     * The nonVerticalLength function is used to find the length of a non-vertical line.
     *
     * @param row int -Determine the row that the tile is in
     * @param col int -Determine the column of the tile
     *
     * @return The length of the non-vertical line
     *
     */
    public int nonVerticalLength(int row,int col){
        int i=1;
        int len=1;
        if(col>0&&col<14){
            for(;col+i<15;i++){
                if(this.tiles[row][col+i]==null)
                    break;
                len++;
            }
            for(i=1;col-i>0;i++){
                if(this.tiles[row][col-i]==null)
                    break;
                len++;
            }
            return len;
        }
        if(col==0){
            for(;col+i<15;i++){
                if(this.tiles[row][col+i]==null)
                    break;
                len++;
            }
            return len;
        }
        for(;col-i>0;i++){
            if(this.tiles[row][col-i]==null)
                break;
            len++;
        }
        return len;
    }
    /**    
     * The nonVerticalTileWord function takes in a row and column number as parameters.
     * It then checks if the tile at that position is null, if it is not null, it will check to see how many tiles are connected to the right of this tile.
     * If there are no tiles connected to the right of this tile, then we return null because there is no word formed by just one letter.
     * Otherwise we create an array of Tiles with length equal to the number of letters in our non-vertical word (including our starting letter). 
     * We also keep track of which column contains our first letter so that when we add
     *
     * @param row int -Specify the row of the tile that is being checked
     * @param col int -Determine the column of the first letter in a word
     * @return A word that is not vertical
     *
     */
    public Word nonVerticalTileWord(int row,int col){
        int i=1;
        int len=nonVerticalLength(row,col);
        int firstLetterCol=col;
        if(len==1) return null;
        Tile[] t = new Tile[len];
        for(;col-i>0;i++){
            if(this.tiles[row][col-i]==null)
                break;
            firstLetterCol--;
        }
        for(int j=0;j<len;j++)
        {
            t[j]=this.tiles[row][firstLetterCol+j];
        }
        Word w = new Word(t,row,firstLetterCol,false);
        return w;
    }
    /**    
     * The getWords function returns an ArrayList of Word objects that are formed by the tiles in the given word.
     *
     * @param word Word -Get the tiles of the word
     * @return An arraylist of words
     *
     */
    public ArrayList<Word> getWords(Word word){
        Tile[] tilesOfWord =word.getTiles();
        int col = word.getCol();
        int row = word.getRow();
        int length = tilesOfWord.length;
        boolean isV = word.isVertical();
        boolean isInListOfBoardWords=false;
        if(!dictionaryLegal(word))
            return null;
        ArrayList<Word> words=new ArrayList<Word>();
        words.add(word);
        if(this.amountOfWords==1)
            return words;
        for (int i=0;i<length;i++){
            Word w = null;
            if(!isV){
                if(tilesOfWord[i]!=null)
                    w = tileVerticalWord(row,col+i);
            }
            else{
                if(tilesOfWord[i]!=null)
                    w = nonVerticalTileWord(row+i,col);
            }
            if(w!=null){
                for(Word word1:this.boardWords){
                    if(isEqual(word1,w)) {
                        isInListOfBoardWords = true;
                        break;
                    }
                }
                if(!isInListOfBoardWords){
                    if(!dictionaryLegal(sendToDictionaryLegal(word,w)))
                        return null;
                    words.add(w);
                }
                isInListOfBoardWords=false;
            }
        }
        return words;
    }
    /**    
     * The sendToDictionaryLegal function takes in a submitted word and a word to finish.
     * It then creates an array of tiles that is the same length as the word to finish,
     * and fills it with tiles from both words. If there is no tile at that position in the
     * WordToFinish, it will take one from submittedWord instead. The function returns this new Word object.
     *
     * @param submittedWord Word
     * @param wordToFinish Word 
     *
     * @return A new word that is the combination of the submittedword and wordtofinish
     *
     */
    public Word sendToDictionaryLegal(Word submittedWord,Word wordToFinish){
        Tile[] t = new Tile[wordToFinish.getTiles().length];
        int row,col,firstRow,firstCol;
        firstRow=wordToFinish.getRow();
        firstCol=wordToFinish.getCol();
        for(int i=0;i<t.length;i++){
            if(wordToFinish.getTiles()[i]!=null){
                t[i]=wordToFinish.getTiles()[i];
                continue;
            }
            if(wordToFinish.isVertical()) {
                if(i==0)firstRow=submittedWord.getRow();
                row=submittedWord.getRow();
                col= submittedWord.getCol();
                while(row<wordToFinish.getRow()+i)row++;
                while(col!=wordToFinish.getCol())col++;
                t[i] = submittedWord.getTiles()[col-submittedWord.getCol()+row-submittedWord.getRow()];
                continue;
            }
            if(i==0)firstCol=submittedWord.getCol();
            row=submittedWord.getRow();
            col= submittedWord.getCol();
            while(col<wordToFinish.getCol()+i)col++;
            while(row!=wordToFinish.getRow())row++;
            t[i] = submittedWord.getTiles()[col-submittedWord.getCol()+row-submittedWord.getRow()];
        }
        return new Word(t,firstRow,firstCol,wordToFinish.isVertical());
    }
    /**    
     * The isEqual function checks if two words are equal.
     *
     * @param w1 Word 
     * @param w2 Word 
     *
     * @return A boolean
     *
     */
    public boolean isEqual(Word w1,Word w2){
        if(w1.getTiles().length!=w2.getTiles().length)
            return false;
        int i,len=w1.getTiles().length;
        Tile[] t1 = w1.getTiles();
        Tile[] t2 = w2.getTiles();
        for(i=0;i<len;i++){
            if(t1[i]==null&&t2[i]==null)continue;
            if(t1[i]==null){
                if(w1.isVertical()){
                    if(this.tiles[w1.getRow()+i][w1.getCol()].letter!=t2[i].letter)
                        return false;
                    continue;
                }
                if(this.tiles[w1.getRow()][w1.getCol()+i].letter!=t2[i].letter)
                    return false;
                continue;
            }
            if(t2[i]==null){
                if(w2.isVertical()){
                    if(this.tiles[w2.getRow()+i][w2.getCol()].letter!=t1[i].letter)
                        return false;
                    continue;
                }
                if(this.tiles[w2.getRow()][w2.getCol()+i].letter!=t1[i].letter)
                    return false;
                continue;
            }
            if(t1[i].letter!=t2[i].letter)
                return false;
        }
        return true;
    }
    /**    
     * The checkBonus function checks the board for any bonus squares and returns a string
     * that represents the type of bonus square. The function takes in two parameters, row and col,
     * which are used to determine what kind of bonus square is at that location on the board.
     *
     * @param row int
     * @param col int
     *
     * @return A string 
     *
     */
    public String checkBonus(int row,int col){
        switch(row){
            case 0:case 14:
                if(col==3||col==11)
                    return "dl";
                if(col==7||col==0||col==14)
                    return "tw";
                break;
            case 1:case 13:
                if(col==1||col==13)
                    return "dw";
                if(col==5||col==9)
                    return "tl";
                break;
            case 2:case 12:
                if(col==2||col==12)
                    return "dw";
                if(col==6||col==8)
                    return "dl";
                break;
            case 3:case 11:
                if(col==0||col==14||col==7)
                    return "dl";
                if(col==3||col==11)
                    return "dw";
                break;
            case 4: case 10:
                if(col==4||col==10)
                    return "dw";
                break;
            case 5: case 9:
                if(col==5||col==9||col==1||col==13)
                    return "tl";
                break;
            case 6: case 8:
                if(col==2||col==12||col==6||col==8)
                    return "dl";
                break;
            case 7:
                if(col==0||col==14)
                    return "tw";
                if(col==3||col==11)
                    return "dl";
                if(col==7)
                    return "dw";
                break;
            default:
                break;
        }
        return "";
    }
    /**    
     * The getScore function takes a Word object as an argument and returns the score of that word.
     * The function first gets all the words in the board by calling getWords(Word) function.
     * Then it iterates over each word, calculates its score and adds it to total score.
     *
     * @param word Word
     *
     * @return The score of the word that is placed on the board
     *
     */
    public int getScore(Word word){
        ArrayList<Word> words = getWords(word);
        int score=0;
        int wordScore=0;
        int doubleWordBonus=0;
        int tripleWordBonus=0;
        int len,i,row,col;
        boolean isV = word.isVertical();
        for(Word w:words){
            Tile[] tilesOfWord=w.getTiles();
            len = tilesOfWord.length;
            isV= w.isVertical();
            row= w.getRow();
            col= w.getCol();
            wordScore=0;
            doubleWordBonus=0;
            tripleWordBonus=0;
            for(i=0;i<len;i++){
                if(this.amountOfWords!=1 && col==7 && row+i==7 && isV){
                    wordScore+=this.tiles[row+i][col].score;
                    continue;
                }
                if(this.amountOfWords!=1 && col+i==7 && row==7 && !isV){
                    wordScore+=this.tiles[row][col+i].score;
                    continue;
                }
                String bonus;
                if(isV){
                    bonus=checkBonus(row+i,col);
                }
                else{
                    bonus=checkBonus(row,col+i);
                }
                if(bonus.intern().equals("dl"))
                {
                    if(isV){
                        wordScore+=this.tiles[row+i][col].score*2;
                    }
                    else
                        wordScore+=this.tiles[row][col+i].score*2;
                    continue;
                }
                if(bonus.intern().equals("tl"))
                {
                    if(isV){
                        wordScore+=this.tiles[row+i][col].score*3;
                    }
                    else
                        wordScore+=this.tiles[row][col+i].score*3;
                    continue;
                }
                if(bonus.intern().equals("dw"))
                {
                    doubleWordBonus+=1;
                }
                if(bonus.intern().equals("tw"))
                {
                    tripleWordBonus+=1;
                }

                if(isV){
                    wordScore+=this.tiles[row+i][col].score;
                }
                else
                    wordScore+=this.tiles[row][col+i].score;
            }
            if(doubleWordBonus!=0){
                score=score+doubleWordBonus*2*wordScore;
                continue;
            }
            if(tripleWordBonus!=0){
                if(tripleWordBonus==2)
                    score=score+9*wordScore;
                else
                    score=score+3*wordScore;
                continue;
            }
            score+= wordScore;
        }
        return score;
    }
    /**    
     * The tryPlaceWord function takes in a word and checks if it is legal to place on the board.
     * If it is, then the function will try to place the word on the board with all the new words
     * that been created as well checks if they are all valid.
     * 
     *
     * @param word Word
     *
     * @return An int - returns the score of the given word, if the word wasn't placed returns 0.
     *
     */
    public int tryPlaceWord(Word word){
        if(!boardLegal(word))
            return 0;
        ArrayList<Word> wordList = getWords(word);
        if(wordList==null)
            return 0;
        Tile[] tilesOfWord=word.getTiles();
        int i=0;
        int row=word.getRow();
        int col= word.getCol();
        int score=0;
        boolean vertical = word.isVertical();
        int len = tilesOfWord.length;
        for(;i<len;i++){
            if(tilesOfWord[i]==null){
                continue;
            }
            if(vertical){
                this.tiles[row+i][col]=tilesOfWord[i];
                continue;
            }
            this.tiles[row][col+i]=tilesOfWord[i];
        }
        this.boardWords.add(word); //need to do something if there are _ letters....
        this.amountOfWords+=1;
        score = getScore(word);
        wordList = getWords(word);
        for(Word w:wordList){
            if(!isEqual(w,word)){
                this.boardWords.add(w);
                this.amountOfWords+=1;
            }
        }
        return score;
    }

    /**    
     * The printBoard function prints the board to the console.
     *
     */
    public void printBoard() {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (tiles[i][j] == null)
                    System.out.print("_ ");
                else
                    System.out.print(tiles[i][j].letter + " ");
            }
            System.out.println();
        }

    }
}
