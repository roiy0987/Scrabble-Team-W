package test;


import java.io.*;
import java.net.Socket;

public class BookScrabbleHandler implements ClientHandler{
    DictionaryManager dm;
    BufferedReader in;
    PrintWriter out;
    public BookScrabbleHandler(){
        this.dm = DictionaryManager.get();
    }

    @Override
    public boolean handleClient(Socket client) throws IOException {
        this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        this.out = new PrintWriter(client.getOutputStream(),true);
        String word;
        word=in.readLine();
        if(!this.dm.query(word) || !this.dm.challenge(word)){
            out.println("false");
            out.flush();
            out.close();
            return false;
        }
        out.println("true");
        out.flush();
        out.close();
        return true;
    }

    @Override
    public void close() {

    }


}