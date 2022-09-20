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

public class AdvancedProtocol<T, U, R> extends Transfer<T, U, R>  implements Serializable {
	public AdvancedProtocol(TargetFlag target) {
		super(target);
	}

	public AdvancedProtocol(UUID token, TargetFlag target) {
		super(token, target);
	}
}
