package test;
import java.util.*;

public class LRU implements CacheReplacementPolicy {
    private final LinkedHashSet<String> words;
    public LRU(){
        this.words= new LinkedHashSet<String>();
    }

    public LinkedHashSet<String> getWords() {
        return words;
    }

    /**
     * The add function adds a word to the list of words.
     *
     * @param word Add a word to the list
     */
    public void add(String word){
        if(words.contains(word)){
            this.words.remove(word);
        }
        this.words.add(word);
    }

    /**
     * The remove function removes the first element in the list.
     *
     * @return The first element in the set
     */
    public String remove(){
        if(this.words.size()==0)
            return null;
        String s = this.words.iterator().next();
        this.words.remove(s);
        return s;
    }
}
