package networking;

import resources.StandartStatus;
import util.Logger;

import java.io.IOException;

public interface Transferring {
    public void send(Transfer transfer);
    public void startReceiving();
}
