package test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public interface ClientHandler {
	void handleClient(Socket client) throws IOException, ClassNotFoundException, InterruptedException;
	void close();
}
