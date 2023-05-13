package test;

import java.util.ArrayList;

public class Board {
    private Tile[][] tiles;
    private int amountOfWords;
    public Tile[][] getTiles(){
        return this.tiles.clone();
    }
    public static Board board = null;
    private ArrayList<Word> boardWords;
    private Board(){
        this.amountOfWords=0;
        this.tiles=new Tile[15][15];
        for(int i=0;i<15;i++){
            for(int j=0;j<15;j++)
                this.tiles[i][j]=null;
        }
        this.boardWords=new ArrayList<Word>();
    }
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
    public boolean dictionaryLegal(Word word){
        return true;
    }
    public static Board getBoard(){
        if(board==null)
            board=new Board();
        return board;
    }
    public int getFirstLetterRow(int row,int col){
        int firstLetterRow=row;
        for(int i=firstLetterRow-1;i>0;i--)
            if(this.tiles[i][col]==null)
                return i+1;
        return 0;
    }
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
    public ArrayList<Word> getWords(Word word){
        Tile[] tilesOfWord =word.getTiles();
        int col = word.getCol();
        int row = word.getRow();
        int length = tilesOfWord.length;
        boolean isV = word.isVertical();
        boolean isInListOfBoardWords=false;
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
                    if(!dictionaryLegal(w))
                        return null;
                    words.add(w);
                }
                isInListOfBoardWords=false;
            }
        }
        return words;
    }
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
}
