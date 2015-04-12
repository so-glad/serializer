package so.glad.serializer.json.convertor.java2json;

import java.lang.reflect.Array;
import java.util.List;

import com.google.common.collect.Lists;
import so.glad.serializer.json.convertor.Java2JSONConvertor;
/**
 * @author Palmtale
 * 2015-03-28
 */
public class Array2JSONConvertor implements Java2JSONConvertor {

	private Java2JSONConvertor convertor;

	public Array2JSONConvertor(Java2JSONConvertor convertor) {
		this.convertor = convertor;
	}

	@Override
	public Object toJSONObject(Object array) {
		List<Object> list = Lists.newArrayList();
		for (int i = 0; i < Array.getLength(array); i++) {
			list.add(convertor.toJSONObject(Array.get(array, i)));

		}
		return list;
	}

}
