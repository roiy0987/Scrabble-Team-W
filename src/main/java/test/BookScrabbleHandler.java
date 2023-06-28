package test;


import java.io.*;
import java.net.Socket;


public class BookScrabbleHandler implements ClientHandler{
    DictionaryManager dm;
    private String[] fileNames;
    private  BooksDirectoryReader books= new BooksDirectoryReader();

    private boolean stop;

    /**
     * The BookScrabbleHandler function is a constructor that initializes the DictionaryManager,
     * and creates an array of Strings to hold the names of books. It also sets stop to false.
     *
     */
    public BookScrabbleHandler(){ // Need to get names of books
        this.dm = DictionaryManager.get();
        fileNames = new String[3];
        for(int i=0; i<2;i++)
        {
            fileNames[i]= books.getBooks()[i];
        }
        stop = false;
    }

    /**
     * The handleClient function is called by the server when a new client connects.
     * It reads in the word that was sent from the client and checks if it is in
     * either of our dictionaries. If it is, then we send back &quot;true&quot; to indicate
     * that this word exists, otherwise we send back &quot;false&quot;. We also check for any
     * challenge words sent from clients and add them to our dictionary if they are not already there.
     *
     * @param client Socket
     *
     */
    @Override
    public void handleClient(Socket client)  {
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(),true);
            while(!stop){
                String word;
                word=in.readLine();
                if(word==null)
                    continue;
                fileNames[2]=word;
                if( this.dm.query(fileNames)||this.dm.challenge(fileNames)){
                    out.print("true\n");
                    out.flush();
                    continue;
                }
                out.print("false\n");
                out.flush();
            }
        }catch (IOException e){

        }

    }

    /**
     * The close function is used to stop the thread from running.
     * It sets the boolean variable &quot;stop&quot; to true, which causes the while loop in run()
     * to terminate. This allows for a clean shutdown of this thread when it is no longer needed.
     *
     */
    @Override
    public void close() {
        stop = true;
    }


}