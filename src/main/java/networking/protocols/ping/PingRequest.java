package networking.protocols.ping;

import networking.BasicProtocol;
import networking.Networking;
import util.Function2Args;

import java.io.Serializable;

/**
 * Created: 19.09.2022
 *
 * @author Jonas Pfeifer (jonas)
 */

public class PingRequest extends BasicProtocol<Networking, Void> implements Serializable {

	public PingRequest(int ping) {
		super((Function2Args<Networking, Void, Void> & Serializable) (networking, unused) -> {
			networking.send(new PingRespond(ping));
			return null;
		});
	}
}
