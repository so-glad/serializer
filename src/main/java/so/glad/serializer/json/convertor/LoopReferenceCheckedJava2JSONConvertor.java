package so.glad.serializer.json.convertor;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author Palmtale
 * 2015-3-25 13:48:36
 */
public class LoopReferenceCheckedJava2JSONConvertor extends BaseJava2JSONConvertor {

	protected final List<Object> convertedObjects = Lists.newArrayList();

	public Object toJSONObject(Object object) {
		if (isConverted(object)) {
			return null;
		}

		convertedObjects.add(object);
		Object jsonObject = super.toJSONObject(object);
		convertedObjects.remove(convertedObjects.size() - 1);

		return jsonObject;
	}

	protected boolean isConverted(Object object) {
		for (Object convertedObject : convertedObjects) {
			if (convertedObject == object) {
				return true;
			}
		}
		return false;
	}
}
