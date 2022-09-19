package networking;

import util.Function2Args;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created: 19.09.2022
 *
 * @author Jonas Pfeifer (jonas)
 */

public class AdvancedProtocol<T, U, R> extends Transfer<T, U, R>  implements Serializable {
	public AdvancedProtocol() {
		super();
	}

	public AdvancedProtocol(UUID token) {
		super(token);
	}
}
