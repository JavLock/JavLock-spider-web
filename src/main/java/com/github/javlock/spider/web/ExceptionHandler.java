package com.github.javlock.spider.web;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import lombok.Getter;
import lombok.Setter;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

	private @Getter @Setter File errorDir = new File("errors");

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		long time = System.currentTimeMillis();

		StringWriter errors = new StringWriter();
		e.printStackTrace(new PrintWriter(errors));

		// data
		String threadName = t.getName();
		String stackTrace = errors.toString();

		StringBuilder dataBuilder = new StringBuilder();
		dataBuilder.append("THREAD:[").append(threadName).append(']').append('\n');
		dataBuilder.append("StackTrace:[").append('\n').append(stackTrace).append(']').append('\n');

		byte[] data = dataBuilder.toString().getBytes(StandardCharsets.UTF_8);

		File dir = getErrorDir();
		if (!dir.exists()) {
			dir.mkdirs();
		}

		File errorFile = new File(dir, time + ".log");
		if (!errorFile.exists()) {
			try {
				errorFile.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		try {
			Files.write(errorFile.toPath(), data, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

}
