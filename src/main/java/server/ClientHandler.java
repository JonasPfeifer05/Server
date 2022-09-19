package server;

import networking.BasicProtocol;
import networking.Transfer;
import networking.Networking;
import networking.protocols.ping.PingRequest;
import networking.protocols.ping.PingResponse;
import resources.StandartStatus;
import util.Counter;
import util.LimitedMap;
import util.Logger;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * Created: 17.09.2022
 *
 * @author Jonas Pfeifer (jonas)
 */

public class ClientHandler implements Runnable, Networking {
    public static final ArrayList<ClientHandler> clients = new ArrayList<>();

    private final Socket socket;
    private final Logger<StandartStatus> logger;

    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    private final LimitedMap<UUID, Object> responses = new LimitedMap<>(10);

    public ClientHandler(Socket socket, Logger<StandartStatus> logger) {
        this.socket = socket;
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
            logger.log(StandartStatus.INFORMATION, "Getting streams for client: " + socket);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());

            clients.add(this);
        } catch (IOException e) {
            disconnect("Couldn´t get streams!");
        }
    }

    private void authorizeUser() {
        int saveNumber = (int) (Math.random() * Integer.MAX_VALUE * 2 - Integer.MAX_VALUE);

        try {
            objectOutputStream.writeObject(new PingRequest(saveNumber));
            socket.setSoTimeout(5 * 1000);

            logger.log(StandartStatus.INFORMATION, "Waiting for authorization");
            Object respond = this.getObjectInputStream().readObject();

            if (!(respond instanceof PingResponse)) {
                disconnect("Failed authorization because of invalid respond Package");
            } else {

                socket.setSoTimeout(0);
                if (((PingResponse) respond).handle(this) != saveNumber)
                    disconnect("Failed authorization: invalid save number");
                logger.log(StandartStatus.INFORMATION, "Authorized user: " + socket);

            }
        } catch (IOException | ClassNotFoundException e) {
            disconnect("Failed authorization: " + e);
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

    public static void disconnectAll(String reason) {
        while (clients.size() > 0) {
            clients.get(0).disconnect(reason);
        }
    }

    public final void disconnect(String reason) {
        logger.log(StandartStatus.PROBLEM, "Disconnecting client " + socket + " because of: " + reason);
        clients.remove(this);
        try {
            socket.close();
        } catch (IOException ex) {
            logger.log(StandartStatus.PROBLEM, ex.getMessage());
        }
    }

    @Override
    public void send(Transfer transfer) {
        try {
            objectOutputStream.writeObject(transfer);
            logger.log(StandartStatus.INFORMATION, "Sent package to: " + socket + " of type " + transfer.getClass().getSimpleName() + " with UUID " + transfer.getToken());
        } catch (IOException e) {
            disconnect("Failed to send package!");
        }
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

    public final Socket getSocket() {

        return socket;
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
