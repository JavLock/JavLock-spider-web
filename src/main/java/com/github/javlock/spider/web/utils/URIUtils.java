package com.github.javlock.spider.web.utils;

import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class URIUtils {
	public static UnaryOperator<String> urlRecovery = string -> {

		boolean needCheck = false;

		if (string.contains("https:://") || string.contains("http:://")) {
			string = string.replaceFirst("::", ":");
			needCheck = true;
		}

		if (string.contains(":///")) {
			string = string.replaceFirst(":///", "://");
			needCheck = true;
		}
		if (string.contains("\u00a0")) {
			string = string.replaceFirst("\u00a0", "%C2%A0");
			needCheck = true;
		}
		if (string.contains("\u2009")) {
			string = string.replaceFirst("\u2009", "%E2%80%89");
			needCheck = true;
		}
		if (string.contains(" ")) {
			string = string.replaceFirst(" ", "%20");
			needCheck = true;
		}
		if (string.contains("\"")) {
			string = string.replaceFirst("\"", "%22");
			needCheck = true;
		}
		if (string.contains(">")) {
			string = string.replaceFirst(">", "%3E");
			needCheck = true;
		}
		if (string.contains("<")) {
			string = string.replaceFirst("<", "%3C");
			needCheck = true;
		}
		if (string.contains("|")) {
			string = string.replaceFirst("[|]", "%7C");
			needCheck = true;
		}
		if (needCheck) {
			string = URIUtils.urlRecovery.apply(string);
		}

		return string;
	};
	private static final String SESSIONKEY_STRING_1 = "PHPSES";
	private static final String SESSIONKEY_STRING_1_LC = "phpses";

	private static final String SESSIONLOCATION_REPLACE = "_{_}HERE{_}_";

	private static String getFullString(String sessionKey, String uri) {
		final String regex = sessionKey + "=[a-z0-9.]*";
		final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
		final Matcher matcher = pattern.matcher(uri);

		while (matcher.find()) {
			return matcher.group(0);
		}
		return null;
	}

	private static String getKeyByURL(String part, String uri) {
		String regex = "[a-zA-Z_]*" + part + "[a-zA-Z_]*";
		final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
		final Matcher matcher = pattern.matcher(uri);
		String group = null;
		while (matcher.find()) {
			group = matcher.group(0);
			return group;
		}
		return group;
	}

	private static String getSessionIDByUrl(String sessionFullString) {
		return StringUtils.substringAfter(sessionFullString, "=");
	}

	public static String removeSessions(boolean full, String uri) {
		String sessionKey = null;
		String sessionID;

		if (uri.contains(SESSIONKEY_STRING_1)) {
			sessionKey = getKeyByURL(SESSIONKEY_STRING_1, uri);
		} else if (uri.contains(SESSIONKEY_STRING_1_LC)) {
			sessionKey = getKeyByURL(SESSIONKEY_STRING_1_LC, uri);
		}
		if (sessionKey == null) {
			return uri;
		}
		String sessionFullString = getFullString(sessionKey, uri);

		if (full) {
			return uri.replaceAll(sessionFullString, "");
		} else {
			sessionID = getSessionIDByUrl(sessionFullString);
			return uri.replaceAll(sessionID, SESSIONLOCATION_REPLACE);

		}
	}

}
