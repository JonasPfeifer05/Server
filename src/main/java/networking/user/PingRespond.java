package networking.user;

import client.Client;
import server.ClientHandler;

import java.io.Serializable;
import java.util.function.Function;

public class PingRespond extends UserTransfer implements Serializable {
    public final int number;

    public PingRespond(int number) {
        super((Function<ClientHandler, Void> & Serializable)  ClientHandler ->
        {
            return null;
        });
        this.number = number;
    }
}
