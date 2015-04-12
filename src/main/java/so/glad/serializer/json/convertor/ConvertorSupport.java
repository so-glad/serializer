package so.glad.serializer.json.convertor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Cartoon
 * on 2015/04/13
 */
public class ConvertorSupport {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected <T> T findConvertor(Map<Class<?>, T> convertors, Class<?> clazz, boolean isStrict) {
		if (isStrict) {
			return convertors.get(clazz);
		}

		for (Entry<Class<?>, T> entry : convertors.entrySet()) {
			if (entry.getKey().isAssignableFrom(clazz)) {
				return entry.getValue();
			}
		}

		return null;
	}

}
