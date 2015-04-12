package so.glad.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author yangyang
 * @since 2009-4-1
 */
public final class StringSerializer {
    private StreamSerializer streamSerializer;

    public StringSerializer(StreamSerializer streamSerializer) {
        this.streamSerializer = streamSerializer;
    }

    public StreamSerializer getStreamSerializer() {
        return streamSerializer;
    }

    public String marshal(Object object) throws MarshalException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        streamSerializer.marshal(object, byteArrayOutputStream, Const.DEFAULT_ENCODING);
        return new String(byteArrayOutputStream.toByteArray());
    }

	public Object unmarshal(String string) throws UnmarshalException {
        return streamSerializer.unmarshal(new ByteArrayInputStream(string.getBytes()), Const.DEFAULT_ENCODING);
    }
}
