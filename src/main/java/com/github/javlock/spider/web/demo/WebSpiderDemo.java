package com.github.javlock.spider.web.demo;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URISyntaxException;

import com.github.javlock.spider.web.FilterEngine;
import com.github.javlock.spider.web.WebSpider;

public class WebSpiderDemo {

	public static void main(String[] args) throws URISyntaxException {
		WebSpider webSpider = new WebSpider();
		// config
		webSpider.setMaxThreads(26);
		webSpider.setProxy(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 1050)));
		// config

		// FILTERs
		FilterEngine filterEngine = webSpider.getFilterEngine();
		filterEngine.getAllowedDomain().add(".*example.com\\b");
		// FILTERs

		// input
		webSpider.appendNew("ROOT", "https://example.com");
		// input

		// output
		// output

		// run
		webSpider.start();

	}

}
