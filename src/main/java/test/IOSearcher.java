package test;

import java.io.*;
public class IOSearcher {
    /**
     * The search function takes a String word and an array of Strings fileNames as parameters.
     * It returns true if the word is found in any of the files, false otherwise.
     *
     * @param word String -Search for the word in the file
     * @param fileNames An array of String -Pass a variable number of arguments to the function
     *
     * @return A boolean
     *
     */
    public static boolean search(String word,String ... fileNames) throws Exception{
        for(String file:fileNames){
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String s =reader.readLine();
            while(s!=null)
            {
                String[] sArr = s.split("[\"\\s,?!.-]+");
                for (String value : sArr) {
                    if (value.toUpperCase().equals(word))
                        return true;
                }
                s =reader.readLine();
            }
            reader.close();
        }
        return false;
    }
}
