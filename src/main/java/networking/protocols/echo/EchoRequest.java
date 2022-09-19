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

public class EchoRequest extends BasicProtocol<Networking, Void> implements Serializable {
    public EchoRequest(String echo) {
        this.setHandle((Function2Args<Networking, Void, Void> & Serializable) (networking, unused) -> {
            networking.send(new EchoResponse(echo, this.getToken()));
            return null;
        });
    }
}
