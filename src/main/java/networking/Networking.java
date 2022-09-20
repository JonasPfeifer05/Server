package networking;

import util.BasicTypes;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public interface Networking extends Serializable {
    void send(Transfer transfer);

    Object request(Enum type, String additional);
    Object await(UUID token, int secTimeout) throws TimeoutException;
}
