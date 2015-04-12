package so.glad.serializer.json.convertor;

import java.lang.reflect.Type;

/**
 * @author Palmtale
 * 2015-3-25 13:48:36
 */
public interface JSON2JavaConvertor {
	
    Object toJavaObject(Object object, Type type);
    
}
