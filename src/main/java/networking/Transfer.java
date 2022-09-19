package networking;

import util.Function2Args;

import java.io.Serializable;

/**
 * Created: 19.09.2022
 *
 * @author Jonas Pfeifer (jonas)
 */

public abstract class Transfer<T, U, R> implements Serializable {
	private final Function2Args<T, U, R> handle;

	public Transfer(Function2Args<T, U, R> handle) {
		this.handle = handle;
	}

	public R handle(T t) {
		return handle.apply(t, null);
	}
}