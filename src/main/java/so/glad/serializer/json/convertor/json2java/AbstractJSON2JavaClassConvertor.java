package so.glad.serializer.json.convertor.json2java;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import so.glad.serializer.json.convertor.JSON2JavaConvertor;

import java.lang.reflect.Type;

/**
 * Convert object to specific class
 * @author Palmtale
 * 2015-3-25 13:48:36
 */
public abstract class AbstractJSON2JavaClassConvertor implements JSON2JavaConvertor {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@SuppressWarnings("unchecked")
	public final Object toJavaObject(Object object, Type type) {
		return toJavaObject(object, (Class) type);
	}

	protected abstract Object toJavaObject(Object object, Class<?> clazz);

	public static String toConvertExceptionMessage(Object object, Class<?> clazz) {
		return "Unsupport convert object [" + object + "] to an instance of class [" + clazz + "]";
	}
}
