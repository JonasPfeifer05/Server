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

public class BasicProtocol<T, R> extends Transfer<T, Void, R> implements Serializable {

	public BasicProtocol(TargetFlag target) {
		super(target);
	}

	public R handle(T t) {
		return super.handle(t, null);
	}

	public BasicProtocol(UUID token, TargetFlag target) {
		super(token, target);
	}
}
