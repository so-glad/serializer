package so.glad.serializer.json;

import so.glad.serializer.json.convertor.*;

import java.io.*;
import java.lang.reflect.Type;

/**
 * @author Palmtale
 * 2015-3-25 13:48:36
 */
public class JSON {

	@Deprecated
	public static final String FORMAT_PRETTY = "pretty";
	public static final String FORMAT_STYLE_PRETTY = FORMAT_PRETTY;

	private final BaseJava2JSONConvertor java2JSONConvertor = new LoopReferenceCheckedJava2JSONConvertor();
	private final BaseJSON2JavaConvertor json2JavaConvertor = new BaseJSON2JavaConvertor();

	private final JSONSerializer jsonSerializer = new JSONSerializer();
	private final JSONDeserializer jsonDeserializer = new JSONDeserializer();

	private final TypeIntrospector introspector = new TypeIntrospector();

	public transient static boolean IS_EXIST_JODA_LIB = false;
	static {
		IS_EXIST_JODA_LIB= isExistJodaTimeLib();
	}
	
	public static boolean isExistJodaTimeLib(){
		String localDateClassName = "org.joda.time.LocalDate";
		try {
			Class.forName(localDateClassName);
			return true;
		} catch (ClassNotFoundException e) {
			try {
				JSON.class.getClassLoader().loadClass(localDateClassName);
				return true;
			} catch (ClassNotFoundException e1) {
				try {
					Thread.currentThread().getContextClassLoader().loadClass(localDateClassName);
					return true;
				} catch (ClassNotFoundException ignored) {
				}
			}
		}
		return false;
	}
	
	public JSON() {

	}

	public Object toJSON(Object object) {
		return this.java2JSONConvertor.toJSONObject(object);
	}

	public Object toJava(Object object, Type type) {
		return this.json2JavaConvertor.toJavaObject(object, type);
	}

	public <T>T toJava(Object object, Class<T> type) {
		Object javaObject = this.json2JavaConvertor.toJavaObject(object, type);
		if(javaObject.getClass().isAssignableFrom(type)){
			return (T)javaObject;
		}
		return null;
	}

	public JSON registerConvertor(Class<?> clazz, CustomConvertor convertor) {
		this.java2JSONConvertor.registerConvertor(clazz, convertor);
		this.json2JavaConvertor.registerConvertor(clazz, convertor);
		return this;
	}

	public static void registerStaticConvertor(Class<?> clazz, CustomConvertor convertor) {
		new JSON().java2JSONConvertor.registerConvertor(clazz, convertor);
		new JSON().json2JavaConvertor.registerConvertor(clazz, convertor);
	}

	public static Object deserialize(Reader reader) throws JSONParseException, IOException {
		return new JSON().jsonDeserializer.read(reader);
	}

	public static void serialize(Writer writer, Object object) throws IOException {
		new JSON().jsonSerializer.writeObject(writer, object);
        writer.flush();
	}

	public static void serializePretty(Writer writer, Object object) throws IOException {
		new JSONPrettySerializer().writeObject(writer, object);
	}

	public static Object toJSONObject(Object object) {
		return new JSON().toJSON(object);
	}

	public static <T>T toJavaObject(Object object, Class<T> type) {
		return new JSON().toJava(object, type);
	}
	public static Object toJavaObject(Object object, Type type){
		return new JSON().toJava(object, type);
	}

	public static Object introspect(Type type) {
		return new JSON().introspector.introspect(type);
	}

	public static void format(Reader reader, Writer writer, String formatStyle) throws IOException, JSONParseException {
		if (formatStyle == null) {
			return;
		}
		if (FORMAT_STYLE_PRETTY.equals(formatStyle)) {
			serializePretty(writer, deserialize(reader));
		} else {
			serialize(writer, deserialize(reader));
		}
	}

	public static String format(String string, String type) {
		StringReader stringReader = new StringReader(string);
		StringWriter stringWriter = new StringWriter();
		try {
			format(stringReader, stringWriter, type);
		} catch (IOException e) {
			// ignore
			return null;
		} catch (JSONParseException e) {
			// ignore
			return null;
		}
		return stringWriter.toString();
	}
}
