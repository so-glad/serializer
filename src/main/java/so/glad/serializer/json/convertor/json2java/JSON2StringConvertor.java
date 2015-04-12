package so.glad.serializer.json.convertor.json2java;

/**
 * @author Palmtale
 * 2015-3-25 13:48:36
 */
public class JSON2StringConvertor extends AbstractJSON2JavaClassConvertor {

	@Override
	protected Object toJavaObject(Object object, Class<?> clazz) {
		if (clazz.isInstance(object)) {
			return object;
		}

		return object.toString();
	}

}
