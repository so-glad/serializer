package so.glad.serializer.json.convertor.json2java;

import so.glad.serializer.json.JSON;
import so.glad.serializer.json.ReflectUtils;

import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * @author Palmtale
 * 2015-3-25 13:48:36
 */
public class JSON2JavaBeanConvertor extends AbstractJSON2JavaClassConvertor {

	@SuppressWarnings("unchecked")
	@Override
	protected Object toJavaObject(Object object, Class<?> clazz) {
		if (clazz.isInstance(object)) {
			return object;
		}

		if (!(object instanceof Map) && !(object instanceof String)) {
			throw new IllegalArgumentException(toConvertExceptionMessage(object, clazz));
		}
		
		if (object instanceof String) {
			try {
				Constructor<?> constructor = clazz.getConstructor(String.class);
				return constructor.newInstance(object);
			} catch (Exception e) {
				throw new IllegalArgumentException(toConvertExceptionMessage(object, clazz), e);
			}
		} else {
			Map<String, Object> map = (Map<String, Object>) object;

			if (Map.class.equals(clazz) || ReflectUtils.getInterfaces(clazz).contains(Map.class)) {
				Map<String, Object> newMap = ReflectUtils.newMap(clazz);
				for (String key : map.keySet()) {
					newMap.put(key, map.get(key));
				}
				return newMap;
			}

			Object newObject = ReflectUtils.newObject(clazz);
			PropertyFillProcessor propertyFillProcessor = new PropertyFillProcessor();
			propertyFillProcessor.setBean(newObject);
			propertyFillProcessor.setBeanMap(map);
			ReflectUtils.forIn(clazz, propertyFillProcessor);
			return newObject;
		}
	}

	public class PropertyFillProcessor implements ReflectUtils.PropertyProcessor {

		private Map<String, Object> beanMap;
		private Object bean;

		public void setBean(Object bean) {
			this.bean = bean;
		}

		public void setBeanMap(Map<String, Object> beanMap) {
			this.beanMap = beanMap;
		}

		public void process(ReflectUtils.BeanProperty beanProperty) {
			Object propertyValue = beanMap.get(beanProperty.getPropertyName());
			if (propertyValue == null) {
				return;
			}
			propertyValue = JSON.toJavaObject(propertyValue, beanProperty.getPropertyType());
			try {
				beanProperty.setPropertyValue(bean, propertyValue);
			} catch (Exception e) {
				log.warn("Ignore property [" + beanProperty.getPropertyName() + "] of class ["
						+ beanProperty.getBeanClass().getName() + "]", e);
			}
		}
	}

}
