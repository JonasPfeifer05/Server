package networking.protocols.lobby;

import client.Client;
import networking.BasicProtocol;
import networking.protocols.echo.EchoResponse;
import util.BasicTypes;
import util.Function2Args;
import util.TargetFlag;

import java.io.Serializable;

/**
 * Created: 20.09.2022
 *
 * @author Jonas Pfeifer (jonas)
 */

public class LobbyRequest extends BasicProtocol<Client, Void> implements Serializable {
	public LobbyRequest() {
		super(TargetFlag.CLIENT);
		this.setHandle((Function2Args<Client, Void, Void> & Serializable) (client, unused) -> {

			return null;
		});
	}
}
