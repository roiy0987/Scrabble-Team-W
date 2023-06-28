package test;


import java.io.*;
import java.util.Scanner;

public class Dictionary {
    private final CacheManager cLFU;
    private final CacheManager cLRU;
    private final BloomFilter bf;
    private final String[] fileNames;
    /**
     * The Dictionary function is the constructor of the class, takes in a list of file names and creates a dictionary
     * that contains all the words from those files. It also creates two caches, one
     * using LFU and another using LRU. The cache size for LFU is 100 while the cache size for LRU is 400.
     *
     * @param fileNames An array of String
     *
     */
    public Dictionary(String ... fileNames)  {
        try {
            this.fileNames = fileNames;
            cLFU = new CacheManager(100, new LFU());
            cLRU = new CacheManager(400, new LRU());
            bf = new BloomFilter(32768,"MD5","SHA1","MD2","SHA256","SHA512");
            for (String file : fileNames) {
                Scanner reader = new Scanner(new File(file));
                while (reader.hasNext()) {
                    String s = reader.nextLine();
                    String[] sArr = s.split("\\W+");
                    for (String value : sArr) {
                        if(value.equals("")||value.length()==1)
                            continue;
                        bf.add(value);
                    }
                }
                reader.close();
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * The query function takes in a word and returns whether the word is
     * contained within the Bloom Filter. If it is, then we add it to our cache of
     * recently used words (cLRU). If it isn't, then we add it to our cache of
     * frequently used words (cLFU). This way, if a user searches for a word that
     * isn't in the Bloom Filter but has been searched for before by other users,
     * they will be notified that this particular word was not found. However, if
     * they search for something that's never been searched before, it adds to
     * the necessary CacheManager.
     *
     * @param word String Check if the word is in the cache
     *
     * @return True if the word is in the LRU or in the BloomFilter and false otherwise.
     *
     */
    public boolean query(String word){
        if(this.cLRU.query(word))
            return true;
        if(this.cLFU.query(word))
            return false;
        if(bf.contains(word)){
            this.cLRU.add(word);
            return true;
        }
        this.cLFU.add(word);
        return false;
    }
    /**
     * The challenge function takes in a string and checks if it is valid.
     * The function search through all of our files using IOSearcher to find out if that word exists in any of them.
     *
     * @param word String -Search the word in the file
     *
     * @return A boolean
     *
     */
    public boolean challenge(String word) {
        boolean b;
        try{
            b = IOSearcher.search(word,this.fileNames);
        }catch (Exception e){
            return false;
        }
        if(b){
            System.out.println("Word is valid by -> IOSearcher!");
            this.cLRU.add(word);
            return true;
        }
        this.cLFU.add(word);
        return false;

    }
}
