package so.glad.serializer.json.convertor.java2json;

import com.google.common.collect.Maps;
import so.glad.serializer.json.convertor.Java2JSONConvertor;

import java.util.Map;

/**
 * @author Palmtale
 * 2015-3-25 13:48:36
 */
public class Map2JSONConvertor implements Java2JSONConvertor {

	private Java2JSONConvertor convertor;

	public Map2JSONConvertor(Java2JSONConvertor convertor) {
		this.convertor = convertor;
	}

	public Object toJSONObject(Object object) {
		Map<Object, Object> result = Maps.newLinkedHashMap();

		Map<?, ?> map = (Map<?, ?>) object;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object key = entry.getKey();
            Object jsonObject = convertor.toJSONObject(entry.getValue());
            result.put(key, jsonObject);
        }
		return result;
	}
}
