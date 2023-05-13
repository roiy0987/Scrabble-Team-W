package test;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Dictionary {
    private final CacheManager cLFU;
    private final CacheManager cLRU;
    private final BloomFilter bf;
    private final String[] fileNames;
    public Dictionary(String ... fileNames)  {
        try {
            this.fileNames = fileNames;
            cLFU = new CacheManager(100, new LFU());
            cLRU = new CacheManager(400, new LRU());
            bf = new BloomFilter(256, "SHA1", "MD5");
            for (String file : fileNames) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String s = reader.readLine();
                while (s != null) {
                    String[] sArr = s.split(" ");
                    for (String value : sArr) {
                        bf.add(value);
                    }
                    s = reader.readLine();
                }
                reader.close();
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
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
    public boolean challenge(String word) {
        boolean b;
        try{
            b = IOSearcher.search(word,this.fileNames);
        }catch (Exception e){
            return false;
        }
        if(b){
            this.cLRU.add(word);
            return true;
        }
        this.cLFU.add(word);
        return false;

    }
}
