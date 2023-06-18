package test;


import java.io.*;
import java.util.Scanner;

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
            System.out.println("Word is valid by -> IOSearcher!");
            this.cLRU.add(word);
            return true;
        }
        this.cLFU.add(word);
        return false;

    }
}
