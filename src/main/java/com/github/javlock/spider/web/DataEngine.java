package com.github.javlock.spider.web;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataEngine {

	public static void appendToNewFile(String fileName, String dataString) {
		File dataFile = new File(fileName);
		if (!dataFile.exists()) {
			try {
				dataFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			byte[] data = (dataString + '\n').getBytes(StandardCharsets.UTF_8);
			Files.write(dataFile.toPath(), data, StandardOpenOption.APPEND);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void handleFilesURI(String uri) {
		appendToNewFile("handleFilesURI", uri.toString());
	}

	public void handleMails(String where, String mailString) {
		String data = "MAIL[" + mailString + "] URL[" + where + "]";
		appendToNewFile("handleMails", data);
	}

	public void handleParseError(String uri) {
		appendToNewFile("handleParseError", uri);
	}

	public void handleStatusCode(int status, String uri) {
		appendToNewFile("handleStatusCode", "CODE:[" + status + "] URI[" + uri + "]");
	}
	// forbiddenByFilter

	public void forbiddenByFilter(String where, String what) {
		String data = "WHAT [" + what + "] WHERE[" + where + "]";
		appendToNewFile("forbiddenByFilter", data);
	}

	public boolean checkForDataByUrlString(String where, String newUrlString) {
		boolean ret = false;

		if (newUrlString.toLowerCase().startsWith("mailto:")) {
			boolean recoveryNeeded = true;
			for (String mail : parseTextForMails(newUrlString)) {
				this.handleMails(where, mail);
				recoveryNeeded = false;
			}
			if (recoveryNeeded) {
				this.handleMails(where, newUrlString);
			}
			ret = true;
		}

		if (ret) {
			System.err.println("[" + newUrlString + "] is DATA at [" + where + "]");
		}

		return ret;
	}

	public static ArrayList<String> parseTextForMails(String text) {
		ArrayList<String> answ = new ArrayList<>();
		final String regex = "[a-z0-9.A-Z-]{1,}\\@[a-zA-Z0-9.-]{1,}[a-zA-Z0-9-]{1,}";
		final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
		final Matcher matcher = pattern.matcher(text);

		while (matcher.find()) {
			String group = matcher.group(0);
			if (!answ.contains(group)) {
				answ.add(group);
			}
		}
		return answ;
	}
}
