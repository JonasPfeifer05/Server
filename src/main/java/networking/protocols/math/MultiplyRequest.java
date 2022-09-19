package networking.protocols.math;

import networking.BasicProtocol;
import networking.Networking;
import networking.protocols.echo.EchoResponse;
import util.Function2Args;

import java.io.Serializable;

/**
 * Created: 19.09.2022
 *
 * @author Jonas Pfeifer (jonas)
 */

public class MultiplyRequest extends BasicProtocol<Networking, Void> implements Serializable {
	public MultiplyRequest(int a, int b) {
		this.setHandle((Function2Args<Networking, Void, Void> & Serializable) (networking, unused) -> {
			networking.send(new EchoResponse("" + (a * b), this.getToken()));
			return null;
		});
	}
}
