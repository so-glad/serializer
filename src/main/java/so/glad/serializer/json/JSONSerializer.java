package so.glad.serializer.json;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author YangYang
 * @version 0.1, 2008-3-16 11:19:20
 */
public class JSONSerializer {

	@SuppressWarnings("unchecked")
	public void writeObject(Writer writer, Object object) throws IOException {
		if (object == null) {
			writer.write("null");
		} else if (object instanceof String) {
			writer.write(escapeString((String) object));
		} else if (object instanceof BigDecimal) {
			writer.write(object.toString());
		} else if (object instanceof Boolean) {
			String text = (Boolean) object ? "true" : "false";
			writer.write(text);
		} else if (object instanceof List) {
			writeList(writer, (List) object);
		} else if (object instanceof Map) {
			writeMap(writer, (Map) object);
		} else {
			throw new IllegalArgumentException("Unsupported Class [" + object.getClass().getName() + "] for "
					+ JSONSerializer.class.getName());
		}
	}

	protected String escapeString(String string) {
		StringBuilder stringBuffer = new StringBuilder();

		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			switch (c) {
			case '"': // quotation mark
				stringBuffer.append("\\\"");
				break;
			case '\\': // reverse solidus
				stringBuffer.append("\\\\");
				break;
			case '\b': // bell
				stringBuffer.append("\\b");
				break;
			case '\f': // form feed
				stringBuffer.append("\\f");
				break;

			case '\n': // newline
				stringBuffer.append("\\n");
				break;

			case '\r': // carriage return
				stringBuffer.append("\\r");
				break;

			case '\t': // horizontal tab
				stringBuffer.append("\\t");
				break;

			default: // everything else

				stringBuffer.append(c);
			}
		}

		return "\"" + stringBuffer.toString() + "\"";
	}

	protected void writeList(Writer writer, List<Object> list) throws IOException {
		writer.write("[");
		for (Iterator<Object> iterator = list.iterator(); iterator.hasNext();) {
			Object item = iterator.next();
			writeObject(writer, item);
			if (iterator.hasNext()) {
				writer.write(",");
			}
		}
		writer.write("]");
	}

	protected void writeMap(Writer writer, Map<Object, Object> map) throws IOException {
		writer.write("{");
		for (Iterator<Object> iterator = map.keySet().iterator(); iterator.hasNext();) {
			Object key = iterator.next();
			writer.write(escapeString(key.toString()));
			writer.write(":");
			writeObject(writer, map.get(key));
			if (iterator.hasNext()) {
				writer.write(",");
			}
		}
		writer.write("}");
	}
}
