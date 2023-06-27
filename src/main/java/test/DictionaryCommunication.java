package test;

import java.io.*;
import java.net.Socket;

public class DictionaryCommunication {
    private static DictionaryCommunication dc = null;
    private Socket server;

    /**
     * The DictionaryCommunication function is a singleton class that allows the client to communicate with the server.
     * It has one public method, sendMessage, which takes in a string and sends it to the server.
     *
     * @return A dictionarycommunication object
     */
    private DictionaryCommunication(){
        try {
            server = new Socket("localhost",8887);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * The checkIfWordValid function takes in a String word and checks if it is valid.
     *
     * @param word Send the word to the server
     *
     * @return True if the word is valid, false otherwise
     */
    public boolean checkIfWordValid(String word){
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
            BufferedReader br = new BufferedReader(new InputStreamReader(server.getInputStream()));
            bw.write(word + "\n");
            bw.flush();
            return br.readLine().equals("true");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * The close function closes the server socket.
     */
    public void close(){
        try {
            server.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * The getInstance function is a singleton function that returns the same instance of DictionaryCommunication every time it is called.
     * This allows for multiple classes to use the same instance of DictionaryCommunication, and thus access the same dictionary.
     *
     * @return An instance of the dictionarycommunication class
     */
    public static DictionaryCommunication getInstance(){
        if(dc==null)
            dc=new DictionaryCommunication();
        return dc;
    }
}
