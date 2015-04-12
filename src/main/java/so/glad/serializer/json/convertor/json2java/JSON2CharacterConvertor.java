package so.glad.serializer.json.convertor.json2java;

/**
 * @author Palmtale
 * 2015-3-25 13:48:36
 */
public class JSON2CharacterConvertor extends AbstractJSON2JavaClassConvertor {

	@Override
	protected Object toJavaObject(Object object, Class<?> clazz) {
		if (clazz.isInstance(object)) {
			return object;
		}

		if (object instanceof String) {
			String text = (String) object;
			if (text.length() != 1) {
				throw new IllegalArgumentException("Illegal string value [" + text + "] for a character");
			}

			return text.toCharArray()[0];
		}

		throw new IllegalArgumentException(toConvertExceptionMessage(object, clazz));
	}

}
