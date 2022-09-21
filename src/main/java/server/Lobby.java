package server;

import client.Client;
import networking.AdvancedProtocol;
import networking.BasicProtocol;
import networking.Networking;
import networking.Transfer;
import resources.StandartStatus;
import util.Counter;
import util.LimitedMap;
import util.Logger;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeoutException;

/**
 * Created: 20.09.2022
 *
 * @author Jonas Pfeifer (jonas)
 */

public class Lobby implements Runnable {
	private static final HashMap<String, Lobby> lobbies = new HashMap<>();

	private final Logger<StandartStatus> logger;
	private final int maxClients;

	private final ArrayList<ClientHandler> clients = new ArrayList<>();

	private final LimitedMap<UUID, Object> responses = new LimitedMap<>(100);

	private final String name;

	public Lobby(String name, Logger<StandartStatus> logger, int maxClients) {
		this.logger = logger;
		this.maxClients = maxClients;
		this.name = name;
	}

	public static Lobby join(String name, ClientHandler client) {
		if (!lobbies.containsKey(name)) create(name, 10, client.getLogger());

		lobbies.get(name).addClient(client);

		return lobbies.get(name);
	}

	public static void create(String name, int maxClients, Logger<StandartStatus> logger) {

		if (lobbies.containsKey(name)) {
			logger.log(StandartStatus.PROBLEM, "Coudlnt create lobby because it already exists!");
			return;
		}
		logger.log(StandartStatus.INFORMATION, "Created new lobby " + name);
		lobbies.put(name, new Lobby(name, logger, maxClients));
	}

	public void addClient(ClientHandler client) {
		if (clients.size() >= maxClients) {
			logger.log(StandartStatus.PROBLEM, "Client connection to Lobby " + this + " refused because it is full!");
			return;
		}
		logger.log(StandartStatus.INFORMATION, "Client " + client + " joined Lobby " + this);
		clients.add(client);
	}

	@Override
	public void run() {
		setup();
	}

	private void setup() {
		
	}

	public void forward(Transfer protocol, ClientHandler from) {
		logger.log(StandartStatus.INFORMATION, "Lobby got Package forwarted of type " + protocol.getClass().getSimpleName() + " with UUID " + protocol.getToken());
		new Thread(() -> {
			responses.add(protocol.getToken(), protocol.handle(from, this));
		}).start();
	}

	public void send(Transfer transfer, ClientHandler client) {
		logger.log(StandartStatus.INFORMATION, "Sending package from Lobby to client " + client);
		client.send(transfer);
	}

	public void sendToAll(Transfer transfer) {
		sendToAll(new ArrayList<>(), transfer);
	}

	public void sendToAll(ArrayList<ClientHandler> except, Transfer transfer) {
		for (ClientHandler client : clients) {
			if (!except.contains(client)){
				client.send(transfer);
			}
		}
	}

	public void kick(ClientHandler client) {
		kick(client, "Kicked out of lobby");
	}

	public void kick(ClientHandler client, String reason) {
		client.disconnect(reason);
	}

	public void remove(ClientHandler client) {
		logger.log(StandartStatus.INFORMATION, "Removed client " + client + " from lobby " + this);
		this.clients.remove(client);
	}

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

	@Override
	public String toString() {
		return name;
	}
}
