package test;


import java.io.*;
import java.net.Socket;

public class BookScrabbleHandler implements ClientHandler{
    DictionaryManager dm;

    public BookScrabbleHandler(){
        this.dm = DictionaryManager.get();
    }

    @Override
    public boolean handleClient(Socket client) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        PrintWriter out = new PrintWriter(client.getOutputStream(),true);
        String word;
        word=in.readLine();
        if(this.dm.query(word) || this.dm.challenge(word)){
            out.println("true");
            out.flush();
            out.close();
            return true;
        }
        out.println("false");
        out.flush();
        out.close();
        return false;
    }

    @Override
    public void close() {

    }


}