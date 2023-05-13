package test;

import java.util.*;

public class CacheManager {
	private final HashSet<String> words;
    private final CacheReplacementPolicy crp;
    private final int maxSize;
	public CacheManager(int size, CacheReplacementPolicy crp){
        this.words =new HashSet<String>();
        this.crp = crp;
        this.maxSize=size;
    }
    public boolean query(String word){
        return this.words.contains(word);
    }

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
