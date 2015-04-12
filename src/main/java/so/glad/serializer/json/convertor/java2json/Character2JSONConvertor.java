package so.glad.serializer.json.convertor.java2json;

import so.glad.serializer.json.convertor.Java2JSONConvertor;

public class Character2JSONConvertor implements Java2JSONConvertor {

	@Override
	public Object toJSONObject(Object object) {
		return object.toString();
	}

}
