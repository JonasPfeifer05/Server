package server;

import exception.ConstructionException;
import resources.StandartStatus;
import util.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created: 16.09.2022
 *
 * @author Jonas Pfeifer (jonas)
 */

public class Server {
	private final ServerSocket serverSocket;
	private final Logger<StandartStatus> logger;
	private final Thread saveClose = new Thread(() -> shutdown());

	public Server(int port, int maxConnections) throws ConstructionException {
		logger = new Logger<>(true);

		try {
			serverSocket = new ServerSocket(port, maxConnections);
			logger.log(StandartStatus.STARTING, "Created server socket!");
		} catch (IOException | IllegalArgumentException | SecurityException e) {
			logger.log(StandartStatus.ERROR, "Error accrued while creating the server socket: " + e.getMessage());
			throw new ConstructionException(e.getMessage());
		}

		setup();
	}

	private void setup() {
		Runtime.getRuntime().addShutdownHook(saveClose);
		startAccepting();
	}

	public void startAccepting() {
		Thread entrance = new Thread(() -> {
			logger.log(StandartStatus.INFORMATION, "Starting to accept Clients!");
			while (!serverSocket.isClosed()) {
				try {
					Socket user = serverSocket.accept();
					logger.log(StandartStatus.INFORMATION, "New Client joined with following Data " + user);

					ClientHandler client = new ClientHandler(user, logger);
					logger.log(StandartStatus.INFORMATION, "Created ClientHandler for Client " + user);
					new Thread(client).start();
				} catch (IOException e) {
					logger.log(StandartStatus.PROBLEM, "Error accrued while accepting Clients: " + e.getMessage());
				}
			}
			logger.log(StandartStatus.SHUTDOWN, "Not accepting any more Clients!");
			logger.log(StandartStatus.INFORMATION, "Kicking all Clients!");
			ClientHandler.disconnectAll("Shutting down!");
		});
		entrance.start();
	}

	public void shutdown() {
		try {
			serverSocket.close();
			logger.log(StandartStatus.SHUTDOWN, "ServerSocket closed!");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Runtime.getRuntime().removeShutdownHook(saveClose);
	}

	public static void main(String[] args) {
		try {
			Server server = new Server(123, 1);
			new Scanner(System.in).nextLine();
			server.shutdown();
		} catch (ConstructionException e) {
			throw new RuntimeException(e);
		}
	}
}
