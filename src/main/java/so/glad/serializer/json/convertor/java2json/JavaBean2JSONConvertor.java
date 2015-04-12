package so.glad.serializer.json.convertor.java2json;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import so.glad.serializer.json.ReflectUtils;
import so.glad.serializer.json.convertor.Java2JSONConvertor;

/**
 * @author Palmtale
 * 2015-3-25 13:48:36
 */
public class JavaBean2JSONConvertor implements Java2JSONConvertor {

	private static Logger logger = LoggerFactory.getLogger(JavaBean2JSONConvertor.class);

	private static final List<String> IGNORE_PROPERTY_NAMES = Lists.newArrayList();

	static {
		IGNORE_PROPERTY_NAMES.add("class");
		IGNORE_PROPERTY_NAMES.add("classLoader");
	}

	private Java2JSONConvertor convertor;

	public JavaBean2JSONConvertor(Java2JSONConvertor convertor) {
		this.convertor = convertor;
	}

	public Object toJSONObject(Object object) {
		PropertyConvertProcessor propertyConvertProcessor = new PropertyConvertProcessor();
		propertyConvertProcessor.setBean(object);
		boolean isHandled = ReflectUtils.forIn(object.getClass(), propertyConvertProcessor);
		
		if (isHandled) {
			return propertyConvertProcessor.getBeanMap();
		} else {
			Constructor<?> constructor = null;
			try {
				constructor = object.getClass().getConstructor(String.class);
			} catch (Exception ignored) {
			}

			if (constructor != null) {
				return object.toString();
			} else {
				if (logger.isWarnEnabled()) {
					logger.warn(object.getClass() + " is not support to convert");
				}
				return Maps.newHashMap();
			}
		}
	}

	public class PropertyConvertProcessor implements ReflectUtils.PropertyProcessor {

		private Map<String, Object> beanMap = Maps.newHashMap();
		private Object bean;

		public void setBean(Object bean) {
			this.bean = bean;
		}

		public Map<String, Object> getBeanMap() {
			return beanMap;
		}

		public void process(ReflectUtils.BeanProperty beanProperty) {
			if (IGNORE_PROPERTY_NAMES.contains(beanProperty.getPropertyName())) {
				return;
			}

			Object propertyValue = null;
			try {
				propertyValue = beanProperty.getPropertyValue(bean);
			} catch (Exception e) {
				logger.warn("Ignore property [" + beanProperty.getPropertyName() + "] of class ["
						+ beanProperty.getBeanClass().getName() + "]", e);
			}

			Object jsonObject = convertor.toJSONObject(propertyValue);
			if (jsonObject != null) {
				beanMap.put(beanProperty.getPropertyName(), jsonObject);
			}
		}
	}
}
