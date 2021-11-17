package com.github.javlock.spider.web;

import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.github.javlock.spider.web.utils.URIUtils;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class WebSpider extends Thread {
	private static final String ABSHREF = "abs:href";
	private boolean alive = true;
	private @Getter @Setter int maxThreads = 1;

	private final ConcurrentHashMap<String, Thread> threadsMap = new ConcurrentHashMap<>();
	private final CopyOnWriteArrayList<String> uriNew = new CopyOnWriteArrayList<>();

	private final @Getter CopyOnWriteArrayList<String> uriParsed = new CopyOnWriteArrayList<>();

	private @Getter @Setter Proxy proxy;
	private final @Getter ExceptionHandler exceptionHandler = new ExceptionHandler();
	private final @Getter FilterEngine filterEngine = new FilterEngine();

	private final @Getter DataEngine dataEngine = new DataEngine();

	boolean fullReplace = true;

	public void appendNew(String where, String newUrlString) {
		try {
			if (newUrlString.isEmpty()) {
				return;
			}
			// data
			if (getDataEngine().checkForDataByUrlString(where, newUrlString)) {
				return;
			}
			// data
			// recovery
			String recoveredUrlString = URIUtils.urlRecovery.apply(newUrlString);
			if (recoveredUrlString == null) {
				return;
			}

			boolean notAllowed = getFilterEngine().check(recoveredUrlString);
			if (notAllowed) {
				getDataEngine().forbiddenByFilter(where, recoveredUrlString);

				return;
			}
			String urlWithOutSession = URIUtils.removeSessions(fullReplace, recoveredUrlString);
			if (!uriParsed.contains(urlWithOutSession) && !uriNew.contains(recoveredUrlString)) {
				uriNew.add(recoveredUrlString);
				// TODO SEND TO LISTENERS
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void execute(String uri) {
		Thread executeThread = new Thread((Runnable) () -> {
			int statusCode;
			try {
				Response resp = Jsoup.connect(uri).proxy(proxy).userAgent("Mozilla").execute();
				statusCode = resp.statusCode();

				Document doc = resp.parse();
				parsePage(uri, doc);
			} catch (org.jsoup.HttpStatusException e) {
				statusCode = e.getStatusCode();
			} catch (org.jsoup.UnsupportedMimeTypeException e) {
				getDataEngine().handleFilesURI(uri);

			} catch (javax.net.ssl.SSLException | java.net.SocketTimeoutException | java.net.SocketException e) {
				// IGNORE (NOW)
			} catch (java.lang.IllegalArgumentException | IOException e) {
				exceptionHandler.uncaughtException(Thread.currentThread(), e);
			}
			next(uri);

		}, uri);
		if (!threadsMap.containsKey(uri)) {
			threadsMap.putIfAbsent(uri, executeThread);
			executeThread.start();
		}

	}

	public void kill() {
		alive = false;
	}

	private void next(String uri) {
		String uriWithOutSessions = URIUtils.removeSessions(fullReplace, uri);

		if (uri.toLowerCase().contains("wp-")) {
			DataEngine.appendToNewFile("wordpress", uri);
		}
		uriParsed.add(uriWithOutSessions);
		uriNew.remove(uri);
		threadsMap.remove(uri);
	}

	private ArrayList<String> parseDocForLinksAsStringsAL(@NonNull Document doc) {
		ArrayList<String> answ = new ArrayList<>();
		Elements a = doc.select("a");
		for (Element element : a) {
			String aaa = null;
			aaa = element.absUrl(ABSHREF);

			if (!answ.contains(aaa)) {
				answ.add(aaa);
			}
		}
		Elements link = doc.select("link");
		for (Element element : link) {
			String linkUrl = null;
			linkUrl = element.absUrl(ABSHREF);
			if (!answ.contains(linkUrl)) {
				answ.add(linkUrl);
			}
		}

		Collections.sort(answ);
		return answ;
	}

	private ArrayList<String> parseDocForTextAsStringsAL(@NonNull String where, @NonNull Document doc) {
		ArrayList<String> answ = new ArrayList<>();
		Element body = doc.body();
		if (body == null) {
			return answ;
		}
		String text = body.text();
		// MAILS
		if (!where.toLowerCase().contains(".css") && text.contains("@")) {

			ArrayList<String> pa = DataEngine.parseTextForMails(text);
			for (String string : pa) {
				getDataEngine().handleMails(where, string);
			}
		}

		return answ;
	}

	private void parsePage(String where, Document doc) {
		ArrayList<String> recoveredLinkList = parseDocForLinksAsStringsAL(doc);
		for (String string : recoveredLinkList) {

			try {
				appendNew(where, string);
			} catch (Exception e) {
				exceptionHandler.uncaughtException(Thread.currentThread(), e);
			}
		}
		ArrayList<String> parsedFromGui = parseDocForTextAsStringsAL(where, doc);
		for (String string : parsedFromGui) {
			appendNew(where, string);
		}

	}

	@Override
	public void run() {
		Thread.currentThread().setName("WebSpider-" + System.currentTimeMillis());
		do {
			for (String uri : uriNew) {
				while (threadsMap.size() >= maxThreads) {
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						exceptionHandler.uncaughtException(Thread.currentThread(), e);
					}
				}
				if (!threadsMap.containsKey(uri)) {
					execute(uri);
				}
			}

		} while (alive);
	}

}
