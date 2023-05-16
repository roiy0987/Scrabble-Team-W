package test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ClientHandler {
	boolean handleClient(InputStream inFromclient, OutputStream outToClient) throws IOException;
	void close();
}
