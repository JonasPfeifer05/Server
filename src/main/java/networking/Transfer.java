package networking;

import util.Function2Args;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created: 19.09.2022
 *
 * @author Jonas Pfeifer (jonas)
 */

public abstract class Transfer<T, U, R> implements Serializable {
	private Function2Args<T, U, R> handle;

	private UUID token;

	public Transfer() {
		this(UUID.randomUUID());
	}

	public Transfer(UUID token) {
		this.setToken(token);
	}

	public R handle(T t) {
		return handle.apply(t, null);
	}

	public void setHandle(Function2Args<T, U, R> handle) {
		this.handle = handle;
	}

	public void setToken(UUID token) {
		this.token = token;
	}

	public UUID getToken() {
		return token;
	}
}