package test;


import java.io.*;
import java.net.Socket;
import java.util.Scanner;


public class BookScrabbleHandler implements ClientHandler{
    DictionaryManager dm;
    private String[] fileNames;
    private  BooksDirectoryReader books= new BooksDirectoryReader();

    private boolean stop;

    public BookScrabbleHandler(){
        this.dm = DictionaryManager.get();
        fileNames = new String[books.getBooks().length+1];
        for(int i=0; i<books.getBooks().length;i++)
        {
            fileNames[i]= books.getBooks()[i];
        }
        stop = false;
    }

    @Override
    public void handleClient(Socket client) throws IOException {
//        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        Scanner in = new Scanner(client.getInputStream());
        PrintWriter out = new PrintWriter(client.getOutputStream(),true);
        while(!stop){
            String word;
            word=in.next();
            if(word==null)
                continue;
            fileNames[books.getBooks().length]=word;
            if(this.dm.query(fileNames) || this.dm.challenge(fileNames)){
                out.print("true\n");
                out.flush();
                continue;
            }
            out.print("false\n");
            out.flush();
        }
    }

    @Override
    public void close() {
        System.out.println("Stop book scrabble handler");
        stop = true;
    }


}