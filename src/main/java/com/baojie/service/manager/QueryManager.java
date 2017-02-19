package com.baojie.service.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.baojie.jpa.entity.LoopTable;
import com.baojie.pojo.QueryPageInfo;
import com.baojie.threadexecutor.factory.YunThreadFactory;
import com.baojie.threadexecutor.pool.YunThreadPoolExecutor;
import com.baojie.threadexecutor.rejecthandler.YunThreadPoolRejectedHandler;
import com.baojie.worker.looptable.LoopBusinessRunner;
import com.baojie.worker.looptable.LoopPageRunner;
import com.baojie.worker.looptable.LoopRecordRunner;

@Service
public class QueryManager implements InitializingBean, DisposableBean {

	// 业务逻辑的消费者线程也可以通过spring实现配置,其他参数也可以，比如，查找条件，查找线程数等等
	private static final int BUSINESS_THREAD_NUM = 8;

	private LinkedBlockingQueue<LoopTable> loopTableRecordQueue;

	private final AtomicBoolean businessButton = new AtomicBoolean(true);

	private YunThreadPoolExecutor pageQueryThreadPool;

	private YunThreadPoolExecutor recordThreadPool;

	private YunThreadPoolExecutor loopBusinessThreadPool;

	private List<Future<?>> loopTableFutureList;

	@Autowired
	private DataServiceManager dataServiceManager;

	public QueryManager() {

	}

	public void pageQueryLoopTable(final QueryPageInfo loopTablePageInfo, final int threadNum,
			final Semaphore taskFinish) {
		if (!businessButton.get()) {
			return;
		}
		final Semaphore pageQueryFinish = initSema(threadNum);
		final Semaphore orderFinish = initSema(threadNum);
		final ConcurrentLinkedQueue<Page<LoopTable>> loopTablePages = new ConcurrentLinkedQueue<>();
		final AtomicBoolean pageDataHasAllGet = new AtomicBoolean(false);
		LoopPageRunner searchRunner = null;
		LoopRecordRunner orderRunner = null;
		for (int i = 0; i < threadNum; i++) {
			searchRunner = LoopPageRunner.create(dataServiceManager, loopTablePages, loopTablePageInfo,
					pageQueryFinish);
			pageQueryThreadPool.submit(searchRunner);
			orderRunner = LoopRecordRunner.create(orderFinish, pageDataHasAllGet, loopTablePages, loopTableRecordQueue);
			recordThreadPool.submit(orderRunner);
		}
		acquireSema(pageQueryFinish, threadNum);
		pageDataHasAllGet.set(true);
		acquireSema(orderFinish, threadNum);
		taskFinish.release(1);
	}

	private Semaphore initSema(final int acquireSize) {
		final Semaphore semaphore = new Semaphore(acquireSize);
		try {
			semaphore.acquire(acquireSize);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return semaphore;
	}

	private void acquireSema(final Semaphore semaphore, final int acquireSize) {
		try {
			semaphore.acquire(acquireSize);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public DataServiceManager getBizManager() {
		return dataServiceManager;
	}

	public void setBizManager(DataServiceManager dataServiceManager) {
		this.dataServiceManager = dataServiceManager;
	}

	@PostConstruct
	private void init() {
		pageQueryThreadPool = new YunThreadPoolExecutor(32, 128, 3, TimeUnit.MINUTES, new LinkedBlockingQueue<>(1024),
				YunThreadFactory.create("PageQueryThread"),
				YunThreadPoolRejectedHandler.create("PageQueryRejectedHandler"));
		recordThreadPool = new YunThreadPoolExecutor(32, 128, 3, TimeUnit.MINUTES, new LinkedBlockingQueue<>(1024),
				YunThreadFactory.create("RecordThread"), YunThreadPoolRejectedHandler.create("OrderRejectedHandler"));
		loopBusinessThreadPool = new YunThreadPoolExecutor(8, 32, 60, TimeUnit.MINUTES, new LinkedBlockingQueue<>(128),
				YunThreadFactory.create("LoopBusinessThread"),
				YunThreadPoolRejectedHandler.create("LoopBusinessRejectedHandler"));
		loopTableFutureList = new ArrayList<>();
		loopTableRecordQueue = new LinkedBlockingQueue<>(16384);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		initBusinessRunner();
	}

	private void initBusinessRunner() {
		checkNull();
		initLoop();
	}

	private void checkNull() {
		if (null == dataServiceManager) {
			throw new NullPointerException();
		}
		if (null == loopBusinessThreadPool) {
			throw new NullPointerException();
		}
		if (null == loopTableRecordQueue) {
			throw new NullPointerException();
		}
	}

	private void initLoop() {
		LoopBusinessRunner loopBusinessRunner = null;
		Future<?> future = null;
		for (int i = 0; i < BUSINESS_THREAD_NUM; i++) {
			loopBusinessRunner = LoopBusinessRunner.create(loopTableRecordQueue, businessButton);
			future = loopBusinessThreadPool.submit(loopBusinessRunner);
			loopTableFutureList.add(future);
		}
	}

	@PreDestroy
	private void destory() {
		businessButton.set(false);
		cancelFuture();
		shutDownPool();
		cleanQueue();
	}

	private void cancelFuture() {
		if (null != loopTableFutureList) {
			cancel(loopTableFutureList);
			loopTableFutureList.clear();
		}
	}

	private void cancel(final List<Future<?>> futureList) {
		final int listSize = futureList.size();
		Future<?> future = null;
		for (int i = 0; i < listSize; i++) {
			future = futureList.get(i);
			future.cancel(true);
		}
	}

	private void shutDownPool() {
		if (null != pageQueryThreadPool) {
			pageQueryThreadPool.purge();
			pageQueryThreadPool.shutdownNow();
		}
		if (null != recordThreadPool) {
			recordThreadPool.purge();
			recordThreadPool.shutdownNow();
		}
		if (null != loopBusinessThreadPool) {
			loopBusinessThreadPool.purge();
			loopBusinessThreadPool.shutdownNow();
		}
	}

	private void cleanQueue() {
		if (null != loopTableRecordQueue) {
			loopTableRecordQueue.clear();
		}
	}

	@Override
	public void destroy() throws Exception {

	}
}
