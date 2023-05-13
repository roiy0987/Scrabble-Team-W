package test;


import java.util.HashMap;

public class DictionaryManager {
    private final HashMap<String, Dictionary> booksDictionaries;
    private static DictionaryManager dm=null;
    private DictionaryManager(){
        booksDictionaries = new HashMap<>();

    }
    public boolean query(String ... args){
        boolean isExist = false;
        for(int i = 0;i<args.length-1;i++){
            if(booksDictionaries.containsKey(args[i])) {
                if(booksDictionaries.get(args[i]).query(args[args.length - 1]))
                    isExist = true;
                continue;
            }
            booksDictionaries.put(args[i],new Dictionary(args[i]));
            if(booksDictionaries.get(args[i]).query(args[args.length - 1]))
                isExist = true;
        }
        return isExist;
    }
    public boolean challenge(String ... args){
        boolean isExist = false;
        for(int i = 0;i<args.length-1;i++){
            if(booksDictionaries.containsKey(args[i])) {
                if(booksDictionaries.get(args[i]).challenge(args[args.length - 1]))
                    isExist = true;
                continue;
            }
            booksDictionaries.put(args[i],new Dictionary(args[i]));
            if(booksDictionaries.get(args[i]).challenge(args[args.length - 1]))
                isExist = true;
        }
        return isExist;
    }
    public static int getSize(){
        return get().booksDictionaries.size();
    }
    public static DictionaryManager get(){
        if(dm==null)
            dm = new DictionaryManager();
        return dm;
    }
}
