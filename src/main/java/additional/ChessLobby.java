package additional;

import resources.StandartStatus;
import server.ClientHandler;
import server.Lobby;
import util.Logger;

public class ChessLobby extends Lobby {
    public ChessLobby(String name, Logger<StandartStatus> logger, int maxClients) {
        super(name, logger, maxClients);
    }

    @Override
    public void onJoin(ClientHandler handler) {
        logger.log(StandartStatus.INFORMATION, "Hello " + handler + " in the chess lobby + " + this);
    }
}
