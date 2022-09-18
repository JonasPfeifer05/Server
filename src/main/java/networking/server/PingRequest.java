package networking.server;

import client.Client;
import networking.user.PingRespond;
import server.ClientHandler;

import java.io.Serializable;
import java.util.function.Function;

public class PingRequest extends ServerTransfer implements Serializable {
    public final int number;

    public PingRequest(int number) {
        super((Function<Client, Void> & Serializable) client ->
        {
            client.send(new PingRespond(number));
            return null;
        });
        this.number = number;
    }
}
