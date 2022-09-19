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
  super((Function2Args<Networking, Void, Void> & Serializable) (networking, unused) -> {
   networking.send(new EchoRespond(echo));
   return null;
  });
 }
}
