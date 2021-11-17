package com.github.javlock.spider.web.uritls;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlUtils {

	private static final String REGEX1 = ":\\/\\/([\\w\\d.]*)";

	/*
	 * private static String domainPhase1(String url) throws NullPointerException,
	 * IllegalArgumentException {
	 * 
	 * }
	 */

	public static String getDomainFromUrl(String url) throws NullPointerException, IllegalArgumentException {
		if (url == null) {
			throw new NullPointerException("URL==null");
		}
		if (url.trim().isEmpty()) {
			throw new IllegalArgumentException("URL");
		}

		final Pattern pattern = Pattern.compile(REGEX1, Pattern.MULTILINE);
		final Matcher matcher = pattern.matcher(url);
		String group = null;
		while (matcher.find()) {
			group = matcher.group(1);
			if (group == null) {
				throw new NullPointerException("gr==null");
			}
			if (group.trim().isEmpty()) {
				throw new IllegalArgumentException(
						"group.trim().isEmpty() for thread:[" + Thread.currentThread() + "] and url[" + url + "]");
			}
		}
		return group;
	}

	private UrlUtils() {
	}

}
