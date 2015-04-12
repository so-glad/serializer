package so.glad.serializer.json.convertor;

import com.google.common.collect.Maps;
import so.glad.serializer.json.convertor.json2java.*;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.*;

/**
 * @author Palmtale
 * 2015-3-25 13:48:36
 */
public class BaseJSON2JavaConvertor extends ConvertorSupport implements JSON2JavaConvertor {

	private final Map<Class<?>, JSON2JavaConvertor> customConvertors = Maps.newHashMap();

	public final void registerConvertor(Class<?> clazz, JSON2JavaConvertor convertor) {
		if (DEFAULT_CONVERTORS.containsKey(clazz)) {
			throw new IllegalStateException("The convertor for class [" + clazz + "] is exists");
		}
		if (customConvertors.containsKey(clazz)) {
			logger.warn("Replace convertor from [" + customConvertors.get(clazz) + "] to [" + convertor + "] for class [" + clazz
					+ "]");
		}

		customConvertors.put(clazz, convertor);
	}

	@SuppressWarnings("unchecked")
	public Object toJavaObject(Object object, Type type) {
		if (object == null) {
			return null;
		}

		if (Object.class.equals(type)) {
			return object;
		}

		if (type instanceof ParameterizedType) {
			return new JSON2ParameterizedTypeConvertor(this).toJavaObject(object, type);
		} else if (type instanceof GenericArrayType) {
			return new JSON2JavaGenericArrayConvertor(this).toJavaObject(object, type);
		} else if (type instanceof Class) {
			Class clazz = (Class) type;
			if (clazz.isArray()) {
				return new JSON2JavaArrayConvertor(this).toJavaObject(object, clazz.getComponentType());
			}

			JSON2JavaConvertor convertor = findConvertor(customConvertors, clazz, true);
			if (convertor == null) {
				convertor = findConvertor(DEFAULT_CONVERTORS, clazz, false);
			}

			if (convertor == null) {
				convertor = new JSON2JavaBeanConvertor();
			}

			return convertor.toJavaObject(object, clazz);
		}

		return object;
	}

	public static final Set<Class<?>> BASIC_CLASSES;
	public static final Map<Class<?>, JSON2JavaConvertor> DEFAULT_CONVERTORS;

	static {
		Map<Class<?>, JSON2JavaConvertor> convertors = Maps.newHashMap();
		convertors.put(Boolean.class, new JSON2BooleanConvertor());
		convertors.put(boolean.class, new JSON2BooleanConvertor());

		convertors.put(Character.class, new JSON2CharacterConvertor());
		convertors.put(char.class, new JSON2CharacterConvertor());

		convertors.put(String.class, new JSON2StringConvertor());

		convertors.put(Date.class, new JSON2DateConvertor());
		convertors.put(Calendar.class, new JSON2DateConvertor());

		convertors.put(Enum.class, new JSON2EnumConvertor());

		convertors.put(byte.class, new JSON2NumberConvertor());
		convertors.put(long.class, new JSON2NumberConvertor());
		convertors.put(float.class, new JSON2NumberConvertor());
		convertors.put(double.class, new JSON2NumberConvertor());
		convertors.put(int.class, new JSON2NumberConvertor());
		convertors.put(short.class, new JSON2NumberConvertor());
		convertors.put(Number.class, new JSON2NumberConvertor());
		convertors.put(BigInteger.class, new JSON2NumberConvertor());

		convertors.put(Collection.class, new JSON2CollectionConvertor());
		convertors.put(Map.class, new JSON2JavaBeanConvertor());

		DEFAULT_CONVERTORS = Collections.unmodifiableMap(convertors);
		BASIC_CLASSES = Collections.unmodifiableSet(DEFAULT_CONVERTORS.keySet());
	}
}
