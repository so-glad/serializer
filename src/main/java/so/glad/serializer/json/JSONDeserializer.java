
package so.glad.serializer.json;

import com.google.common.collect.Lists;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONDeserializer {

	private JSONTokenizer tokenizer;

	private JSONToken token;

	public Object read(Reader reader) throws JSONParseException, IOException {
		tokenizer = new JSONTokenizer(reader);
		nextToken();
		return parseValue();
	}

	/**
	 * Returns the next token from the tokenzier reading the JSON string
     * @return the next token
     * @throws java.io.IOException IOException
     * @throws so.glad.serializer.json.JSONParseException JSONParseException
	 */
	private JSONToken nextToken() throws JSONParseException, IOException {
		return token = tokenizer.getNextToken();
	}

	/**
	 * Attempt to parse an array
     * @return the array value
     * @throws java.io.IOException IOException
     * @throws so.glad.serializer.json.JSONParseException JSONParseException
	 */
	private List<Object> parseArray() throws JSONParseException, IOException {
		// create an array internally that we're going to attempt
		// to parse from the tokenizer
		List<Object> list = Lists.newArrayList();

		// grab the next token from the tokenizer to move
		// past the opening [
		nextToken();

		// check to see if we have an empty array
		if (token.getType() == JSONTokenType.RIGHT_BRACKET) {
			// we're done reading the array, so return it
			return list;
		}

		// deal with elements of the array, and use an "infinite"
		// loop because we could have any amount of elements
		while (true) {
			// read in the value and add it to the array
			list.add(parseValue());

			// after the value there should be a ] or a ,
			nextToken();

            if (token == null) {
                tokenizer.parseError("Expecting ']' or ',' but found unexpected end");
            }
			if (token.getType() == JSONTokenType.RIGHT_BRACKET) {
				// we're done reading the array, so return it
				return list;
			} else if (token.getType() == JSONTokenType.COMMA) {
				// move past the comma and read another value
				nextToken();
			} else {
                tokenizer.parseError("Expecting ']' or ',' but found '" + token.getValue() + "'");
			}
		}
	}

	/**
	 * Attempt to parse an object
     * @return the object value
     * @throws java.io.IOException IOException
     * @throws so.glad.serializer.json.JSONParseException JSONParseException
	 */
	private Map<String, Object> parseObject() throws JSONParseException, IOException {
		// create the object internally that we're going to
		// attempt to parse from the tokenizer
		HashMap<String, Object> map = new HashMap<String, Object>();

		// store the string part of an object member so
		// that we can assign it a value in the object
		String key;

		// grab the next token from the tokenizer
		nextToken();

		// check to see if we have an empty object
		if (token.getType() == JSONTokenType.RIGHT_BRACE) {
			// we're done reading the object, so return it
			return map;
		}

		// deal with members of the object, and use an "infinite"
		// loop because we could have any amount of members
		while (true) {

			if (token.getType() == JSONTokenType.STRING) {
				// the string value we read is the key for the object
				key = (String) token.getValue();

				// move past the string to see what's next
				nextToken();

				// after the string there should be a :
				if (token.getType() == JSONTokenType.COLON) {

					// move past the : and read/assign a value for the key
					nextToken();
					map.put(key, parseValue());

					// move past the value to see what's next
					nextToken();

                    if (token == null) {
                        tokenizer.parseError("Expecting '}' or ',' but found unexpected end");
					    // after the value there's either a } or a ,
                    }else if (token.getType() == JSONTokenType.RIGHT_BRACE) {
						// // we're done reading the object, so return it
						return map;
					} else if (token.getType() == JSONTokenType.COMMA) {
						// skip past the comma and read another member
						nextToken();
					} else {
						tokenizer.parseError("Expecting '}' or ',' but found '" + token.getValue() + "'");
					}
				} else {
					tokenizer.parseError("Expecting ':' but found '" + token.getValue() + "'");
				}
			} else {
				tokenizer.parseError("Expecting string but found '" + token.getValue() + "'");
			}
		}
	}

	/**
	 * Attempt to parse a value
     * @return the value
     * @throws java.io.IOException IOException
     * @throws so.glad.serializer.json.JSONParseException JSONParseException
	 */
	private Object parseValue() throws JSONParseException, IOException {
		if (token == null) {
			throw new JSONParseException("Unexpected end");
		}

        switch (token.getType()) {
            case JSONTokenType.LEFT_BRACE:
                return parseObject();

            case JSONTokenType.LEFT_BRACKET:
                return parseArray();

            case JSONTokenType.STRING:
            case JSONTokenType.NUMBER:
            case JSONTokenType.TRUE:
            case JSONTokenType.FALSE:
            case JSONTokenType.NULL:
                return token.getValue();

            default:
                throw new JSONParseException("Unexpected " + token.getValue());

        }
	}
}

class JSONToken {

	private int type = JSONTokenType.UNKNOWN;
	private Object value;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}

class JSONTokenizer {
	private Reader reader;
	private char ch;
	private char[] chBuffer = new char[1024 * 4];
	private int chBufferIndex = chBuffer.length;
	private int chBufferSize = chBuffer.length;

	public JSONTokenizer(Reader reader) throws IOException {
		this.reader = reader;
		nextChar();
	}

	public JSONToken getNextToken() throws JSONParseException, IOException {
		JSONToken token = new JSONToken();
		char[] buffer;

		skipIgnored();

		// examine the new character and see what we have...
		switch (ch) {

		case '{':
			token.setType(JSONTokenType.LEFT_BRACE);
			token.setValue('{');
			nextChar();
			break;

		case '}':
			token.setType(JSONTokenType.RIGHT_BRACE);
			token.setValue('}');
			nextChar();
			break;

		case '[':
			token.setType(JSONTokenType.LEFT_BRACKET);
			token.setValue('[');
			nextChar();
			break;

		case ']':
			token.setType(JSONTokenType.RIGHT_BRACKET);
			token.setValue(']');
			nextChar();
			break;

		case ',':
			token.setType(JSONTokenType.COMMA);
			token.setValue(',');
			nextChar();
			break;

		case ':':
			token.setType(JSONTokenType.COLON);
			token.setValue(':');
			nextChar();
			break;

		case 't': // attempt to read true
			String possibleTrue = "t" + nextChar() + nextChar() + nextChar();

			if (possibleTrue.equals("true")) {
				token.setType(JSONTokenType.TRUE);
				token.setValue(true);
				nextChar();
			} else {
				parseError("Expecting 'true' but found '" + possibleTrue + "'");
			}

			break;

		case 'f': // attempt to read false
			buffer = new char[4];
			read(buffer);
			String possibleFalse = "f" + new String(buffer);

			if (possibleFalse.equals("false")) {
				token.setType(JSONTokenType.FALSE);
				token.setValue(false);
				nextChar();
			} else {
				parseError("Expecting 'false' but found '" + possibleFalse + "'");
			}

			break;

		case 'n': // attempt to read null
			buffer = new char[3];
			read(buffer);
			String possibleNull = "n" + new String(buffer);

			if (possibleNull.equals("null")) {
				token.setType(JSONTokenType.NULL);
				token.setValue(null);
				nextChar();
			} else {
                parseError("Expecting 'null' but found '" + possibleNull + "'");
			}

			break;

		case '"': // the start of a string
			token = readString();
			break;
		default:
			// see if we can read a number
			if (isDigit(ch) || ch == '-') {
				token = readNumber();
			} else if (ch == '\0') {
				// check for reading past the end of the string
				return null;
			} else {
				// not sure what was in the input string - it's not
				// anything we expected
				parseError("Unexpected '" + ch + "' encountered");
			}
		}

		return token;
	}

	/**
	 * Attempts to read a string from the input string. Places the character
	 * location at the first character after the string. It is assumed that ch
	 * is " before this method is called.
	 * 
	 * @return the JSONToken with the string value if a string could be read.
	 *         Throws an error otherwise.
     * @throws java.io.IOException IOException
     * @throws so.glad.serializer.json.JSONParseException JSONParseException
	 */
	private JSONToken readString() throws JSONParseException, IOException {
		// the token for the string we'll try to read
		JSONToken token = new JSONToken();
		token.setType(JSONTokenType.STRING);

		// the string to store the string we'll try to read
		StringBuilder string = new StringBuilder();

		// advance past the first "
		nextChar();

		while (ch != '"' && ch != '\0') {

			// unescape the escape sequences in the string
			if (ch == '\\') {

				// get the next character so we know what
				// to unescape
				nextChar();

				switch (ch) {

				case '"': // quotation mark
					string.append('"');
					break;

				case '/': // solidus
					string.append("/");
					break;

				case '\\': // reverse solidus
					string.append('\\');
					break;

				case 'b': // bell
					string.append('\b');
					break;

				case 'f': // form feed
					string.append('\f');
					break;

				case 'n': // newline
					string.append('\n');
					break;

				case 'r': // carriage return
					string.append('\r');
					break;

				case 't': // horizontal tab
					string.append('\t');
					break;

				case 'u':
					// convert a unicode escape sequence
					// to it's character value - expecting
					// 4 hex digits

					// save the characters as a string we'll convert to an int
					String hexValue = "";

					// try to find 4 hex characters
					for (int i = 0; i < 4; i++) {
						// get the next character and determine
						// if it's a valid hex digit or not
						if (!isHexDigit(nextChar())) {
							parseError("Excepted a hex digit, but found '" + ch + "'");
						}
						// valid, add it to the value
						hexValue += ch;
					}

					// convert hexValue to an integer, and use that
					// integrer value to create a character to add
					// to our string.
					string.append((char)(Integer.parseInt(hexValue, 16)));

					break;

				default:
					// couldn't unescape the sequence, so just pass it through
					string.append("\\" + ch);

				}

			} else {
				// didn't have to unescape, so add the character to the string
				string.append(ch);

			}

			// move to the next character
			nextChar();

		}

		// we read past the end of the string without closing it, which
		// is a parse error
		if (ch == '\0') {
			parseError("Unterminated string literal");
		}

		// move past the closing " in the input string
		nextChar();

		// attach to the string to the token so we can return it
		token.setValue(string.toString());

		return token;
	}

	/**
	 * Attempts to read a number from the input string. Places the character
	 * location at the first character after the number.
	 *
	 * @return The JSONToken with the number value if a number could be read.
	 *         Throws an error otherwise.
     * @throws java.io.IOException IOException
     * @throws so.glad.serializer.json.JSONParseException JSONParseException
	 */
	private JSONToken readNumber() throws JSONParseException, IOException {
		// the token for the number we'll try to read
		JSONToken token = new JSONToken();
		token.setType(JSONTokenType.NUMBER);

		// the string to accumulate the number characters
		// into that we'll convert to a number at the end
		String input = "";

		// check for a negative number
		if (ch == '-') {
			input += '-';
			nextChar();
		}

		// the number must start with a digit
		if (!isDigit(ch)) {
			parseError("Expecting a digit, but found '" + ch + "'");
		}

		// 0 can only be the first digit if it
		// is followed by a decimal point
		if (ch == '0') {
			input += ch;
			nextChar();

			// make sure no other digits come after 0
			if (isDigit(ch)) {
                parseError("A digit '" + ch + "' cannot immediately follow 0");
			}
		} else {
			// read numbers while we can
			while (isDigit(ch)) {
				input += ch;
				nextChar();
			}
		}

		// check for a decimal value
		if (ch == '.') {
			input += '.';
			nextChar();

			// after the decimal there has to be a digit
			if (!isDigit(ch)) {
				parseError("Expecting a digit, but found '" + ch + "'");
			}

			// read more numbers to get the decimal value
			while (isDigit(ch)) {
				input += ch;
				nextChar();
			}
		}

		// check for scientific notation
		if (ch == 'e' || ch == 'E') {
			input += "e";
			nextChar();
			// check for sign
			if (ch == '+' || ch == '-') {
				input += ch;
				nextChar();
			}

			// require at least one number for the exponent
			// in this case
			if (!isDigit(ch)) {
				parseError("Expecting a digit, but found '" + ch + "' after scientific notation 'E'");
			}

			// read in the exponent
			while (isDigit(ch)) {
				input += ch;
				nextChar();
			}
		}

		// convert the string to a number value
		BigDecimal bigDecimal = null;
		try {
			bigDecimal = new BigDecimal(input);
		} catch (NumberFormatException e) {
			parseError("Number '" + input + "' is not valid!");
		}
		token.setValue(bigDecimal);
		return token;
	}

	/**
	 * Reads the next character in the input string and advances the character
	 * location.
	 *
	 * @return The next character in the input string, or null if we've read
	 *         past the end.
     * @throws java.io.IOException IOException
	 */
	private char nextChar() throws IOException {
		if(chBufferIndex < this.chBufferSize) {
			ch = chBuffer[chBufferIndex];
			chBufferIndex++;
			return ch;
		}
		this.chBufferSize = reader.read(chBuffer);
		if(this.chBufferSize == -1) {
			ch = '\0';
			return ch;
		}
		chBufferIndex = 0;
		ch = chBuffer[chBufferIndex];
		chBufferIndex++;
		return ch;
	}

    private void read(char[] buffer) throws IOException {
        for(int i=0; i<buffer.length; i++) {
            buffer[i] = nextChar();
        }
    }

	/**
	 * Advances the character location past any sort of white space and comments
     * @throws java.io.IOException IOException
     * @throws so.glad.serializer.json.JSONParseException JSONParseException
	 */
	private void skipIgnored() throws JSONParseException, IOException {
		skipWhite();
		skipComments();
		skipWhite();
	}

	/**
	 * Skips comments in the input string, either single-line or multi-line.
	 * Advances the character to the first position after the end of the
	 * comment.
     * @throws java.io.IOException IOException
     * @throws so.glad.serializer.json.JSONParseException JSONParseException
	 */
	private void skipComments() throws JSONParseException, IOException {
		if (ch == '/') {
			// Advance past the first / to find out what type of comment
			nextChar();
			switch (ch) {
			case '/': // single-line comment, read through end of line

				// Loop over the characters until we find
				// a newline or until there's no more characters left
				do {
					nextChar();
				} while (ch != '\n' && ch != '\0');

				// move past the \n
				nextChar();

				break;

			case '*': // multi-line comment, read until closing */

				// move past the opening *
				nextChar();

				// try to find a trailing */
				while (true) {
					if (ch == '*') {
						// check to see if we have a closing /
						nextChar();
						if (ch == '/') {
							// move past the end of the closing */
							nextChar();
							break;
						}
					} else {
						// move along, looking if the next character is a *
						nextChar();
					}

					// when we're here we've read past the end of
					// the string without finding a closing */, so error
					if (ch == '\0') {
						parseError("Multi-line comment not closed");
					}
				}

				break;

			// Can't match a comment after a /, so it's a parsing error
			default:
				parseError("Unexpected '" + ch + "' encountered (expecting '/' or '*' )");
			}
		}

	}

	/**
	 * Skip any whitespace in the input string and advances the character to the
	 * first character after any possible whitespace.
     * @throws java.io.IOException IOException
	 */
	private void skipWhite() throws IOException {

		// As long as there are spaces in the input
		// stream, advance the current location pointer
		// past them
		while (isWhiteSpace(ch)) {
			nextChar();
		}

	}

	/**
	 * Determines if a character is whitespace or not.
	 *
     * @param ch char value
	 * @return True if the character passed in is a whitespace character
	 */
	private Boolean isWhiteSpace(char ch) {
		return Character.isWhitespace(ch);
	}

	/**
	 * Determines if a character is a digit [0-9].
	 *
     * @param ch char value
	 * @return True if the character passed in is a digit
	 */
	private Boolean isDigit(char ch) {
		return Character.isDigit(ch);
	}

	/**
	 * Determines if a character is a digit [0-9].
	 *
     * @param ch char value
	 * @return True if the character passed in is a digit
	 */
	private Boolean isHexDigit(char ch) {
		// get the uppercase value of ch so we only have
		// to compare the value between 'A' and 'F'
		char uc = ("" + ch).toUpperCase().charAt(0);

		// a hex digit is a digit of A-F, inclusive ( using
		// our uppercase constraint )
		return (isDigit(ch) || (uc >= 'A' && uc <= 'F'));
	}

	/**
	 * Raises a parsing error with a specified message, tacking on the error
	 * location and the original string.
	 * 
	 * @param message the message indicating why the error occurred
     * @throws so.glad.serializer.json.JSONParseException JSONParseException
	 */
	public void parseError(String message) throws JSONParseException {
		throw new JSONParseException(message);
	}
}
