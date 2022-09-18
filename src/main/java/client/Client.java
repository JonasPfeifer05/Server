package client;

import networking.Transfer;
import networking.Transferring;
import networking.server.PingRequest;
import networking.server.ServerTransfer;
import networking.user.EchoRequest;
import resources.StandartStatus;
import util.Logger;

import java.io.*;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created: 16.09.2022
 *
 * @author Jonas Pfeifer (jonas)
 */

public class Client implements Transferring {

	ObjectInputStream ois;
	ObjectOutputStream oos;

	Logger<StandartStatus> logger = new Logger<>(true);

	Socket socket;

	public Client(String host, int port) throws IOException {
		socket = new Socket(host, port);
		start();
	}

	public Client(Inet4Address address, int port) throws IOException {
		socket = new Socket(address, port);
		start();
	}

	private void start() {
		logger.addMessage(StandartStatus.INFORMATION, "Starting Setup!");
		setUp();
		logger.addMessage(StandartStatus.INFORMATION, "Finished Setup!");

		startReceiving();
	}

	private void setUp() {
		try {
			logger.addMessage(StandartStatus.INFORMATION, "Getting streams!");
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			logger.addMessage(StandartStatus.ERROR, "Error while getting streams!");
		}
	}

	@Override
	public void send(Transfer transfer) {
		try {
			oos.writeObject(transfer);
			logger.addMessage(StandartStatus.INFORMATION, "Sent package to: " + socket + " of type " + transfer);
		} catch (IOException e) {
			logger.addMessage(StandartStatus.PROBLEM, "Failed to send Package!");
			disconnect("Socket is closed!");
		}
	}

	@Override
	public void startReceiving() {
		logger.addMessage(StandartStatus.INFORMATION, "Starting traffic controller for server: " + socket);
		Thread trafficControl = new Thread(() ->
		{
			try {
				while (true) {
					try {
						Object o = ois.readObject();

						if (!(o instanceof ServerTransfer)) {
							logger.addMessage(StandartStatus.PROBLEM, "Got non server transfer package!");
						} else {
							ServerTransfer serverTransfer = (ServerTransfer) o;
							logger.addMessage(StandartStatus.INFORMATION, "Received Package from: " + socket + " of type " + serverTransfer);
							serverTransfer.handle(this);
						}

					} catch (ClassNotFoundException e) {
						logger.addMessage(StandartStatus.PROBLEM, "Received unknown package!");
					}
				}
			} catch (IOException e) {
				logger.addMessage(StandartStatus.ERROR, "Client got disconnected");
			}
			logger.addMessage(StandartStatus.INFORMATION, "Stopping traffic controller for server: " + socket);
		});
		trafficControl.start();
	}

	public final void disconnect(String reason) {
		logger.addMessage(StandartStatus.INFORMATION, "Disconnecting client " + socket + " because of: " + reason);
		try {
			socket.close();
		} catch (IOException ex) {
			logger.addMessage(StandartStatus.PROBLEM, ex.getMessage());
		}
	}

	public static void main(String[] args) throws IOException {
		Client client = new Client("localhost", 123);

		new Scanner(System.in).nextLine();

		client.send(new EchoRequest("Hallo Das ist ein Echo"));

		new Scanner(System.in).nextLine();
	}
}
