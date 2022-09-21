package networking.protocols.lobby;

import networking.AdvancedProtocol;
import server.ClientHandler;
import server.Lobby;
import util.Function2Args;
import util.TargetFlag;

import java.io.Serializable;

/**
 * Created: 21.09.2022
 *
 * @author Jonas Pfeifer (jonas)
 */

public class LeaveRequest extends AdvancedProtocol<ClientHandler, Lobby, Void> implements Serializable {
	public LeaveRequest() {
		super(TargetFlag.LOBBY);
		this.setHandle((Function2Args<ClientHandler, Lobby, Void> & Serializable) (clientHandler, lobby) -> {
			lobby.kick(clientHandler, "Leaving the lobby");

			return null;
		});
	}
}
