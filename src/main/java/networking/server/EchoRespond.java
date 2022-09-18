package networking.server;

import client.Client;

import java.io.Serializable;
import java.util.function.Function;

public class EchoRespond extends ServerTransfer implements Serializable {
    public EchoRespond(String echo) {
        super((Function<Client, Void> & Serializable) client ->
        {
            System.out.println(echo);
            return null;
        });
    }
}
