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
