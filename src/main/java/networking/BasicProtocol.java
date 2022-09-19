package networking;

import util.Function2Args;

import java.io.Serializable;

/**
 * Created: 19.09.2022
 *
 * @author Jonas Pfeifer (jonas)
 */

public class BasicProtocol<T, R> extends Transfer<T, Void, R> implements Serializable {
	public BasicProtocol(Function2Args<T, Void, R> handle) {
		super(handle);
	}
}
