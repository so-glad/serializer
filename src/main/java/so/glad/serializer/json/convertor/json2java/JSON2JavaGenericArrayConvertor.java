package so.glad.serializer.json.convertor.json2java;

import so.glad.serializer.json.convertor.JSON2JavaConvertor;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * @author Palmtale
 * 2015-3-25 13:48:36
 */
public class JSON2JavaGenericArrayConvertor implements JSON2JavaConvertor {

	private JSON2JavaConvertor convertor;

	public JSON2JavaGenericArrayConvertor(JSON2JavaConvertor convertor) {
		super();
		this.convertor = convertor;
	}

	@SuppressWarnings("unchecked")
	public Object toJavaObject(Object object, Type type) {
		GenericArrayType genericArrayType = (GenericArrayType) type;
		if (object instanceof ArrayList) {
			ArrayList arrayList = (ArrayList) object;
			Type componentType = genericArrayType.getGenericComponentType();

			while (!(componentType instanceof Class) && componentType instanceof ParameterizedType) {
				ParameterizedType parameterizedType = (ParameterizedType) componentType;
				componentType = parameterizedType.getRawType();
			}
			// Class componentClass = componentType;
			Object objectArray = Array.newInstance((Class<?>) componentType, arrayList.size());

			for (int i = 0; i < arrayList.size(); i++) {
				Array.set(objectArray, i, convertor.toJavaObject(arrayList.get(i), componentType));
			}
			return objectArray;
		}
		throw new IllegalArgumentException("Illegal object class [" + object.getClass().getName() + "] for Array");
	}
}
