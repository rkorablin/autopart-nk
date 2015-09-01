package ru.greenworm.autopart.utils;

import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;

public class UriUtils {

	public static URIBuilder getBuilder(String uri) {
		try {
			return new URIBuilder(uri);
		} catch (URISyntaxException e) {
			return null;
		}

	}

}
