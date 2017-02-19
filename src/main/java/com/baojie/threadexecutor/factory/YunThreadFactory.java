package com.baojie.threadexecutor.factory;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baojie.threadexecutor.uncaught.YunThreadUncaughtException;


public class YunThreadFactory implements ThreadFactory {

	private static final UncaughtExceptionHandler UNCAUGHT_EXCEPTION = new YunThreadUncaughtException();
	private static final Logger LOG = LoggerFactory.getLogger(YunThreadFactory.class);
	private static final AtomicInteger POOLNUMBER = new AtomicInteger(1);
	private static final int NO_THREAD_PRIORITY = Thread.NORM_PRIORITY;
	private final AtomicLong threadNumber = new AtomicLong(1);
	private final String factoryName;
	private final int threadPriority;
	private final ThreadGroup group;
	private final String namePrefix;
	private final boolean isDaemon;

	public static YunThreadFactory create(final String name) {
		return new YunThreadFactory(name, false, NO_THREAD_PRIORITY);
	}

	public static YunThreadFactory create(final String name, final boolean isDaemon) {
		return new YunThreadFactory(name, isDaemon, NO_THREAD_PRIORITY);
	}

	public static YunThreadFactory create(final String name, final int threadPriority) {
		return new YunThreadFactory(name, false, threadPriority);
	}
	
	public static YunThreadFactory create(final String name, final boolean isDaemon, final int threadPriority) {
		return new YunThreadFactory(name, isDaemon, threadPriority);
	}

	private YunThreadFactory(final String name, final boolean isDaemon, final int threadPriority) {
		this.group = getThreadGroup();
		this.factoryName = name;
		this.isDaemon = isDaemon;
		this.threadPriority = threadPriority;
		this.namePrefix = factoryName + "-" + POOLNUMBER.getAndIncrement() + "-thread-";
	}

	private ThreadGroup getThreadGroup() {
		final SecurityManager sm = getSecurityManager();
		final ThreadGroup threadGroup = innerThreadGroup(sm);
		return threadGroup;
	}

	private SecurityManager getSecurityManager() {
		SecurityManager sm = null;
		sm = System.getSecurityManager();
		if (null == sm) {
			LOG.debug("SecurityManager could be null.");
		}
		return sm;
	}

	private ThreadGroup innerThreadGroup(final SecurityManager sm) {
		ThreadGroup threadGroup = null;
		if (null != sm) {
			threadGroup = sm.getThreadGroup();
		} else {
			threadGroup = Thread.currentThread().getThreadGroup();
		}
		if (null == threadGroup) {
			LOG.error("ThreadGroup must not be null.");
			throw new NullPointerException("threadgroup must not be null.");
		}
		return threadGroup;
	}

	@Override
	public Thread newThread(final Runnable r) {
		final Thread thread = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
		setThreadProperties(thread);
		return thread;
	}

	private void setThreadProperties(final Thread thread) {
		setDaemon(thread);
		setThreadPriority(thread);
		thread.setUncaughtExceptionHandler(UNCAUGHT_EXCEPTION);
	}

	private void setDaemon(final Thread thread) {
		if (isDaemon == true) {
			thread.setDaemon(true);
		} else {
			if (thread.isDaemon()) {
				thread.setDaemon(false);
			}
		}
	}

	private void setThreadPriority(final Thread thread) {
		if (threadPriority == NO_THREAD_PRIORITY) {
			if (thread.getPriority() != Thread.NORM_PRIORITY) {
				thread.setPriority(Thread.NORM_PRIORITY);
			}
		} else {
			final int priority = checkThreadPriority();
			thread.setPriority(priority);
		}
	}

	private int checkThreadPriority() {
		if (threadPriority <= Thread.MIN_PRIORITY) {
			return Thread.MIN_PRIORITY;
		} else if (threadPriority >= Thread.MAX_PRIORITY) {
			return Thread.MAX_PRIORITY;
		} else {
			return threadPriority;
		}
	}

}
