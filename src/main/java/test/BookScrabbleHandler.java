package test;


import java.io.*;
import java.util.ArrayList;

public class BookScrabbleHandler implements ClientHandler {
    private final DictionaryManager dm;

    private final InGameData db;

    public BookScrabbleHandler() {
        dm = DictionaryManager.get();
        db = new InGameData();
    }

    public boolean handleClient(InputStream inFromclient, OutputStream outToClient) throws IOException {
        //TODO
        BufferedReader bf = new BufferedReader(new InputStreamReader(inFromclient));
        String request = bf.readLine();
        if(request==null)
            return false;
        String[] requestSplitted = request.split(":");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outToClient));
        ObjectOutputStream out = new ObjectOutputStream(outToClient);
        boolean isSuccess=true;
        switch (requestSplitted[0]) {
            case "GetBoard":
                out.writeObject(db.getBoard());
                out.flush();
                break;
            case "GetNewTiles": // GetNewTiles:{amount}
                out.writeObject(getNewTiles(Integer.parseInt(requestSplitted[1])));
                out.flush();
                break;
            case "GetScore":
                bw.write(db.getScore());
                bw.flush();
                break;
            case "SubmitWord":
                String response = submitWord(requestSplitted);
                bw.write(response);
                bw.flush();
                if(response.startsWith("false"))
                    isSuccess=false;
                break;
            default:
                break;
        }
        bw.close();
        out.close();
        return isSuccess;
    }

    @Override
    public void close() {

    }

    private String submitWord(String[] requestSplitted) {
        String answer = "false";
        if (dm.query(requestSplitted[1]) || dm.challenge(requestSplitted[1])) {
            answer = db.submitWord(requestSplitted);
        }
        return answer;
    }

    public ArrayList<Character> getNewTiles(int amount) {
        ArrayList<Character> newTiles = new ArrayList<>();
        Tile newTile;
        for (int i = 0; i < amount; i++) {
            newTile = Tile.Bag.getBag().getRand();
            newTiles.add(newTile.letter);
        }
        return newTiles;
    }
    
}
