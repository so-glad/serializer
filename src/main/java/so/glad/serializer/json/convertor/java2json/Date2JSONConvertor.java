package so.glad.serializer.json.convertor.java2json;

import com.google.common.collect.Maps;
import so.glad.serializer.json.convertor.Java2JSONConvertor;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * @author Palmtale
 * on 2015/04/13
 */
public class Date2JSONConvertor implements Java2JSONConvertor {

	public static final String JDK_DATE_KEY = "time";

	@Override
	public Object toJSONObject(Object object) {
		Map<String, BigDecimal> map = Maps.newHashMap();
		if (object instanceof Date) {
			map.put(JDK_DATE_KEY, new BigDecimal(((Date) object).getTime()));
		} else if (object instanceof Calendar) {
			map.put(JDK_DATE_KEY, new BigDecimal(((Calendar) object).getTimeInMillis()));
		}
		return map;
	}

}
