package ru.greenworm.autopart.utils;

import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public final class XmlUtils {

	private XmlUtils() {
	};

	public static Stream<Element> stream(NodeList nodeList) {
		return IntStream.range(0, nodeList.getLength()).mapToObj(i -> nodeList.item(i)).filter(n -> n instanceof Element).map(n -> (Element) n);
	}

	public static Optional<Element> firstChild(Element element, String tagName) {
		return stream(element.getChildNodes()).filter(n -> n.getNodeName().equals(tagName)).findFirst();
	}

	public static String firstChildText(Element element, String tagName) {
		Optional<Element> optional = firstChild(element, tagName);
		return optional.isPresent() ? optional.get().getFirstChild().getNodeValue() : null;
	}
}
