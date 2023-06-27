package test;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.BitSet;

public class BloomFilter {
    private final BitSet b;
    private final ArrayList<MessageDigest> algo;
    /**
     * The BloomFilter function takes in a string and hashes it using the hash functions
     * that were passed into the constructor. It then sets those bits to 1.

     *
     * @param size int -Set the size of the bitset
     * @param algo A String array -Pass a variable number of arguments to the function
     *
     * @return A bloomfilter object
     *
     */
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
    /**
     * The add function takes a string and hashes it using the hash functions
     * in this.algo, then sets the corresponding bits in this.b to true.

     *
     * @param word String -Get the bytes of the word and then to hash it
     *
     * @return Void
     *
     * @docauthor Trelent
     */
    public void add(String word) {
        for(MessageDigest md:this.algo){
            byte[] bts=md.digest(word.getBytes());
            BigInteger bi = new BigInteger(1,bts);
            int val = bi.intValue();
            val = Math.abs(val)%this.b.size();
            this.b.set(val,true);
        }
    }
    /**
     * The contains function checks if the given word is in the BloomFilter.
     *
     * @param word String -Get the bytes of the word and then convert it to a biginteger
     *
     * @return True if the word is in the filter and false otherwise
     */
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
    /**
     * The toString function returns a string representation of the BitVector.
     *
     * @return A string representation of the bitset
     */
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
