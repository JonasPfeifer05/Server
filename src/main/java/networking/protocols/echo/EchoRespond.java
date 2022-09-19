package networking.protocols.echo;

import networking.BasicProtocol;
import networking.Networking;
import util.Function2Args;

import java.io.Serializable;

/**
 * Created: 19.09.2022
 *
 * @author Jonas Pfeifer (jonas)
 */

public class EchoRespond extends BasicProtocol<Networking, Void> implements Serializable {
	public EchoRespond(String echo) {
		super((Function2Args<Networking, Void, Void> & Serializable) (networking, unused) -> {
			System.out.println(echo);
			return null;
		});
	}
}
