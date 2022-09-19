package client;

import networking.BasicProtocol;
import networking.Transfer;
import networking.Networking;
import networking.protocols.SleepRequest;
import networking.protocols.math.AddRequest;
import networking.protocols.echo.EchoRequest;
import networking.protocols.math.MultiplyRequest;
import networking.protocols.ping.PingRequest;
import resources.StandartStatus;
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

    private final Client thisThread;

    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    private final LimitedMap<UUID, Object> responses = new LimitedMap<>(10);

    private final Logger<StandartStatus> logger = new Logger<>(true);

    private final Socket socket;

    private final Thread saveClose = new Thread(() -> shutdown());

    public Client(String host, int port) throws IOException {
        socket = new Socket(host, port);
        start();
        thisThread = this;
    }

    public Client(Inet4Address address, int port) throws IOException {
        socket = new Socket(address, port);
        start();
        thisThread = this;
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
                            logger.addMessage(StandartStatus.INFORMATION, "Received Package from: " + socket + " of type " + serverTransfer + " with UUID " + serverTransfer.getToken());
                            new Thread(() -> {
                                responses.add(serverTransfer.getToken(), serverTransfer.handle(this));
                            }).start();
                        }
                    } catch (ClassNotFoundException e) {
                        logger.addMessage(StandartStatus.PROBLEM, "Received unknown package!");
                    }
                }
            } catch (IOException e) {
                logger.addMessage(StandartStatus.ERROR, "Cant reach Server " + e.getMessage());
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
            logger.addMessage(StandartStatus.INFORMATION, "Sent package to " + socket + " of type " + transfer + " with UUID " + transfer.getToken());
        } catch (IOException e) {
            logger.addMessage(StandartStatus.PROBLEM, "Failed to send Package because of " + e.getMessage());
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

        Transfer echoRequest = new EchoRequest("Hallo das ist ein echo!");
        client.send(echoRequest);
        try {
            System.out.println(client.await(echoRequest.getToken(), 2));
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        new Scanner(System.in).nextLine();

        Transfer sleepRequest = new SleepRequest();
        client.send(sleepRequest);

        Thread.sleep(3 * 1000);
        client.send(new EchoRequest("Hallo"));

        try {
            System.out.println(client.await(sleepRequest.getToken(), 20));
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        new Scanner(System.in).nextLine();

        client.shutdown();

    }
}
