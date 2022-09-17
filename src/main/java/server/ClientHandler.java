package server;

import resources.StandartStatus;
import util.Logger;

import java.io.*;
import java.net.Socket;

/**
 * Created: 17.09.2022
 *
 * @author Jonas Pfeifer (jonas)
 */

public class ClientHandler implements Runnable {
	Socket socket;
	Logger<StandartStatus> logger;
	public ClientHandler(Socket socket, Logger<StandartStatus> logger) {
		this.socket = socket;
		this.logger = logger;
	}

	@Override
	public void run() {
		try {
			logger.addMessage(StandartStatus.INFORMATION,"Starting Client handler for: " + socket);

			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			logger.addMessage(StandartStatus.INFORMATION, "Received Object writer for client: " + socket);
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			logger.addMessage(StandartStatus.INFORMATION, "Received Object reader for client: " + socket);

			while (true) {
				try {
					logger.addMessage(StandartStatus.INFORMATION, socket + ": " + ois.readObject());
				} catch (IOException e) {
					logger.addMessage(StandartStatus.INFORMATION, "User left: " + socket);
					break;
				} catch (ClassNotFoundException e) {
					logger.addMessage(StandartStatus.PROBLEM, "User sent unknown class object!");
				}
			}
		} catch (IOException e) {
			logger.addMessage(StandartStatus.PROBLEM, "Couldn´t get streams from user!");
		}
	}
}
