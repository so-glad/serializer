package so.glad.serializer.json.convertor.json2java;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import so.glad.serializer.json.JSON;
import so.glad.serializer.json.ReflectUtils;
import so.glad.serializer.json.convertor.JSON2JavaConvertor;
import so.glad.serializer.json.convertor.UnsupportedTypeException;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Palmtale
 * 2015-3-25 13:48:36
 */
public class JSON2ParameterizedTypeConvertor implements JSON2JavaConvertor {

	private static final Logger log = LoggerFactory.getLogger(JSON2ParameterizedTypeConvertor.class);

	private JSON2JavaConvertor convertor;

	public JSON2ParameterizedTypeConvertor(JSON2JavaConvertor convertor) {
		super();
		this.convertor = convertor;
	}

	@SuppressWarnings("unchecked")
	public Object toJavaObject(Object object, Type type) {
		if (object instanceof List) {
			return new JSON2CollectionConvertor(convertor).toJavaObject(object, type);
		} else if (object instanceof Map) {
			return new JSON2MapConvertor().toJavaObject(object, type);
		} else {
			throw new UnsupportedTypeException(type);
		}
	}

	private static class JSON2CollectionConvertor implements JSON2JavaConvertor {

		private JSON2JavaConvertor convertor;

		public JSON2CollectionConvertor(JSON2JavaConvertor convertor) {
			super();
			this.convertor = convertor;
		}

		public Object toJavaObject(Object object, Type type) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			if (parameterizedType.getActualTypeArguments().length != 1) {
				throw new UnsupportedTypeException(type);
			}

			List<?> list = (List<?>) object;
			Class<?> clazz = (Class<?>) parameterizedType.getRawType();
			Type itemType = parameterizedType.getActualTypeArguments()[0];
			Collection<Object> collection = ReflectUtils.newCollection(clazz);
			collection.addAll(list.stream().map(item -> convertor.toJavaObject(item, itemType)).collect(Collectors.toList()));

			return collection;
		}
	}

	private static class JSON2MapConvertor implements JSON2JavaConvertor {

		@SuppressWarnings("unchecked")
		public Object toJavaObject(Object object, Type type) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
			if (actualTypeArguments.length != 2) {
				log.warn("Unsupported type [" + type + "] to convert");
				return object;
			}

			Constructor<? extends Type> constructor = null;
			Type actualType = actualTypeArguments[0];
			if (!String.class.equals(actualType)) {
				try {
					constructor = ((Class) actualType).getConstructor(String.class);
					if (constructor == null) {
						log.warn("Constructor " + actualType.getClass() + "(java.lang.String) is not found and unsupported type [" + type
								+ "] to convert");
						return object;
					}
				} catch (Exception e) {
					log.warn("Get constructor with parameter type java.lang.String error", e);
				}
			}

			Type valueType = actualTypeArguments[1];
			Class<?> clazz = (Class<?>) parameterizedType.getRawType();
			Map<Object, Object> map = (Map<Object, Object>) object;
			Map<Object, Object> newMap = newMap(clazz);
			for (Object key : map.keySet()) {
				if (constructor != null) {
					try {
						newMap.put(constructor.newInstance(key.toString()), JSON.toJavaObject(map.get(key), valueType));
					} catch (Exception e) {
						log.error("Unexpected exception", e);
						newMap.put(key, JSON.toJavaObject(map.get(key), valueType));
					}
				} else {
					newMap.put(key, JSON.toJavaObject(map.get(key), valueType));
				}
			}
			return newMap;
		}
	}
	
	@SuppressWarnings("unchecked")
	private static Map<Object, Object> newMap(Class<?> clazz) {
		Map<Object, Object> newMap;
		if (!clazz.isInstance(newMap = Maps.newLinkedHashMap())) {
			newMap = (Map) ReflectUtils.newObject(clazz);
		}
		return newMap;
	}
}
