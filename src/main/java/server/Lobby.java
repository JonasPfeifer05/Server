package server;

import networking.Transfer;
import resources.StandartStatus;
import util.Counter;
import util.LimitedMap;
import util.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.TimeoutException;

/**
 * Created: 20.09.2022
 *
 * @author Jonas Pfeifer (jonas)
 */

public class Lobby implements Runnable {
	private static final HashMap<String, Lobby> lobbies = new HashMap<>();

	protected final Logger<StandartStatus> logger;
	private final int maxClients;

	protected final ArrayList<ClientHandler> clients = new ArrayList<>();

	private final LimitedMap<UUID, Object> responses = new LimitedMap<>(100);

	private final String name;

	public Lobby(String name, Logger<StandartStatus> logger, int maxClients) {
		this.logger = logger;
		this.maxClients = maxClients;
		this.name = name;
	}

	public static Lobby join(String name, ClientHandler client) {
		if (!lobbies.containsKey(name)) {
			client.getLogger().log(StandartStatus.PROBLEM, "Client " + client + " wanted to join none existing Lobby!");
			return null;
		}

		if (!lobbies.get(name).addClient(client)) {
			return null;
		}
		lobbies.get(name).onJoin(client);

		return lobbies.get(name);
	}

	public void onJoin(ClientHandler handler) {}

	public static String create(LobbyData data, Logger<StandartStatus> logger) {

		if (lobbies.containsKey(data.name())) {
			logger.log(StandartStatus.PROBLEM, "Coudlnt create lobby because it already exists!");
			return null;
		}
		logger.log(StandartStatus.INFORMATION, "Created new lobby " + data.name());
		try {
			lobbies.put(data.name(), data.clazz().getConstructor(String.class, Logger.class, int.class).newInstance(data.name(), logger, data.maxClients()));
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}

		return data.name();
	}

	public boolean addClient(ClientHandler client) {
		if (clients.size() >= maxClients) {
			logger.log(StandartStatus.PROBLEM, "Client connection to Lobby " + this + " refused because it is full!");
			return false;
		}
		logger.log(StandartStatus.INFORMATION, "Client " + client + " joined Lobby " + this);
		clients.add(client);
		return true;
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
