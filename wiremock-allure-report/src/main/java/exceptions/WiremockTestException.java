package exceptions;

public class WiremockTestException extends RuntimeException {
	public WiremockTestException(String message, Throwable cause) {
		super(message, cause);
	}

}
