package networking;

import client.Client;

import java.io.Serializable;
import java.util.function.Function;

public class Transfer<T> implements Serializable{
    public final Function<T, Void> handle;


    public Transfer(Function<T, Void> handle) {
        this.handle = handle;
    }

    public void handle(T side) {
        handle.apply(side);
    }
}
