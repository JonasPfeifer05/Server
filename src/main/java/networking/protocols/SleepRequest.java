package networking.protocols;

import networking.BasicProtocol;
import networking.Networking;
import networking.protocols.echo.EchoRequest;
import networking.protocols.echo.EchoResponse;
import util.Function2Args;

import java.io.Serializable;

public class SleepRequest extends BasicProtocol<Networking, Void> implements Serializable {
    public SleepRequest() {
        this.setHandle((Function2Args<Networking, Void, Void> & Serializable) (networking, unused) -> {
            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            networking.send(new EchoResponse("Finished Sleeping", this.getToken()));
            return null;
        });
    }
}
