package so.glad.serializer;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Palmtale
 * 2015-3-25
 */
public interface StreamSerializer {

	void marshal(Object object, OutputStream outputStream, String encoding) throws MarshalException;

	Object unmarshal(InputStream inputStream, String encoding) throws UnmarshalException;

}
