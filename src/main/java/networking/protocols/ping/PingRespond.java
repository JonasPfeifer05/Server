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

public class PingRespond extends BasicProtocol<Networking, Integer> implements Serializable {

	public PingRespond(int ping) {
		super((Function2Args<Networking, Void, Integer> & Serializable) (networking, unused) -> {
			return ping;
		});
	}
}
