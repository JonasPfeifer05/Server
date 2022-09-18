package networking.user;

import networking.server.EchoRespond;
import server.ClientHandler;

import java.io.Serializable;
import java.util.function.Function;

public class EchoRequest extends UserTransfer implements Serializable {
    public EchoRequest(String echo) {
        super((Function<ClientHandler, Void> & Serializable) clientHandler ->
        {
            clientHandler.send(new EchoRespond(echo));
            return null;
        });
    }
}
