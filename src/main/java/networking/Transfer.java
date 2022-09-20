package networking;

import util.Function2Args;
import util.TargetFlag;

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
	public final TargetFlag target;

	public Transfer(TargetFlag target) {
		this(UUID.randomUUID(), target);
	}

	public Transfer(UUID token, TargetFlag target) {
		this.setToken(token);
		this.target = target;
	}

	public R handle(T t, U u) {
		return handle.apply(t, u);
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