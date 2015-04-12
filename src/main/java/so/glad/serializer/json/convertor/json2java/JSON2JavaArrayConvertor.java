package so.glad.serializer.json.convertor.json2java;

import so.glad.serializer.json.convertor.JSON2JavaConvertor;

import java.lang.reflect.Array;
import java.util.List;

/**
 * @author Palmtale
 * 2015-3-25 13:48:36
 */
public class JSON2JavaArrayConvertor extends AbstractJSON2JavaClassConvertor {

	private JSON2JavaConvertor convertor;

	public JSON2JavaArrayConvertor(JSON2JavaConvertor convertor) {
		super();
		this.convertor = convertor;
	}

	@Override
	protected Object toJavaObject(Object object, Class<?> clazz) {
		List<?> list = (List<?>) object;

		Object array = Array.newInstance(clazz, list.size());
		for (int i = 0; i < list.size(); i++) {
			Array.set(array, i, convertor.toJavaObject(list.get(i), clazz));
		}
		return array;
	}

}
