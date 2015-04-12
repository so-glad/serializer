package so.glad.serializer;

/**
 * 
 * @author Norther
 * 
 */
public class MarshalException extends SerializerException {

	private static final long serialVersionUID = -2436434225763539572L;

	public MarshalException() {
		super();
	}

	public MarshalException(String message, Throwable cause) {
		super(message, cause);
	}

	public MarshalException(String message) {
		super(message);
	}

	public MarshalException(Throwable cause) {
		super(cause);
	}

}
