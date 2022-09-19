package networking;

import util.Function2Args;

import java.io.Serializable;

/**
 * Created: 19.09.2022
 *
 * @author Jonas Pfeifer (jonas)
 */

public class AdvancedProtocol<T, U, R> extends Transfer<T, U, R>  implements Serializable {
	public AdvancedProtocol(Function2Args<T, U, R> handle) {
		super(handle);
	}
}
