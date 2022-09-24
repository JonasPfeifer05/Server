package server;

import java.io.Serializable;

public record LobbyData(Class<? extends Lobby> clazz, String name, int maxClients) implements Serializable {
}
