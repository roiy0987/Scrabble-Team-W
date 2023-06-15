package Model;

import test.ClientHandler;
import test.Tile;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import java.io.Serializable;

public class HostHandler implements ClientHandler {

    private boolean stop;
    private HostModel model;
    public HostHandler(HostModel model){
        this.model = model;
        stop = false;
    }



    @Override
    public void handleClient(Socket client) throws IOException, ClassNotFoundException, InterruptedException {
        InputStream inFromclient = client.getInputStream();
        OutputStream outToClient = client.getOutputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(inFromclient));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outToClient));
        String request;
        StringBuilder sb;

        while(!stop) {
            request = br.readLine();
            if(request==null)
                continue;
            String[] requestSplitted = request.split(":");

            switch (requestSplitted[0]) {
                case "Connect": // Connect:Michal
                    model.players.add(new Player(requestSplitted[1], client, 0));
                    //model.setChanged();
                    model.notifyObservers1();
                    bw.write("Ok"+"\n");
                    bw.flush();
                    System.out.println("Connected");
                    break;
                case "GetBoard": // GetBoard
                    Character[][] board = model.getBoardToCharacters();
                    sb = new StringBuilder();
                    for(int i =0; i<15; i++){
                        for(int j=0; j <15; j++){
                            sb.append(board[i][j]).append(':');
                        }
                        if(i != 14)
                            sb.append(";");
                    }
                    bw.write(sb.toString()+"\n");
                    bw.flush();
                    break;
                case "GetNewTiles": // GetNewTiles:{amount} returns null if bag is empty.
                    ArrayList<Character> tiles= model.getNewPlayerTiles(Integer.parseInt(requestSplitted[1]));
                    sb = new StringBuilder();
                    for(int i=0;i<tiles.size();i++){
                        sb.append(tiles.get(i));
                    }
                    bw.write(sb.toString()+"\n");
                    bw.flush();
                    break;
                case "GetScore": // GetScore
                    bw.write(model.getScore() + "\n");
                    bw.flush();
                    System.out.println("Score written");
                    break;
                case "SubmitWord": // SubmitWord:CAT:10:8
                    String response = Boolean.toString(model.submitWord(requestSplitted[1],
                            Integer.parseInt(requestSplitted[2]),
                            Integer.parseInt(requestSplitted[3]),
                            Boolean.parseBoolean(requestSplitted[4])));
                    bw.write(response+"\n");
                    bw.flush();
                    break;
                case "NextTurn": // nextTurn
                    model.nextTurn();
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    public void close() {
        stop = true;
    }
}
