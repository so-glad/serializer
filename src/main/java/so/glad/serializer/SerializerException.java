package so.glad.serializer;

/**
 * The exception in serializer
 * @author Palmtale
 * @since 2015-3-25 at 16:27:27
 */
public class SerializerException extends RuntimeException {

	private static final long serialVersionUID = 5626054944940569050L;

	public SerializerException() {
		super();
	}

	public SerializerException(String message, Throwable cause) {
		super(message, cause);
	}

	public SerializerException(String message) {
		super(message);
	}

	public SerializerException(Throwable cause) {
		super(cause);
	}

}
