package test;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.BitSet;
import java.util.Collections;
import java.util.Vector;

public class BloomFilter {
	private final BitSet b;
    private final Vector<String> algo;
    private int maxBit;
    public BloomFilter(int size, String ... algo) {
        this.algo = new Vector<>();
        Collections.addAll(this.algo, algo);
        maxBit=0;
        b= new BitSet(size);
    }
    public void add(String word) {
        for(String s:this.algo){
            MessageDigest md= null;
            try {
                md = MessageDigest.getInstance(s);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            byte[] bts=md.digest(word.getBytes());
            BigInteger bi = new BigInteger(bts);
            int val = bi.intValue();
            val = Math.abs(val);
            if(val%this.b.size()>maxBit)
                maxBit=val%this.b.size()+1;
            this.b.set(val%this.b.size());
        }
    }
    public boolean contains(String word) {
        for(String s:this.algo) {
            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance(s);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            byte[] bts = md.digest(word.getBytes());
            BigInteger bi = new BigInteger(bts);
            int val = bi.intValue();
            val = Math.abs(val);
            if(!this.b.get(val%this.b.size())){
                return false;
            }
        }
        return true;
    }
    public String toString(){
        StringBuilder s= new StringBuilder();
        for(int i = 0 ; i < this.maxBit;i++){
            if(b.get(i))
                s.append("1");
            else
                s.append("0");
        }
        return s.toString();
    }
}
