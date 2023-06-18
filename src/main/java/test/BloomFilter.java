package test;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.BitSet;

public class BloomFilter {
    private final BitSet b;
    private final ArrayList<MessageDigest> algo;
    public BloomFilter(int size, String ... algo) {
        this.algo = new ArrayList<>();
        for(int i=0;i<algo.length;i++){
            try {
                this.algo.add(MessageDigest.getInstance(algo[i]));
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
        b = new BitSet(size);
    }
    public void add(String word) {
        for(MessageDigest md:this.algo){
            byte[] bts=md.digest(word.getBytes());
            BigInteger bi = new BigInteger(1,bts);
            int val = bi.intValue();
            val = Math.abs(val)%this.b.size();
            this.b.set(val,true);
        }
    }
    public boolean contains(String word) {
        System.out.println(word);
        for(MessageDigest md:this.algo) {
            byte[] bts = md.digest(word.getBytes());
            BigInteger bi = new BigInteger(1,bts);
            int val = bi.intValue();
            val = Math.abs(val)%this.b.size();
            if(!this.b.get(val)){
                return false;
            }
        }
        System.out.println("Word is valid by -> BloomFilter!");
        return true;
    }
    public String toString(){
        StringBuilder s= new StringBuilder();
        for(int i = 0 ; i < this.b.length();i++){
            if(b.get(i))
                s.append("1");
            else
                s.append("0");
        }
        return s.toString();
    }
}
