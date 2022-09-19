package networking.protocols.ping;

import networking.BasicProtocol;
import networking.Networking;
import util.Function2Args;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created: 19.09.2022
 *
 * @author Jonas Pfeifer (jonas)
 */

public class PingResponse extends BasicProtocol<Networking, Integer> implements Serializable {

	public PingResponse(int ping, UUID token) {
		super(token);
		this.setHandle((Function2Args<Networking, Void, Integer> & Serializable) (networking, unused) -> {
			return ping;
		});
	}
}
