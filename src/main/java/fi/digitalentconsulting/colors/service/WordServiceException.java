package fi.digitalentconsulting.colors.service;

public class WordServiceException extends RuntimeException {

	public WordServiceException() {
	}

	public WordServiceException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public WordServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public WordServiceException(String message) {
		super(message);
	}

	public WordServiceException(Throwable cause) {
		super(cause);
	}

}
