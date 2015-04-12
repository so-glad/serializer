package so.glad.serializer.json.convertor.json2java;

import so.glad.serializer.json.ReflectUtils;

/**
 *
 * @author Palmtale
 * 2015-3-25 13:48:36
 */
public class JSON2EnumConvertor extends AbstractJSON2JavaClassConvertor {

	@SuppressWarnings("unchecked")
	@Override
	protected Object toJavaObject(Object object, Class clazz) {
		if (clazz.isInstance(object)) {
			return object;
		}

		if (object instanceof String) {
			if (ReflectUtils.getSuperClasses(clazz).contains(Enum.class)) {
				return Enum.valueOf(clazz, (String) object);
			}
		}

		throw new IllegalArgumentException(toConvertExceptionMessage(object, clazz));
	}

}
