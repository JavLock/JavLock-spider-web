package com.github.javlock.spider.web;

import java.net.URISyntaxException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.javlock.spider.web.uritls.UrlUtils;

import lombok.Getter;

public class FilterEngine {
	private @Getter CopyOnWriteArrayList<String> allowedDomain = new CopyOnWriteArrayList<>();
	private @Getter CopyOnWriteArrayList<String> forbiddenDomain = new CopyOnWriteArrayList<>();

	private boolean allowByDomain(String url) throws URISyntaxException {
		String domain = getDomainByURI(url);
		for (String regEx : allowedDomain) {
			final Pattern pattern = Pattern.compile(regEx, Pattern.MULTILINE);
			if (domain == null) {
				throw new IllegalArgumentException("error on DOMAIN:[" + domain + "] for URI:[" + url + "]");
			}
			final Matcher matcher = pattern.matcher(domain);
			while (matcher.find()) {
				String aaaa = matcher.group();
				if ((aaaa.equals(domain))) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean check(String url) throws URISyntaxException {
		return allowByDomain(url);
	}

	private String getDomainByURI(String url) throws NullPointerException, IllegalArgumentException {
		return UrlUtils.getDomainFromUrl(url);
	}

}
