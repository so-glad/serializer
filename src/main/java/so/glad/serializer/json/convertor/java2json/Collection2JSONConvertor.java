package so.glad.serializer.json.convertor.java2json;

import so.glad.serializer.json.convertor.Java2JSONConvertor;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author Palmtale
 * 2015-03-28
 */
public class Collection2JSONConvertor implements Java2JSONConvertor {

	private Java2JSONConvertor convertor;

	public Collection2JSONConvertor(Java2JSONConvertor convertor) {
		this.convertor = convertor;
	}

	@Override
	public Object toJSONObject(Object collection) {
		return ((Collection<?>) collection).stream().map(convertor::toJSONObject).collect(Collectors.toList());
	}

}
