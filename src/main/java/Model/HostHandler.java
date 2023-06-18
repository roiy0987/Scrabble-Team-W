package Model;

import test.ClientHandler;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class HostHandler implements ClientHandler {

    private boolean stop;
    private HostModel model;
    public HostHandler(HostModel model){
        this.model = model;
        stop = false;
    }

    @Override
    public void handleClient(Socket client) {
        InputStream inFromClient = null;
        try {
            inFromClient = client.getInputStream();
            OutputStream outToClient = client.getOutputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inFromClient));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outToClient));
            String request;
            StringBuilder sb;
            while(!stop) {
                request = br.readLine();
                if(request==null) {
                    continue;
                }
                String[] requestSplitted = request.split(":");

                switch (requestSplitted[0]) {
                    case "Connect": // Connect:Michal
                        if(model.players.size()==4){
                            bw.write("GameIsFull"+"\n");
                            bw.flush();
                            return;
                        }
                        for(int i=0;i<model.players.size();i++){
                            if(requestSplitted[1].equals(model.players.get(i).name))
                            {
                                bw.write("NameIsTaken"+"\n");
                                bw.flush();
                                return;
                            }
                        }
                        model.players.add(new Player(requestSplitted[1], client, 0));
                        //model.setChanged();
                        model.update();
                        bw.write("Ok"+"\n");
                        bw.flush();
                        System.out.println(requestSplitted[1]+"Connected!");
                        break;
                    case "Disconnect":
                        //System.out.println("Disconnect started!");
                        for(int i=0;i<this.model.players.size();i++){
                            if(this.model.players.get(i).name.equals(model.name))
                                continue;
                            if(this.model.players.get(i).name.equals(requestSplitted[1])){
                                if(!this.model.players.get(i).socket.isClosed()){
                                    this.model.players.get(i).socket.close();
                                }
                                System.out.println(this.model.players.get(i).name + " Disconnected Successfully!");
                                this.model.players.remove(this.model.players.get(i));
                                this.model.update();
                            }
                        }
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
        } catch (IOException e) {
            //TODO
            //Need to end the game of the player which it is his turn now.
            //model.endGame();
            throw new RuntimeException("Socket Closed!");
        } catch (ClassNotFoundException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void close() {
        stop = true;
    }
}
