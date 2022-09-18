package server;

import networking.Transfer;
import networking.Transferring;
import networking.server.PingRequest;
import networking.server.ServerTransfer;
import networking.user.PingRespond;
import networking.user.UserTransfer;
import resources.StandartStatus;
import util.Logger;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created: 17.09.2022
 *
 * @author Jonas Pfeifer (jonas)
 */

public class ClientHandler implements Runnable, Transferring {
	public static ArrayList<ClientHandler> clients = new ArrayList<>();

	private final Socket userSocket;
	private final Logger<StandartStatus> logger;

	ObjectOutputStream objectOutputStream;
	ObjectInputStream objectInputStream;

	public ClientHandler(Socket userSocket, Logger<StandartStatus> logger) {
		this.userSocket = userSocket;
		this.logger = logger;
	}

	@Override
	public final void run() {
		setUp();
		authorizeUser();
		startReceiving();
	}

	public final void setUp() {
		try {
			objectOutputStream = new ObjectOutputStream(userSocket.getOutputStream());
			logger.addMessage(StandartStatus.INFORMATION, "Received Object writer for client: " + userSocket);
			objectInputStream = new ObjectInputStream(userSocket.getInputStream());
			logger.addMessage(StandartStatus.INFORMATION, "Received Object reader for client: " + userSocket);

			clients.add(this);
		} catch (IOException e) {
			logger.addMessage(StandartStatus.PROBLEM, "Couldn´t get streams from user!");
			disconnect("Couldn´t get streams!");
		}
	}

	private void authorizeUser() {
		int saveNumber = (int) (Math.random()*Integer.MAX_VALUE*2-Integer.MAX_VALUE);

		try {
			objectOutputStream.writeObject(new PingRequest(saveNumber));
			userSocket.setSoTimeout(5 * 1000);

			logger.addMessage(StandartStatus.INFORMATION, "Waiting for authorization");
			Object respond = this.getObjectInputStream().readObject();

			if (!(respond instanceof PingRespond)) {

				disconnect("Failed authorization");

			} else {

				userSocket.setSoTimeout(0);
				if (((PingRespond) respond).number != saveNumber) disconnect("Failed authorization");
				logger.addMessage(StandartStatus.INFORMATION, "Authorized user: " + userSocket);

			}
		} catch (IOException | ClassNotFoundException e) {
			disconnect("Failed authorization");
		}
	}

	@Override
	public void startReceiving() {
		logger.addMessage(StandartStatus.INFORMATION, "Accepting packages from client " + userSocket);
		Thread trafficControl = new Thread(() ->
		{
			try {
				while (true) {
					try {
						Object o = objectInputStream.readObject();

						if (!(o instanceof UserTransfer)) {
							logger.addMessage(StandartStatus.PROBLEM, "Got non user transfer package!");
						} else {
							UserTransfer userTransfer = (UserTransfer) o;
							logger.addMessage(StandartStatus.INFORMATION, "Received Package from " + userSocket + " of type " + userTransfer);
							userTransfer.handle(this);
						}

					} catch (ClassNotFoundException e) {
						logger.addMessage(StandartStatus.PROBLEM, "Received unknown package!");
					}
				}
			} catch (IOException e) {
				logger.addMessage(StandartStatus.ERROR, "Cant reach Client");
			}
			disconnect("Cant reach Client");
			logger.addMessage(StandartStatus.INFORMATION, "Not accepting anymore packages from user " + userSocket);
		});
		trafficControl.start();
	}

	public static void disconnectAll(String reason) {
		while (clients.size() > 0) {
			clients.get(0).disconnect(reason);
		}
	}

	public final void disconnect(String reason) {
		logger.addMessage(StandartStatus.INFORMATION, "Disconnecting client " + userSocket + " because of: " + reason);
		clients.remove(this);
		try {
			userSocket.close();
		} catch (IOException ex) {
			logger.addMessage(StandartStatus.PROBLEM, ex.getMessage());
		}
	}

	@Override
	public void send(Transfer transfer) {
		try {
			objectOutputStream.writeObject(transfer);
			logger.addMessage(StandartStatus.INFORMATION, "Sent package to: " + userSocket + " of type " + transfer);
		} catch (IOException e) {
			logger.addMessage(StandartStatus.PROBLEM, "Failed to send Package!");
			disconnect("Socket is closed!");
		}
	}

	public final Socket getUserSocket() {

		return userSocket;
	}

	public final Logger<StandartStatus> getLogger() {
		return logger;
	}

	public final ObjectOutputStream getObjectOutputStream() {
		return objectOutputStream;
	}

	public final ObjectInputStream getObjectInputStream() {
		return objectInputStream;
	}
}
