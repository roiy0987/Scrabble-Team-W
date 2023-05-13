package test;


import java.util.Objects;

public class Tile {
    public final char letter;
    public final int score;

    private Tile(char letter, int score) {
        this.letter = letter;
        this.score = score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        return letter == tile.letter && score == tile.score;
    }

    @Override
    public int hashCode() {
        return Objects.hash(letter, score);
    }
    public static class Bag{
        private int[] amount;
        private int size=98;
        public static Bag b=null;
        private Tile[] letters;
        private final int[] maxAmount;
        private Bag(){
            letters = new Tile[]{new Tile('A', 1), new Tile('B', 3), new Tile('C', 3), new Tile('D', 2),
                    new Tile('E', 1), new Tile('F', 4), new Tile('G', 2),
                    new Tile('H', 4), new Tile('I', 1), new Tile('J', 8), new Tile('K', 5), new Tile('L', 1),
                    new Tile('M', 3), new Tile('N', 1), new Tile('O', 1), new Tile('P', 3), new Tile('Q', 10),
                    new Tile('R', 1), new Tile('S', 1), new Tile('T', 1), new Tile('U', 1), new Tile('V', 4),
                    new Tile('W', 4), new Tile('X', 8), new Tile('Y', 4), new Tile('Z', 10)};
            amount=new int[]{9,2,2,4,12,2,3,2,9,1,1,4,2,6,8,2,1,6,4,6,4,2,2,1,2,1};
            maxAmount=amount.clone();

        }
        public Tile getRand(){
            if(size==0)
                return null;
            int rand = (int)Math.floor(Math.random()*(26));
            while(amount[rand]==0)
                rand = (int)Math.floor(Math.random()*(26));
            amount[rand]--;
            size--;
            return letters[rand];
        }
        public Tile getTile(char letter){
            if(letter<'A'||letter>'Z')
                return null;
            if(amount[letter-'A']==0){
                return null;
            }
            amount[letter-'A']--;
            size--;
            return letters[letter-'A'];
        }
        public void put(Tile t){
            if(size!=98&&amount[t.letter-'A']!=maxAmount[t.letter-'A']){
                amount[t.letter-'A']++;
                size++;
            }
        }
        public int size(){
            return size;
        }
        public static Bag getBag(){
            if(b==null){
                b=new Bag();
            }
            return b;
        }
        public int[] getQuantities(){
            return this.amount.clone();
        }
    }

}
