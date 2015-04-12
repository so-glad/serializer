package so.glad.serializer.json;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author Palmtale
 * 2015-3-25 13:48:36
 */
public class ReflectUtils {

	private static Logger log = LoggerFactory.getLogger(ReflectUtils.class);

	public static final String PROPERTY_TYPE_STRICT = "STRICT";
	public static final String PROPERTY_TYPE_LOOSE = "LOOSE";

	public interface PropertyProcessor {
		void process(BeanProperty beanProperty);
	}

	public static class BeanProperty {
		private Class<?> clazz;
		private String propertyName;
		private Method getMethod;
		private Method writeMethod;

		public BeanProperty(Class<?> clazz, String propertyName, Method getMethod, Method writeMethod) {
			this.clazz = clazz;
			this.propertyName = propertyName;
			this.getMethod = getMethod;
			this.writeMethod = writeMethod;
		}

		public String getPropertyName() {
			return propertyName;
		}

		public Type getPropertyType() {
			if (this.getMethod != null) {
				return this.getMethod.getGenericReturnType();
			} else {
				return this.writeMethod.getGenericParameterTypes()[0];
			}
		}

		public Class<?> getPropertyClass() {
			if (this.getMethod != null) {
				return this.getMethod.getReturnType();
			} else {
				return this.writeMethod.getParameterTypes()[0];
			}
		}

		public Class<?> getBeanClass() {
			return this.clazz;
		}

		public Object getPropertyValue(Object bean) {
			return invokeMethod(this.getMethod, bean);
		}

		public void setPropertyValue(Object bean, Object value) {
			invokeMethod(this.writeMethod, bean, value);
		}
	}

	public static boolean forIn(Class<?> clazz, PropertyProcessor propertyProcessor, String type) {
		PropertyDescriptor[] propertyDescriptors;
		try {
			propertyDescriptors = Introspector.getBeanInfo(clazz).getPropertyDescriptors();
		} catch (IntrospectionException e) {
			log.error("Introspect class [" + clazz.getName() + "] failed", e);
			throw new RuntimeException(e);
		}
		
		boolean isHandled = false;
		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			String propertyName = propertyDescriptor.getName();
			Method getMethod = propertyDescriptor.getReadMethod();
			Method writeMethod = propertyDescriptor.getWriteMethod();
			if (PROPERTY_TYPE_STRICT.equalsIgnoreCase(type)) {
				if (getMethod == null || writeMethod == null) {
					continue;
				}
			}

			isHandled = true;
			propertyProcessor.process(new BeanProperty(clazz, propertyName, getMethod, writeMethod));
		}
		return isHandled;
	}

	public static boolean forIn(Class<?> clazz, PropertyProcessor propertyProcessor) {
		return forIn(clazz, propertyProcessor, PROPERTY_TYPE_STRICT);
	}

	@SuppressWarnings("unchecked")
	public static Set<Class> getInterfaces(Class<?> clazz) {
		Set<Class> iterfaces = Sets.newLinkedHashSet();
		iterfaces.addAll(Arrays.asList(clazz.getInterfaces()));
		for (Class superClass : getSuperClasses(clazz)) {
			iterfaces.addAll(Arrays.asList(superClass.getInterfaces()));
		}
		return iterfaces;
	}

	@SuppressWarnings("unchecked")
	public static Set<Class> getSuperClasses(Class clazz) {
		Set<Class> superClasses = Sets.newLinkedHashSet();
		while (clazz.getSuperclass() != null && !Object.class.equals(clazz.getSuperclass())) {
			superClasses.add(clazz.getSuperclass());
			clazz = clazz.getSuperclass();
		}
		return superClasses;
	}

	public static Object invokeMethod(Method method, Object bean, Object... parameters) {
		boolean accessible = method.isAccessible();
		try {
			if (!accessible) {
				method.setAccessible(true);
			}
			return method.invoke(bean, parameters);
		} catch (IllegalAccessException e) {
			String message = "Invoke method [" + method + "] failed";
			if (log.isDebugEnabled()) {
				log.debug(message, e);
			}
			throw new RuntimeException(message, e);
		} catch (InvocationTargetException e) {
			String message = "Invoke method [" + method + "] failed";
			if (log.isDebugEnabled()) {
				log.debug(message, e.getTargetException());
			}
			throw new RuntimeException(message, e.getTargetException());
		} finally {
			method.setAccessible(accessible);
		}
	}

	@SuppressWarnings("unchecked")
	public static Collection<Object> newCollection(Class<?> clazz) {
		Collection<Object> collection;
		if (!clazz.isInstance(collection = Lists.newArrayList())
				&& !clazz.isInstance(collection = Sets.newLinkedHashSet())) {
			collection = (Collection<Object>) newObject(clazz);
		}
		return collection;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> newMap(Class<?> clazz) {
		Map<String, Object> newMap;
		if (!clazz.isInstance(newMap = Maps.newLinkedHashMap())) {
			newMap = (Map) newObject(clazz);
		}
		return newMap;
	}

	public static Object newObject(Class<?> clazz) {
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			throw new IllegalArgumentException("New an instance of [" + clazz + "] failed", e);
		}
	}
}
