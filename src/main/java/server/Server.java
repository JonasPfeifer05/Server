package server;

import exception.ServerConstructionException;
import resources.StandartStatus;
import util.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created: 16.09.2022
 *
 * @author Jonas Pfeifer (jonas)
 */

public class Server {
	private final ServerSocket serverSocket;
	private final Logger<StandartStatus> logger;
	private Thread entryPoint;

	private ArrayList<Socket> users = new ArrayList<>();

	public Server(int port, int maxConnections) throws ServerConstructionException {
		logger = new Logger<>(true);

		logger.addMessage(StandartStatus.STARTING, "Creating the Server Instance!");

		try {
			serverSocket = new ServerSocket(port, maxConnections);
		} catch (IOException | IllegalArgumentException | SecurityException e) {
			logger.addMessage(StandartStatus.ERROR, "Error accrued while creating the Instance: " + e.getMessage());
			throw new ServerConstructionException(e.getMessage());
		}

		logger.addMessage(StandartStatus.STARTING, "Finished creating the Server Instance!");

		start();
	}

	public void start() {
		entryPoint = new Thread() {
			@Override
			public void run() {
				logger.addMessage(StandartStatus.INFORMATION, "Starting EntryPoint!");
				while (!serverSocket.isClosed()) {
					try {
						Socket user = serverSocket.accept();
						users.add(user);
						logger.addMessage(StandartStatus.INFORMATION, "New User joined with following data: " + user);
						new Thread(new ClientHandler(user, logger)).start();
					} catch (IOException e) {
						logger.addMessage(StandartStatus.PROBLEM, "Error accrued while accepting Users: " + e.getMessage());
					}
				}
				logger.addMessage(StandartStatus.SHUTDOWN, "Stopping EntryPoint!");
			}
		};
		entryPoint.start();
	}

	public void shutDown() {
		try {
			serverSocket.close();
			logger.addMessage(StandartStatus.SHUTDOWN, "ServerSocket closed!");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) {
		try {
			Server server = new Server(123, 10);
			new Scanner(System.in).nextLine();
			server.shutDown();
		} catch (ServerConstructionException e) {
			throw new RuntimeException(e);
		}
	}
}
