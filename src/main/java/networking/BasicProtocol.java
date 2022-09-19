package networking;

import util.Function2Args;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created: 19.09.2022
 *
 * @author Jonas Pfeifer (jonas)
 */

public class BasicProtocol<T, R> extends Transfer<T, Void, R> implements Serializable {

	public BasicProtocol() {
		super();
	}

	public BasicProtocol(UUID token) {
		super(token);
	}
}
