package so.glad.serializer.json.convertor.json2java;

import so.glad.serializer.json.convertor.java2json.Date2JSONConvertor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 *
 * @author Palmtale
 * 2015-3-25 13:48:36
 */
public class JSON2DateConvertor extends AbstractJSON2JavaClassConvertor {

	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss SSS";

	@SuppressWarnings("unchecked")
	@Override
	protected Object toJavaObject(Object object, Class<?> clazz) {
		if (clazz.isInstance(object)) {
			return object;
		}

		Date date;
		if (object instanceof String) {
			String text = (String) object;
			try {
				date = new SimpleDateFormat(DEFAULT_DATE_FORMAT).parse(text);
			} catch (ParseException e) {
				throw new IllegalArgumentException("Illegal string value [" + text + "] for Date with format ["
						+ DEFAULT_DATE_FORMAT + "]");
			}
		} else if (object instanceof Map && ((Map) object).get(Date2JSONConvertor.JDK_DATE_KEY) instanceof BigDecimal) {
			date = new Date(((BigDecimal) ((Map) object).get(Date2JSONConvertor.JDK_DATE_KEY)).longValue());
		} else {
			throw new IllegalArgumentException(toConvertExceptionMessage(object, clazz));
		}

		if (clazz.equals(Date.class)) {
			return date;
		} else if (clazz.equals(java.sql.Date.class)) {
			return new java.sql.Date(date.getTime());
		} else if (clazz.equals(Timestamp.class)) {
			return new Timestamp(date.getTime());
		} else if (clazz.equals(Calendar.class)) {
			Calendar instance = Calendar.getInstance();
			instance.setTime(date);
			return instance;
		}

		throw new IllegalArgumentException(toConvertExceptionMessage(object, clazz));
	}
}
