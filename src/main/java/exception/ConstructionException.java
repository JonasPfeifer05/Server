package exception;

/**
 * Created: 16.09.2022
 *
 * @author Jonas Pfeifer (jonas)
 */

public class ConstructionException extends Exception{
	public ConstructionException() {
	}

	public ConstructionException(String message) {
		super(message);
	}
}
