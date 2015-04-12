package so.glad.serializer.json;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Palmtale
 * 2015-3-25 13:48:36
 */
public class JSONPrettySerializer extends JSONSerializer {
	
	private int level = 0;

	protected void writeList(Writer writer, List<Object> list) throws IOException {
		writer.write("[");
		writer.write("\n");
		level = level + 1;
		for (Iterator<Object> iterator = list.iterator(); iterator.hasNext();) {
			writeFormat(writer);
			Object item = iterator.next();
			writeObject(writer, item);
			if (iterator.hasNext()) {
				writer.write(",");
			}
			writer.write("\n");
		}
		level = level - 1;
		writeFormat(writer);
		writer.write("]");
		writer.flush();
	}

	protected void writeMap(Writer writer, Map<Object, Object> map) throws IOException {
		writer.write("{");
		writer.write("\n");
		level = level + 1;
		for (Iterator<Object> iterator = map.keySet().iterator(); iterator.hasNext();) {
			writeFormat(writer);
			Object key = iterator.next();
			writer.write(escapeString(key.toString()));
			writer.write(" : ");
			writeObject(writer, map.get(key));
			if (iterator.hasNext()) {
				writer.write(",");
			}
			writer.write("\n");
		}
		level = level - 1;
		writeFormat(writer);
		writer.write("}");
		writer.flush();
	}

	private void writeFormat(Writer writer) throws IOException {
		for (int i = 0; i < level; i++) {
			writer.write("\t");
		}
	}
}
