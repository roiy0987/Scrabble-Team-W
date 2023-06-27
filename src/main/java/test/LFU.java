package test;

import java.util.*;

public class LFU implements CacheReplacementPolicy {
    private final LinkedHashMap<String,Integer> wordsByOrder;
    private final LinkedHashMap<String,Integer> frequencies;
    public LFU(){
        wordsByOrder = new LinkedHashMap<>();
        frequencies = new LinkedHashMap<>();
    }

    public LinkedHashMap<String, Integer> getWordsByOrder() {
        return wordsByOrder;
    }


    //add in O(1) or add in O(N)
    /**
     * The add function adds a word to the map. If the word is already in the map,
     * it increments its frequency by 1. Otherwise, it adds a new entry with
     * frequency 1.
     *
     * @param  word Add a word to the map
     */
    public void add(String word){
        if(wordsByOrder.containsKey(word)){
            frequencies.put(word,this.wordsByOrder.get(word)+1);
        }
        else{
            wordsByOrder.put(word,1);
            frequencies.put(word,1);
        }
    }
    //remove in O(N) or remove in O(log(N))
    /**
     * The remove function removes the least frequently used word from the cache.
     *
     * @return The string that was removed
     */
    public String remove(){
        if(wordsByOrder.size()==0)
            return null;
        int minFrequency = Integer.MAX_VALUE;
        String minKey=null;
        for (Map.Entry<String, Integer> entry : frequencies.entrySet()) {
            String key = entry.getKey();
            int frequency = entry.getValue();
            if (frequency < minFrequency) {
                minFrequency = frequency;
                minKey = key;
            }
        }
        this.wordsByOrder.remove(minKey);
        frequencies.remove(minKey);
        return minKey;
    }
}
