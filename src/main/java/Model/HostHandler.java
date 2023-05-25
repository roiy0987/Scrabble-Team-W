package Model;

import test.ClientHandler;
import test.Tile;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class HostHandler implements ClientHandler {
    private HostModel model;
    public HostHandler(HostModel model){
        this.model = model;
    }
    @Override
    public void handleClient(Socket client) throws IOException, ClassNotFoundException {
        InputStream inFromclient = client.getInputStream();
        OutputStream outToClient = client.getOutputStream();
        BufferedReader bf = new BufferedReader(new InputStreamReader(inFromclient));
        String request = bf.readLine();
        if(request==null)
            return;
        String[] requestSplitted = request.split(":");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outToClient));
        ObjectOutputStream out = new ObjectOutputStream(outToClient);

        switch (requestSplitted[0]) {
            case "GetBoard": // GetBoard
                out.writeObject(model.getBoardToCharacters());
                out.flush();
                break;
            case "GetNewTiles": // GetNewTiles:{amount} returns null if bag is empty.
                out.writeObject(model.getNewPlayerTiles(Integer.parseInt(requestSplitted[1])));
                out.flush();
                break;
            case "GetScore": // GetScore
                bw.write(model.getScore()+"\n");
                bw.flush();
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
            case "Connect": // Connect:Michal
                model.players.add(new Player(requestSplitted[1],client,0));
                bw.write("Ok\n");
                bw.flush();
                break;
            default:
                break;
        }
    }


    @Override
    public void close() {

    }
}
