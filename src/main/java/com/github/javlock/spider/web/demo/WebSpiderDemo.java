package com.github.javlock.spider.web.demo;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

import com.github.javlock.spider.web.FilterEngine;
import com.github.javlock.spider.web.WebSpider;

public class WebSpiderDemo {

	private static final String INPUT_LINKS = "INPUT_LINKS";
	private static final String INPUT_ALLOW = "INPUT_ALLOW";
	private static final File INPUT_ALLOWFILE = new File(INPUT_ALLOW);
	private static final File INPUT_LINKSFILE = new File(INPUT_LINKS);

	public static void main(String[] args) throws IOException {
		WebSpider webSpider = new WebSpider();
		// config
		webSpider.setMaxThreads(26);
		webSpider.setProxy(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 9050)));
		// config

		// FILTERs
		FilterEngine filterEngine = webSpider.getFilterEngine();
		if (!INPUT_ALLOWFILE.exists()) {
			Files.createFile(INPUT_ALLOWFILE.toPath());
		}
		List<String> allowedRegEx = Files.readAllLines(INPUT_ALLOWFILE.toPath());
		for (String regex : allowedRegEx) {
			filterEngine.getAllowedDomain().add(regex);
		}
		// FILTERs

		// input
		if (!INPUT_LINKSFILE.exists()) {
			Files.createFile(INPUT_LINKSFILE.toPath());
		}
		List<String> links = Files.readAllLines(INPUT_LINKSFILE.toPath());
		Collections.sort(links);
		for (String link : links) {
			webSpider.appendNew("ROOT", link);
		}

		// input

		// output
		// output

		// run
		webSpider.start();

	}

}
