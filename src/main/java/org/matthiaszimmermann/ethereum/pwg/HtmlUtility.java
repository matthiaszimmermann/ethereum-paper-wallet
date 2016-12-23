package org.matthiaszimmermann.ethereum.pwg;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Base64;

public class HtmlUtility {
	public static final String NEWLINE = System.lineSeparator();

	public static final String HTML = "html";
	public static final String HEAD = "head";
	public static final String TITLE = "title";
	public static final String STYLE = "style";
	public static final String BODY = "body";
	public static final String DIV = "div";
	public static final String H2 = "h2";
	public static final String P = "p";
	public static final String FOOTER = "footer";
	
	public static void addTitle(StringBuffer buf, String title) {
		addElementWithContent(buf, TITLE, title, null);
	}

	public static void addStyles(StringBuffer buf, String... styles) {
		addOpenElementsWithClass(buf, null, STYLE);
		Arrays.stream(styles)
		.forEach(style -> {
			buf.append(style); 
			buf.append(NEWLINE); 
		});
		addCloseElements(buf, STYLE);
	}

	public static void addHeader2(StringBuffer buf, String title) {
		addElementWithContent(buf, H2, title, null);
	}

	public static void addParagraph(StringBuffer buf, String paragraph) {
		addParagraph(buf,paragraph, null);
	}

	public static void addParagraph(StringBuffer buf, String paragraph, String cssClass) {
		addElementWithContent(buf, P, paragraph, cssClass);
	}

	public static void addContent(StringBuffer buf, String content) {
		if(content == null || content.isEmpty()) {
			return;
		}

		buf.append(content);
	}

	public static void addElementWithContent(StringBuffer buf, String element, String content, String cssClass) {
		if(content == null || content.isEmpty()) {
			return;
		}

		if(element == null || element.isEmpty()) {
			throw new IllegalArgumentException("Tag of element to add must not be empty or null");
		}

		addOpenElementsWithClass(buf, cssClass, element);
		buf.append(content);
		addCloseElements(buf, element);
	}

	public static void addEncodedImage(StringBuffer buf, InputStream image, int height, String cssClass) {
		try {
			byte[] data = getBytesFromInputStream(image);
			addEncodedImage(buf, data, height, cssClass);

		} 
		catch (Exception e) {
			throw new RuntimeException("Failed to load content from file input stream:", e);
		}
	}

	private static byte[] getBytesFromInputStream(InputStream is) throws IOException {
		try (ByteArrayOutputStream os = new ByteArrayOutputStream();)
		{
			byte[] buffer = new byte[0xFFFF];

			for (int len; (len = is.read(buffer)) != -1;)
				os.write(buffer, 0, len);

			os.flush();

			return os.toByteArray();
		}
	}

	public static void addEncodedImage(StringBuffer buf, byte [] imageFile, int height, String cssClass) {
		String encodedFile = getEncodedBytes(imageFile);
		String encodedImage = String.format("<img class=\"%s\" src=\"data:image/png;base64,%s\">%n", cssClass, encodedFile, height);  
		buf.append(encodedImage);
	}

	public static void addOpenDiv(StringBuffer buf, String... classAttributes) {
		buf.append(String.format("<%s class=\"%s\">%n", DIV, String.join(" ", classAttributes)));
	}

	public static void addCloseDiv(StringBuffer buf) {
		addCloseElements(buf, DIV);
	}

	public static void addOpenFooter(StringBuffer buf, String... classAttributes) {
		buf.append(String.format("<%s class=\"%s\">%n", FOOTER, String.join(" ", classAttributes)));
	}

	public static void addCloseFooter(StringBuffer buf) {
		addCloseElements(buf, FOOTER);
	}
	
	public static void addOpenElements(StringBuffer buf, String... elements) {
		addOpenElementsWithClass(buf, null, elements);
	}

	private static void addOpenElementsWithClass(StringBuffer buf, String cssClass, String... elements) {
		Arrays.stream(elements).forEach(element -> buf.append(toOpenElement(element, cssClass))); 
	}

	public static void addCloseElements(StringBuffer buf, String... elements) {
		Arrays.stream(elements).forEach(element -> buf.append(toCloseElement(element)));
	}

	private static String getEncodedBytes(byte [] data) {
		return Base64.getEncoder().encodeToString(data);
	}

	private static String toOpenElement(String element, String cssClass) {
		if(cssClass == null || cssClass.isEmpty()) {
			return String.format("<%s>%n", element);
		}
		else {
			return String.format("<%s class=\"%s\">%n", element, cssClass);
		}
	}

	private static String toCloseElement(String element) {
		return String.format("</%s>%n", element);
	}
}
