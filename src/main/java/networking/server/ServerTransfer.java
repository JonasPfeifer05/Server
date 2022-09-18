package networking.server;

import client.Client;
import networking.Transfer;
import server.ClientHandler;

import java.io.Serializable;
import java.util.function.Function;

public class ServerTransfer extends Transfer<Client> implements Serializable{

    public ServerTransfer(Function<Client, Void> handle) {
        super(handle);
    }
}
