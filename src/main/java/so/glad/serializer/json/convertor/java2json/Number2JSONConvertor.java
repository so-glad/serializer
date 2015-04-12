package so.glad.serializer.json.convertor.java2json;

import so.glad.serializer.json.convertor.Java2JSONConvertor;

import java.math.BigDecimal;

/**
 * @author Palmtale
 * 2015-3-25 13:48:36
 */
public class Number2JSONConvertor implements Java2JSONConvertor {

	public Object toJSONObject(Object object) {
		return new BigDecimal(object.toString());
	}

}
