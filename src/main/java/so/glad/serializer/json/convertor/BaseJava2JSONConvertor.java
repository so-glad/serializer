package so.glad.serializer.json.convertor;

import com.google.common.collect.Maps;
import so.glad.serializer.json.convertor.java2json.*;

import java.util.*;

/**
 * @author Palmtale
 * 2015-3-25 13:48:36
 */
public class BaseJava2JSONConvertor extends ConvertorSupport implements Java2JSONConvertor {

	private final Map<Class<?>, Java2JSONConvertor> customConvertors = Maps.newHashMap();

	public final void registerConvertor(Class<?> clazz, Java2JSONConvertor convertor) {
		if (DEFAULT_BASIC_CONVERTORS.containsKey(clazz)) {
			throw new IllegalStateException("The convertor for class [" + clazz + "] is exists");
		}
		if (customConvertors.containsKey(clazz)) {
			logger.warn("Replace convertor from [" + customConvertors.get(clazz) + "] to [" + convertor + "] for class [" + clazz
					+ "]");
		}

		customConvertors.put(clazz, convertor);
	}

	@SuppressWarnings("unchecked")
	public Object toJSONObject(Object object) {
		if (object == null) {
			return null;
		}

		Java2JSONConvertor convertor = findConvertor(customConvertors, object.getClass(), true);
		if (convertor == null) {
			convertor = findConvertor(DEFAULT_BASIC_CONVERTORS, object.getClass(), false);
		}

		if (convertor == null) {
			if (object instanceof Collection) {
				convertor = new Collection2JSONConvertor(this);
			} else if (object.getClass().isArray()) {
				convertor = new Array2JSONConvertor(this);
			} else if (object instanceof Map) {
				convertor = new Map2JSONConvertor(this);
			}
		}

		if (convertor == null) {
			convertor = new JavaBean2JSONConvertor(this);
		}

		return convertor.toJSONObject(object);
	}

	private static final Map<Class<?>, Java2JSONConvertor> DEFAULT_BASIC_CONVERTORS = Maps.newHashMap();

	static {
		DEFAULT_BASIC_CONVERTORS.put(String.class, new NonJava2JSONConvertor());
		DEFAULT_BASIC_CONVERTORS.put(Boolean.class, new NonJava2JSONConvertor());
		DEFAULT_BASIC_CONVERTORS.put(Number.class, new Number2JSONConvertor());
		DEFAULT_BASIC_CONVERTORS.put(Enum.class, new Enumeration2JSONConvertor());
		DEFAULT_BASIC_CONVERTORS.put(Date.class, new Date2JSONConvertor());
		DEFAULT_BASIC_CONVERTORS.put(Calendar.class, new Date2JSONConvertor());
		DEFAULT_BASIC_CONVERTORS.put(Character.class, new Character2JSONConvertor());
	}

}
