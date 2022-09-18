package networking.user;

import networking.Transfer;
import server.ClientHandler;

import java.util.function.Function;

public class UserTransfer extends Transfer<ClientHandler> {
    public UserTransfer(Function<ClientHandler, Void> handle) {
        super(handle);
    }
}
