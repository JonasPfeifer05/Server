package networking.protocols.lobby;

import networking.BasicProtocol;
import server.ClientHandler;
import server.Lobby;
import server.LobbyData;
import util.Function2Args;
import util.TargetFlag;

import java.io.Serializable;

public class LobbyCreationResponse extends BasicProtocol<ClientHandler, LobbyData> implements Serializable {
    public LobbyCreationResponse(Class<? extends Lobby> clazz, String name, int maxClients) {
        super(TargetFlag.CLIENT);
        this.setHandle((Function2Args<ClientHandler, Void, LobbyData> & Serializable) (clientHandler, unused) -> {
            return new LobbyData(clazz, name, maxClients);
        });
    }
}
