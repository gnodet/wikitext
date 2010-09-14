/*******************************************************************************
 * Copyright (c) 2007, 2010 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     Fintan Bolton - modified to normalize EOL character and to replace
 *         certain special characters with entities.
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.core.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.eclipse.mylyn.internal.wikitext.core.util.XML11Char;

/**
 * A default implementation of {@link XmlStreamWriter} that creates XML character output.
 * 
 * @author David Green
 * @since 1.0
 */
public class DefaultXmlStreamWriter extends XmlStreamWriter {

	private static final String STANDARD_LINE_TERMINATOR = "\n";

	private PrintWriter out;

	private final Map<String, String> prefixToUri = new HashMap<String, String>();

	private final Map<String, String> uriToPrefix = new HashMap<String, String>();

	private boolean inEmptyElement = false;

	private boolean inStartElement = false;

	private final Stack<String> elements = new Stack<String>();

	private char xmlHederQuoteChar = '\'';

	public DefaultXmlStreamWriter(OutputStream out) throws UnsupportedEncodingException {
		this.out = createUtf8PrintWriter(out);
	}

	public DefaultXmlStreamWriter(Writer out) {
		this.out = new PrintWriter(out);
	}

	public DefaultXmlStreamWriter(Writer out, char xmlHeaderQuoteChar) {
		this.out = new PrintWriter(out);
		this.xmlHederQuoteChar = xmlHeaderQuoteChar;
	}

	protected PrintWriter createUtf8PrintWriter(java.io.OutputStream out) throws UnsupportedEncodingException {
		return new java.io.PrintWriter(new OutputStreamWriter(out, "UTF8")); //$NON-NLS-1$
	}

	@Override
	public void close() {
		if (out != null) {
			closeElement();
			flush();
		}
		out = null;
	}

	@Override
	public void flush() {
		out.flush();
	}

	@Override
	public String getPrefix(String uri) {
		return uriToPrefix.get(uri);
	}

	public Object getProperty(String name) throws IllegalArgumentException {
		return null;
	}

	@Override
	public void setDefaultNamespace(String uri) {
		setPrefix("", uri); //$NON-NLS-1$
	}

	@Override
	public void setPrefix(String prefix, String uri) {
		prefixToUri.put(prefix, uri);
		uriToPrefix.put(uri, prefix);
	}

	@Override
	public void writeAttribute(String localName, String value) {
		out.write(' ');
		out.write(localName);
		out.write("=\""); //$NON-NLS-1$
		if (value != null) {
			attrEncode(value);
		}
		out.write("\""); //$NON-NLS-1$
	}

	@Override
	public void writeAttribute(String namespaceURI, String localName, String value) {
		out.write(' ');
		String prefix = uriToPrefix.get(namespaceURI);
		if (prefix != null && prefix.length() > 0) {
			out.write(prefix);
			out.write(':');
		}
		out.write(localName);
		out.write("=\""); //$NON-NLS-1$
		if (value != null) {
			attrEncode(value);
		}
		out.write("\""); //$NON-NLS-1$
	}

	@Override
	public void writeAttribute(String prefix, String namespaceURI, String localName, String value) {
		out.write(' ');
		if (prefix != null && prefix.length() > 0) {
			out.write(prefix);
			out.write(':');
		}
		out.write(localName);
		out.write("=\""); //$NON-NLS-1$
		if (value != null) {
			attrEncode(value);
		}
		out.write("\""); //$NON-NLS-1$
	}

	private void attrEncode(String value) {
		if (value == null) {
			return;
		}
		printEscaped(out, value, true);
	}

	private void encode(String text) {
		if (text == null) {
			return;
		}
		printEscaped(out, text, false);
	}

	@Override
	public void writeCData(String data) {
		closeElement();
		out.write("<![CDATA["); //$NON-NLS-1$
		out.write(data);
		out.write("]]>"); //$NON-NLS-1$
	}

	@Override
	public void writeCharacters(String text) {
		closeElement();
		// It is essential to use the same line terminator throughout.
		// Otherwise, SVN throws an error during commit.
		//
		// Note: Some unusual line terminator characters:
		//       \u0085 is next-line character
		//       \u2028 is line-separator character
		//       \u2029 is paragraph-separator character
		String normalized = text.replaceAll(
				"(\r\n|\n|\r|\u0085|\u2028|\u2029)",
				STANDARD_LINE_TERMINATOR
				);
		encode(normalized);
	}

	public void writeCharactersUnescaped(String text) {
		closeElement();
		out.print(text);
	}

	@Override
	public void writeLiteral(String literal) {
		writeCharactersUnescaped(literal);
	}

	@Override
	public void writeCharacters(char[] text, int start, int len) {
		closeElement();
		encode(new String(text, start, len));
	}

	@Override
	public void writeComment(String data) {
		closeElement();
		out.write("<!-- "); //$NON-NLS-1$
		out.write(data);
		out.write(" -->"); //$NON-NLS-1$
	}

	@Override
	public void writeDTD(String dtd) {
		String normalized = dtd.replaceAll(
				"(\r\n|\n|\r|\u0085|\u2028|\u2029)",
				STANDARD_LINE_TERMINATOR
				);
		out.write(normalized);
	}

	@Override
	public void writeDefaultNamespace(String namespaceURI) {
		writeAttribute("xmlns", namespaceURI); //$NON-NLS-1$
	}

	private void closeElement() {
		if (inEmptyElement) {
			out.write("/>"); //$NON-NLS-1$
			inEmptyElement = false;
		} else if (inStartElement) {
			out.write(">"); //$NON-NLS-1$
			inStartElement = false;
		}
	}

	@Override
	public void writeEmptyElement(String localName) {
		closeElement();
		inEmptyElement = true;
		out.write('<');
		out.write(localName);
	}

	@Override
	public void writeEmptyElement(String namespaceURI, String localName) {
		closeElement();
		inEmptyElement = true;
		String prefix = uriToPrefix.get(namespaceURI);
		out.write('<');
		if (prefix != null && prefix.length() > 0) {
			out.write(prefix);
			out.write(':');
		}
		out.write(localName);
	}

	@Override
	public void writeEmptyElement(String prefix, String localName, String namespaceURI) {
		closeElement();
		inEmptyElement = true;
		out.write('<');
		if (prefix != null && prefix.length() > 0) {
			out.write(prefix);
			out.write(':');
		}
		out.write(localName);
	}

	@Override
	public void writeEndDocument() {
		if (!elements.isEmpty()) {
			throw new IllegalStateException(elements.size() + " elements not closed"); //$NON-NLS-1$
		}
	}

	@Override
	public void writeEndElement() {
		closeElement();
		if (elements.isEmpty()) {
			throw new IllegalStateException();
		}
		String name = elements.pop();
		out.write('<');
		out.write('/');
		out.write(name);
		out.write('>');
	}

	@Override
	public void writeEntityRef(String name) {
		closeElement();
		out.write('&');
		out.write(name);
		out.write(';');
	}

	@Override
	public void writeNamespace(String prefix, String namespaceURI) {
		if (prefix == null || prefix.length() == 0) {
			writeAttribute("xmlns", namespaceURI); //$NON-NLS-1$
		} else {
			writeAttribute("xmlns:" + prefix, namespaceURI); //$NON-NLS-1$
		}
	}

	@Override
	public void writeProcessingInstruction(String target) {
		closeElement();
		// Technically, we ought to check that 'target' does not match to [Xx][Mm][Ll]
		// but we are unlikely to encounter this in practice.
		out.write('<');
		out.write('?');
		out.write(target);
		out.write('?');
		out.write('>');
	}

	@Override
	public void writeProcessingInstruction(String target, String data) {
		closeElement();
		// Technically, we ought to check that 'target' does not match to [Xx][Mm][Ll]
		// but we are unlikely to encounter this in practice.
		out.write('<');
		out.write('?');
		out.write(target);
		out.write(' ');
		out.write(data);
		out.write('?');
		out.write('>');
	}

	@Override
	public void writeStartDocument() {
		out.write(processXmlHeader("<?xml version='1.0' ?>")); //$NON-NLS-1$
	}

	@Override
	public void writeStartDocument(String version) {
		out.write(processXmlHeader("<?xml version='" + version + "' ?>")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public void writeStartDocument(String encoding, String version) {
		out.write(processXmlHeader("<?xml version='" + version + "' encoding='" + encoding + "' ?>")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public void writeStartElement(String localName) {
		closeElement();
		inStartElement = true;
		elements.push(localName);
		out.write('<');
		out.write(localName);
	}

	@Override
	public void writeStartElement(String namespaceURI, String localName) {
		closeElement();
		inStartElement = true;
		String prefix = uriToPrefix.get(namespaceURI);
		out.write('<');
		if (prefix != null && prefix.length() > 0) {
			out.write(prefix);
			out.write(':');
			elements.push(prefix + ':' + localName);
		} else {
			elements.push(localName);
		}
		out.write(localName);
	}

	@Override
	public void writeStartElement(String prefix, String localName, String namespaceURI) {
		closeElement();
		inStartElement = true;
		elements.push(localName);
		out.write('<');
		if (prefix != null && prefix.length() > 0) {
			out.write(prefix);
			out.write(':');
		}
		out.write(localName);
	}

	public char getXmlHederQuoteChar() {
		return xmlHederQuoteChar;
	}

	public void setXmlHederQuoteChar(char xmlHederQuoteChar) {
		this.xmlHederQuoteChar = xmlHederQuoteChar;
	}

	private String processXmlHeader(String header) {
		return xmlHederQuoteChar == '\'' ? header : header.replace('\'', xmlHederQuoteChar);
	}

	private static void printEscaped(PrintWriter writer, CharSequence s, boolean attribute) {
		int length = s.length();

		try {
			char previous_ch = 0;
			for (int x = 0; x < length; ++x) {
				char ch = s.charAt(x);
				if (ch == '&' && previous_ch != '\\') {
					// Tunnel text entities into XML
					// Look ahead in s to see if you can match an entity, &\w+;
					char ch_in_entity = 0;
					int w;
					for (w=1; x+w < length; ++w) {
						ch_in_entity = s.charAt(x+w);
						if (!Character.isLetter(ch_in_entity)) {
							break;
						}
					}
					if ((ch_in_entity == ';') && (w>1)) {
						writer.write(s.subSequence(x, x+w+1).toString());
						x += w+1;
						continue;
					}
				}
				printEscaped(writer, ch, attribute);
				previous_ch = ch;
			}
		} catch (IOException ioe) {
			throw new IllegalStateException();
		}
	}

	/**
	 * Print an XML character in its escaped form.
	 * 
	 * @param writer
	 *            The writer to which the character should be printed.
	 * @param ch
	 *            the character to print.
	 * 
	 * @throws IOException
	 */
	private static void printEscaped(PrintWriter writer, int ch, boolean attribute) throws IOException {

		String ref = getEntityRef(ch, attribute);
		if (ref != null) {
			writer.write('&');
			writer.write(ref);
			writer.write(';');
		} else if (ch == '\r' || ch == 0x0085 || ch == 0x2028) {
			printHex(writer, ch);
		} else if ((ch >= ' ' && ch != 160 && isUtf8Printable((char) ch) && XML11Char.isXML11ValidLiteral(ch))
				|| ch == '\t' || ch == '\n' || ch == '\r') {
			writer.write((char) ch);
		} else {
			printHex(writer, ch);
		}
	}

	/**
	 * Escapes chars
	 */
	final static void printHex(PrintWriter writer, int ch) throws IOException {
		writer.write("&#x"); //$NON-NLS-1$
		writer.write(Integer.toHexString(ch));
		writer.write(';');
	}

	protected static String getEntityRef(int ch, boolean attribute) {
		// Encode special XML characters into the equivalent character
		// references.
		// These five are defined by default for all XML documents.
		switch (ch) {
		case '<':
			return "lt"; //$NON-NLS-1$

			// no need to encode '>'.

		case '"':
			if (attribute) {
				return "quot"; //$NON-NLS-1$
			}
			break;
		case '&':
			return "amp"; //$NON-NLS-1$
		case 0x2013:
			return "ndash";
		case 0x2014:
			return "mdash";
		case 0x00A9:
			return "copy";
		case 0x2122:
			return "trade";
		case 0x00AE:
			return "reg";

			// WARN: there is no need to encode apostrophe, and doing so has an
			// adverse
			// effect on XHTML documents containing javascript with some browsers.
			// case '\'':
			// return "apos";
		}
		return null;
	}

	protected static boolean isUtf8Printable(char ch) {
		// fall-back method here.
		if ((ch >= ' ' && ch <= 0x10FFFF && ch != 0xF7) || ch == '\n' || ch == '\r' || ch == '\t') {
			// If the character is not printable, print as character reference.
			// Non printables are below ASCII space but not tab or line
			// terminator, ASCII delete, or above a certain Unicode threshold.
			return true;
		}

		return false;
	}

}
