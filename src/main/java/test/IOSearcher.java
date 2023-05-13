package test;

import java.io.*;
public class IOSearcher {
    public static boolean search(String word,String ... fileNames) throws Exception{
        for(String file:fileNames){
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String s =reader.readLine();
            while(s!=null)
            {
                String[] sArr =s.split(" ");
                for (String value : sArr) {
                    if (value.equals(word))
                        return true;
                }
                s =reader.readLine();
            }
            reader.close();
        }
        return false;
    }
}
