package so.glad.serializer.json.convertor.json2java;

/**
 * Convert JSON Boolean to Java Boolean or boolean or String
 * @author Palmtale
 * 2015-3-25 13:48:36
 */
public class JSON2BooleanConvertor extends AbstractJSON2JavaClassConvertor {

	@Override
	protected Object toJavaObject(Object object, Class<?> clazz) {
		if (object instanceof Boolean) {
			return object;
		}

		if (object instanceof String) {
			return Boolean.valueOf((String) object);
		}

		throw new IllegalArgumentException(toConvertExceptionMessage(object, clazz));
	}

}
