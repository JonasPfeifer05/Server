package networking;

import java.io.Serializable;

public interface Networking extends Serializable {
    public void send(Transfer transfer);
    public void startReceiving();
}
