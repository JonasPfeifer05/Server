package client;

import exception.ConstructionException;
import networking.BasicProtocol;
import networking.Transfer;
import networking.Networking;
import networking.protocols.echo.EchoResponse;
import networking.protocols.lobby.LeaveRequest;
import networking.protocols.lobby.LobbyCreationResponse;
import resources.StandartStatus;
import testing.ChessLobby;
import util.BasicTypes;
import util.Counter;
import util.LimitedMap;
import util.Logger;

import java.io.*;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * Created: 16.09.2022
 *
 * @author Jonas Pfeifer (jonas)
 */

public class Client implements Networking {
	private ObjectInputStream objectInputStream;
	private ObjectOutputStream objectOutputStream;
	private final LimitedMap<UUID, Object> responses = new LimitedMap<>(10);
	private final Logger<StandartStatus> logger = new Logger<>(true);
	private final Socket socket;
	private final Thread saveClose = new Thread(this::shutdown);

	public Client(String host, int port) throws ConstructionException {
		try {
			socket = new Socket(Inet4Address.getByName(host), port);
		} catch (IOException e) {
			throw new ConstructionException();
		}
		setUp();
	}

	private void setUp() {
		getStreams();

		startReceiving();
	}

	private void getStreams() {
		Runtime.getRuntime().addShutdownHook(saveClose);
		try {
			logger.log(StandartStatus.INFORMATION, "Getting streams!");
			objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
			objectInputStream = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			disconnect("Error while getting the streams");
		}
	}

	public void startReceiving() {
		logger.log(StandartStatus.INFORMATION, "Starting network listener for user" + socket);
		Thread listener = new Thread(() ->
		{
			try {
				while (true) {
					try {
						Object o = objectInputStream.readObject();

						if (!(o instanceof BasicProtocol)) {
							logger.log(StandartStatus.PROBLEM, "Got non transfer package!");
						} else {
							BasicProtocol protocol = (BasicProtocol) o;
							logger.log(StandartStatus.INFORMATION, "Received Package from " + protocol + " of type " + protocol.getClass().getSimpleName() + " with UUID " + protocol.getToken());
							new Thread(() -> {
								responses.add(protocol.getToken(), protocol.handle(this));
							}).start();
						}

					} catch (ClassNotFoundException e) {
						logger.log(StandartStatus.PROBLEM, "Received unknown package!");
					}
				}
			} catch (IOException e) {
				logger.log(StandartStatus.ERROR, "Cant reach socket " + socket);
			}
			disconnect("Cant reach socket " + socket);
			logger.log(StandartStatus.INFORMATION, "Not accepting anymore packages from socket " + socket);
		});
		listener.start();
	}

	public void shutdown() {
		disconnect("Shutdown");
		Runtime.getRuntime().removeShutdownHook(saveClose);
	}

	@Override
	public void send(Transfer transfer) {
		try {
			objectOutputStream.writeObject(transfer);
			logger.log(StandartStatus.INFORMATION, "Sent package to " + socket + " of type " + transfer.getClass().getSimpleName() + " with UUID " + transfer.getToken());
		} catch (IOException e) {
			disconnect("Failed to send package!");
		}
	}

	@Override
	public Object request(Enum type, String additional) {
		Object ret = null;

		ret = getBasic((BasicTypes) type, additional);

		//Add additional testing

		return ret;
	}

	public Object getBasic(BasicTypes type, String additional) {
		Object o = null;

		Scanner scanner = new Scanner(System.in);

		try {
			switch (type) {
				case STRING -> {
					System.out.print(additional);
					o = scanner.nextLine();
				}
				case CHAR -> {
					System.out.print(additional);
					o = scanner.next().charAt(0);
				}
				case INT -> {
					System.out.print(additional);
					o = scanner.nextInt();
				}
				case LONG -> {
					System.out.print(additional);
					o = scanner.nextLong();
				}
				case DOUBLE -> {
					System.out.print(additional);
					o = scanner.nextDouble();
				}
				case BOOLEAN -> {
					System.out.print(additional);
					o = scanner.nextBoolean();
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return o;
	}

	@Override
	public Object await(UUID token, int secTimeout) throws TimeoutException {
		final Counter counter = new Counter(secTimeout);

		java.util.Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				counter.dec();
			}
		}, 0, 1000);

		while (!responses.contains(token)) {
			if (counter.getCurrent() <= 0) throw new TimeoutException("Timed out while waiting for package!");
		}

		return responses.pop(token);
	}

	public final void disconnect(String reason) {
		logger.log(StandartStatus.PROBLEM, "Disconnecting client " + socket + " because of: " + reason);
		try {
			socket.close();
		} catch (IOException ex) {
			logger.log(StandartStatus.PROBLEM, ex.getMessage());
		}
	}

	@Override
	public String toString() {
		return socket.toString();
	}

	public static void main(String[] args) throws ConstructionException, InterruptedException {
		Client client = new Client("localhost", 123);

		Thread.sleep(1000);

		client.send(new EchoResponse("asd", null));
	}
}
