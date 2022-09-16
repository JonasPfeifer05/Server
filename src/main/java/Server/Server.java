package Server;

import exception.ServerConstructionException;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created: 16.09.2022
 *
 * @author Jonas Pfeifer (jonas)
 */

public class Server {
	private final ServerSocket serverSocket;

	public Server(int port, int maxConnections) throws ServerConstructionException{
		try {
			serverSocket = new ServerSocket(port, maxConnections);
		} catch (IOException | IllegalArgumentException | SecurityException e) {
			throw new ServerConstructionException(e.getMessage());
		}
	}
}
