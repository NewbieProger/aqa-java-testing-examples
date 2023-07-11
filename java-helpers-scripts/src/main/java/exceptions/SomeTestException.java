package exceptions;

public class SomeTestException extends RuntimeException {

	public SomeTestException(String message, Throwable cause) {
		super(message, cause);
	}

	public SomeTestException(String message) {
		super(message);
	}

}
