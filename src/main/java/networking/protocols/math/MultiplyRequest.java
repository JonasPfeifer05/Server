package networking.protocols.math;

import networking.BasicProtocol;
import networking.Networking;
import networking.Transfer;
import networking.protocols.echo.EchoRespond;
import util.Function2Args;

import java.io.Serializable;

/**
 * Created: 19.09.2022
 *
 * @author Jonas Pfeifer (jonas)
 */

public class MultiplyRequest extends BasicProtocol<Networking, Void> implements Serializable {
	public MultiplyRequest(int a, int b) {
		super((Function2Args<Networking, Void, Void> & Serializable) (networking, unused) -> {
			networking.send(new EchoRespond("" + (a * b)));
			return null;
		});
	}
}
