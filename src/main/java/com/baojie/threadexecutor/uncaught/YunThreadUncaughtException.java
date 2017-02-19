package com.baojie.threadexecutor.uncaught;

import java.lang.Thread.UncaughtExceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baojie.util.CheckNull;

public class YunThreadUncaughtException implements UncaughtExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(YunThreadUncaughtException.class);

	public YunThreadUncaughtException() {

	}

	@Override
	public void uncaughtException(final Thread t, final Throwable e) {
		innerCheck(t, e);
		final String threadName = getThreadName(t);
		yunThreadInterrupted(t);
		log.error("thread : " + threadName + " occured UncaughtException and interrupted. Error info ：" + e.toString());
	}

	private void innerCheck(final Thread t, final Throwable e) {
		CheckNull.checkObjectNull(t);
		CheckNull.checkObjectNull(e);
	}

	private String getThreadName(final Thread thread) {
		String string = Thread.currentThread().getName();
		CheckNull.checkObjectNull(string);
		return string;
	}

	private void yunThreadInterrupted(final Thread t) {
		try {
			t.interrupt();
		} finally {
			alwaysInterrupt(t);
		}
	}

	private void alwaysInterrupt(final Thread t) {
		if (!t.isInterrupted()) {
			t.interrupt();
		}
		if (t.isAlive()) {
			t.interrupt();
		}
	}
}
