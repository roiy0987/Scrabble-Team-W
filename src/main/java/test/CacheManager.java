package test;

import java.util.*;

public class CacheManager {
	private final HashSet<String> words;
    private final CacheReplacementPolicy crp;
    private final int maxSize;
    /**
    * The CacheManager constructor is responsible to initialize all the data members of the class.
    *
    * @param size int -Set the maxsize of the cache
    * @param crp CacheReplacementPolicy -Determine which cache replacement policy to use
    *
    * @return A new CacheManager object
    *
    */
    public CacheManager(int size, CacheReplacementPolicy crp){
        this.words =new HashSet<String>();
        this.crp = crp;
        this.maxSize=size;
    }
    /**
     * The query function takes in a string and returns true if the word is in the dictionary, false otherwise.
     *
     *
     * @param word String -Check if the word is in the words arraylist
     *
     * @return True if the word is in the dictionary, false otherwise
     */
    public boolean query(String word){
        return this.words.contains(word);
    }

    /**
     * The add function adds a word to the cache. If the cache is full, it removes
     * a word from the cache depending on the CacheReplacementPolicy, then adds the given word.
     *
     * @param word String
     *
     */
    public void add(String word){
        if(this.words.size()!=maxSize){
            this.words.add(word);
            this.crp.add(word);
            return;
        }
        this.words.remove(this.crp.remove());
        this.crp.add(word);
        this.words.add(word);
    }
}
