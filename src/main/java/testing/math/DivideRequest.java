package testing.math;

import networking.BasicProtocol;
import networking.Networking;
import networking.protocols.ping.PingResponse;
import util.Function2Args;
import util.TargetFlag;

import java.io.Serializable;

/**
 * Created: 20.09.2022
 *
 * @author Jonas Pfeifer (jonas)
 */

public class DivideRequest extends BasicProtocol<Networking, Void> {
	public DivideRequest(double a, double b) {
		super(TargetFlag.CLIENT);
		this.setHandle((Function2Args<Networking, Void, Void> & Serializable) (networking, unused) -> {
			networking.send(new PingResponse(a / b, this.getToken()));
			return null;
		});
	}
}
