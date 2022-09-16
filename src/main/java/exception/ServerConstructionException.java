package exception;

/**
 * Created: 16.09.2022
 *
 * @author Jonas Pfeifer (jonas)
 */

public class ServerConstructionException extends Exception{
	public ServerConstructionException() {
	}

	public ServerConstructionException(String message) {
		super(message);
	}
}
