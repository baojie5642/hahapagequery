package com.baojie.threadexecutor.pool;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YunScheduledThreadPool extends ScheduledThreadPoolExecutor {
	
	private static final Logger log = LoggerFactory.getLogger(YunRejectedThreadPool.class);

	public YunScheduledThreadPool(int corePoolSize) {
		super(corePoolSize);
	}

	public YunScheduledThreadPool(int corePoolSize, ThreadFactory threadFactory) {
		super(corePoolSize, threadFactory);
	}

	public YunScheduledThreadPool(int corePoolSize, RejectedExecutionHandler handler) {
		super(corePoolSize, handler);
	}

	public YunScheduledThreadPool(int corePoolSize, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
		super(corePoolSize, threadFactory, handler);
	}

	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		
	}

	@Override
	protected void afterExecute(final Runnable runnable, final Throwable throwable) {
		final String threadName = Thread.currentThread().getName();
		try {
			if (null == runnable) {
				log.error("Thread name : " + threadName
						+ ", Runnable is null,this case should never happen in threadpool.");
				throw new NullPointerException("Thread name : " + threadName
						+ ", Runnable is null,this case should never happen in threadpool.");
			}
			dealWithRunnableIfOccurException(runnable, threadName);
		} finally {
			handleException(throwable, threadName);
		}
	}

	private void dealWithRunnableIfOccurException(final Runnable runnable, final String threadName) {
		FutureTask<?> futureTask = null;
		if (runnable instanceof FutureTask<?>) {
			futureTask = (FutureTask<?>) runnable;
			dealFutureTask(futureTask, threadName);
		} else if (runnable instanceof RunnableFuture<?>) {
			log.debug("runnable just instanceof RunnableFuture.");
		} else {
			log.debug("runnable just runnable.");
		}
	}

	private void dealFutureTask(final FutureTask<?> futureTask, final String threadName) {
		Object object = null;
		if (futureTask.isDone()) {
			object = getError(futureTask, threadName);
			if (null == object) {
				return;
			} else {
				handleThrowable(object, threadName);
			}
		} else {
			log.error("futureTask has not been done, this should never be happen.");
			throw new IllegalStateException("futureTask has not been done, this should never be happen.");
		}
	}

	private Object getError(final FutureTask<?> futureTask, final String threadName) {
		Object object = null;
		try {
			object = futureTask.get(100, TimeUnit.MILLISECONDS);
		} catch (ExecutionException e) {
			object = null;
			e.printStackTrace();
			log.error("Thread : " + threadName + ", has occur ExecutionException during call run method, error is : "
					+ e.toString() + ".");
		} catch (CancellationException e) {
			object = null;
			e.printStackTrace();
			log.error("Thread : " + threadName + ", has occur CancellationException during call run method, error is : "
					+ e.toString() + ".");
		} catch (InterruptedException e) {
			object = null;
			e.printStackTrace();
			log.error("Thread : " + threadName
					+ ", has occur InterruptedException when call future.get method which should be never happen, error is : "
					+ e.getCause().toString() + ".");
		} catch (TimeoutException e) {
			object = null;
			e.printStackTrace();
			log.error("Thread : " + threadName
					+ ", has occur TimeoutException when call future.get method which should be never happen, error is : "
					+ e.getCause().toString() + ".");
		}
		return object;
	}

	private void handleThrowable(final Object object, final String threadName) {
		Throwable throwable = null;
		if (object instanceof Throwable) {
			throwable = (Throwable) object;
			throwable.printStackTrace();
			log.error("Thread name : " + threadName + ", error is : " + throwable.getCause().toString() + ".");
		} else {
			log.debug("Thread name : " + threadName + ", object is not null, object.tostring is : " + object.toString()
					+ ", object.getClass is : " + object.getClass().toString() + ".");
		}
	}

	private void handleException(final Throwable throwable, final String threadName) {
		if (null == throwable) {
			return;
		} else {
			log.error("throwable not null, has occured errors.");
			innerDeal(throwable, threadName);
		}
	}

	private void innerDeal(final Throwable throwable, final String threadName) {
		if (throwable instanceof RuntimeException) {
			innerPrint(throwable, threadName);
			throw new RuntimeException(throwable);
		} else if (throwable instanceof Error) {
			innerPrint(throwable, threadName);
			throw new Error(throwable);
		} else {
			innerPrint(throwable, threadName);
			throw new Error(throwable);
		}
	}

	private void innerPrint(final Throwable throwable, final String threadName) {
		throwable.printStackTrace();
		log.error("Thread name : " + threadName + ", throwable.getMessage() is : " + throwable.getMessage() + ".");
		log.error("Thread name : " + threadName + ", throwable.toString() is : " + throwable.toString() + ".");
	}

}
