package so.glad.serializer.json.convertor.json2java;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 *
 * @author Palmtale
 * 2015-3-25 13:48:36
 */
public class JSON2NumberConvertor extends AbstractJSON2JavaClassConvertor {

	@Override
	protected Object toJavaObject(Object object, Class<?> clazz) {
		if (clazz.isInstance(object)) {
			return object;
		}

		BigDecimal bigDecimal;
		if (object instanceof String) {
			bigDecimal = new BigDecimal((String) object);
		} else if (object instanceof BigDecimal) {
			bigDecimal = (BigDecimal) object;
		} else {
			throw new IllegalArgumentException(toConvertExceptionMessage(object, clazz));
		}

		if (BigDecimal.class.equals(clazz)) {
			return bigDecimal;
		}
		if (Byte.class.equals(clazz) || Byte.TYPE.equals(clazz)) {
			return bigDecimal.byteValue();
		}
		if (Long.class.equals(clazz) || Long.TYPE.equals(clazz)) {
			return bigDecimal.longValue();
		}
		if (Float.class.equals(clazz) || Float.TYPE.equals(clazz)) {
			return bigDecimal.floatValue();
		}
		if (Double.class.equals(clazz) || Double.TYPE.equals(clazz)) {
			return bigDecimal.doubleValue();
		}
		if (Integer.class.equals(clazz) || Integer.TYPE.equals(clazz)) {
			return bigDecimal.intValue();
		}
		if (Short.class.equals(clazz) || Short.TYPE.equals(clazz)) {
			return bigDecimal.shortValue();
		}
		if (BigInteger.class.equals(clazz)) {
			return bigDecimal.toBigInteger();
		}

		throw new IllegalArgumentException(toConvertExceptionMessage(object, clazz));
	}

}
