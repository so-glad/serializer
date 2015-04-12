package so.glad.serializer.json.convertor;

import java.lang.reflect.Type;

/**
 * @author Palmtale
 * 2015-3-25 13:48:36
 */
public class UnsupportedTypeException extends RuntimeException {

	private static final long serialVersionUID = 709970095020851188L;

	public UnsupportedTypeException(Type type) {
		super("Unsupported type [" + type + "]");
	}
}
