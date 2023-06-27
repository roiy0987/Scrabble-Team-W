package test;


import java.util.HashMap;

public class DictionaryManager {
    private final HashMap<String, Dictionary> booksDictionaries;
    private static DictionaryManager dm=null;

    /**
     * The DictionaryManager function is a singleton class that manages the dictionaries of all books.
     * It has a HashMap that maps book names to their respective dictionaries.
     *
     * @return A new instance of dictionarymanager
     */
    private DictionaryManager(){
        booksDictionaries = new HashMap<>();
    }

    /**
     * The query function takes in a variable number of arguments, and returns true if the last argument is found in any of the dictionaries.
     *
     * @param args Pass in a variable number of arguments and the last one is the word to check
     *
     * @return True if the word is in any of the books, otherwise it returns false
     */
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
    /**
     * The challenge function takes in a variable number of arguments,
     * and checks if the last argument is contained within any of the dictionaries.
     *
     * @param args Pass in a variable number of arguments and the last one is the word to check
     *
     * @return True if the word is found in any of the dictionaries, and false otherwise
     */
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

    /**
     * The getSize function returns the size of the booksDictionaries array.
     *
     * @return The size of the booksdictionaries arraylist
     */
    public static int getSize(){
        return get().booksDictionaries.size();
    }

    /**
     * The get function is a static function that returns the singleton instance of
     * DictionaryManager. If no instance exists, it creates one and then returns it.
     *
     * @return A dictionarymanager object
     */
    public static DictionaryManager get(){
        if(dm==null)
            dm = new DictionaryManager();
        return dm;
    }
}
