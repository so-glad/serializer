package so.glad.serializer.json.convertor.java2json;

import so.glad.serializer.json.convertor.Java2JSONConvertor;

/**
 * @author Palmtale
 * 2015-3-25 13:48:36
 */
public class Enumeration2JSONConvertor implements Java2JSONConvertor {

	public Object toJSONObject(Object object) {
		return object.toString();
	}

}
