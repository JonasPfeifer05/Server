package client;

import networking.BasicProtocol;
import networking.Transfer;
import networking.Networking;
import networking.protocols.echo.EchoRequest;
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

public class Client implements Networking {

	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	private final Logger<StandartStatus> logger = new Logger<>(true);

	private final Socket socket;

	private final Thread saveClose = new Thread(new Runnable() {
		@Override
		public void run() {
			shutdown();
		}
	});

	public Client(String host, int port) throws IOException {
		socket = new Socket(host, port);
		start();
	}

	public Client(Inet4Address address, int port) throws IOException {
		socket = new Socket(address, port);
		start();
	}

	private void start() {
		setUp();

		startReceiving();
	}

	private void setUp() {
		Runtime.getRuntime().addShutdownHook(saveClose);
		try {
			logger.addMessage(StandartStatus.INFORMATION, "Getting streams!");
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			logger.addMessage(StandartStatus.ERROR, "Error while getting streams!");
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

						if (!(o instanceof BasicProtocol)) {
							logger.addMessage(StandartStatus.PROBLEM, "Got non transfer package!");
						} else {
							BasicProtocol serverTransfer = (BasicProtocol) o;
							logger.addMessage(StandartStatus.INFORMATION, "Received Package from: " + socket + " of type " + serverTransfer);
							Object handle = serverTransfer.handle(this);
						}
					} catch (ClassNotFoundException e) {
						logger.addMessage(StandartStatus.PROBLEM, "Received unknown package!");
					}
				}
			} catch (IOException e) {
				logger.addMessage(StandartStatus.ERROR, "Cant reach Server");
			}
			logger.addMessage(StandartStatus.INFORMATION, "Stopping traffic controller for server: " + socket);
			shutdown();
		});
		trafficControl.start();
	}

	public void shutdown() {
		disconnect("Shutdown");
		Runtime.getRuntime().removeShutdownHook(saveClose);
	}

	@Override
	public void send(Transfer transfer) {
		try {
			oos.writeObject(transfer);
			logger.addMessage(StandartStatus.INFORMATION, "Sent package to " + socket + " of type " + transfer);
		} catch (IOException e) {
			logger.addMessage(StandartStatus.PROBLEM, "Failed to send Package because of " + e.getMessage());
			disconnect("Failed to send package!");
		}
	}

	public final void disconnect(String reason) {
		logger.addMessage(StandartStatus.INFORMATION, "Disconnecting client " + socket + " because of: " + reason);
		try {
			socket.close();
		} catch (IOException ex) {
			logger.addMessage(StandartStatus.PROBLEM, ex.getMessage());
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		Client client = new Client("localhost", 123);

		new Scanner(System.in).nextLine();

		client.send(new EchoRequest("Hallo das ist ein echo!"));

		new Scanner(System.in).nextLine();

		client.shutdown();

	}
}
