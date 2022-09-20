package networking.protocols.ping;

import networking.BasicProtocol;
import networking.Networking;
import util.Function2Args;
import util.TargetFlag;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created: 19.09.2022
 *
 * @author Jonas Pfeifer (jonas)
 */

public class PingResponse extends BasicProtocol<Networking, Double> implements Serializable {

	public PingResponse(double ping, UUID token) {
		super(token, TargetFlag.CLIENT);
		this.setHandle((Function2Args<Networking, Void, Double> & Serializable) (networking, unused) -> {
			return ping;
		});
	}
}
