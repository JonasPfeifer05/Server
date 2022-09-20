package networking.protocols.echo;

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

public class EchoResponse extends BasicProtocol<Networking, String> implements Serializable {
	public EchoResponse(String echo, UUID token) {
		super(token, TargetFlag.CLIENT);
		this.setHandle((Function2Args<Networking, Void, String> & Serializable) (networking, unused) -> {
			return echo;
		});
	}
}
