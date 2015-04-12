package so.glad.serializer.json.convertor.json2java;

import so.glad.serializer.json.ReflectUtils;
import so.glad.serializer.json.convertor.BaseJSON2JavaConvertor;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Convert JSON Boolean to ArrayList Interface and Super class, or other
 * Instantiation Collection
 * @author Palmtale
 * 2015-3-25 13:48:36
 */
public class JSON2CollectionConvertor extends AbstractJSON2JavaClassConvertor {

	@Override
	protected Object toJavaObject(Object object, Class<?> clazz) {
		if (clazz.isInstance(object)) {
			log.warn("Assume expected [" + clazz.getName() + "] is <" + BaseJSON2JavaConvertor.BASIC_CLASSES + ">");
			return object;
		}

		Collection<Object> collection = ReflectUtils.newCollection(clazz);
		for (Object item : (Collection<?>) object) {
			collection.add(item);
		}
		return collection;
	}
}
