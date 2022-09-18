package server;

import exception.ServerConstructionException;
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
	private Thread saveClose = new Thread(new Runnable() {
		@Override
		public void run() {
			shutdown();
		}
	});

	public Server(int port, int maxConnections) throws ServerConstructionException {
		logger = new Logger<>(true);


		try {
			logger.addMessage(StandartStatus.STARTING, "Creating Server Socket!");
			serverSocket = new ServerSocket(port, maxConnections);
		} catch (IOException | IllegalArgumentException | SecurityException e) {
			logger.addMessage(StandartStatus.ERROR, "Error accrued while creating the Server Socket: " + e.getMessage());
			throw new ServerConstructionException(e.getMessage());
		}

		setup();
	}

	private void setup() {
		Runtime.getRuntime().addShutdownHook(saveClose);
		startAccepting();
	}

	public void startAccepting() {
		Thread entrance = new Thread() {
			@Override
			public void run() {
				logger.addMessage(StandartStatus.INFORMATION, "Starting to accept Clients!");
				while (!serverSocket.isClosed()) {
					try {
						Socket user = serverSocket.accept();
						logger.addMessage(StandartStatus.INFORMATION, "New Client joined with following Data " + user);

						ClientHandler client = new ClientHandler(user, logger);
						logger.addMessage(StandartStatus.INFORMATION, "Created ClientHandler for Client " + user);
						new Thread(client).start();
					} catch (IOException e) {
						logger.addMessage(StandartStatus.PROBLEM, "Error accrued while accepting Clients: " + e.getMessage());
					}
				}
				logger.addMessage(StandartStatus.SHUTDOWN, "Not accepting any more Clients!");
				logger.addMessage(StandartStatus.INFORMATION, "Kicking all Clients!");
				ClientHandler.disconnectAll("Shutting down!");
			}
		};
		entrance.start();
	}

	public void shutdown() {
		try {
			serverSocket.close();
			logger.addMessage(StandartStatus.SHUTDOWN, "ServerSocket closed!");
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
		} catch (ServerConstructionException e) {
			throw new RuntimeException(e);
		}
	}
}
