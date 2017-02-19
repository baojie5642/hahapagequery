package com.baojie.service.manager;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baojie.pojo.QueryPageInfo;

@Service
public class TaskManager {
	
	private static final int PAGE_QUERY_THREAD=8;
	
	public TaskManager() {

	}

	@Autowired
	private QueryManager queryManager;

	public void levelFourLoopPageQuery() {
		final int levelValue = 4;
		final String levelName = "loopLevel";
		final Semaphore loopSemaphore = initSemp(1);
		final QueryPageInfo loopTablePageInfo = QueryPageInfo.create(new AtomicInteger(1), 10000, levelValue,
				levelName);
		queryManager.pageQueryLoopTable(loopTablePageInfo, PAGE_QUERY_THREAD, loopSemaphore);
		acquireSem(loopSemaphore, 1);
	}

	public void levelThreeLoopPageQuery() {
		final int levelValue = 3;
		final String levelName = "loopLevel";
		final Semaphore loopSemaphore = initSemp(1);
		final QueryPageInfo loopTablePageInfo = QueryPageInfo.create(new AtomicInteger(1), 10000, levelValue,
				levelName);
		queryManager.pageQueryLoopTable(loopTablePageInfo, PAGE_QUERY_THREAD, loopSemaphore);
		acquireSem(loopSemaphore, 1);
	}

	public void levelTwoLoopPageQuery() {
		final int levelValue = 2;
		final String levelName = "loopLevel";
		final Semaphore loopSemaphore = initSemp(1);
		final QueryPageInfo loopTablePageInfo = QueryPageInfo.create(new AtomicInteger(1), 10000, levelValue,
				levelName);
		queryManager.pageQueryLoopTable(loopTablePageInfo, PAGE_QUERY_THREAD, loopSemaphore);
		acquireSem(loopSemaphore, 1);
	}

	public void levelOneLoopPageQuery() {
		final int levelValue = 1;
		final String levelName = "loopLevel";
		final Semaphore loopSemaphore = initSemp(1);
		final QueryPageInfo loopTablePageInfo = QueryPageInfo.create(new AtomicInteger(1), 10000, levelValue,
				levelName);
		queryManager.pageQueryLoopTable(loopTablePageInfo, PAGE_QUERY_THREAD, loopSemaphore);
		acquireSem(loopSemaphore, 1);
	}

	private Semaphore initSemp(final int permits) {
		final Semaphore semaphore = new Semaphore(permits);
		acquireSem(semaphore, permits);
		return semaphore;
	}

	private void acquireSem(final Semaphore semaphore, final int permits) {
		try {
			semaphore.acquire(permits);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public QueryManager getQueryManager() {
		return queryManager;
	}

	public void setQueryManager(QueryManager queryManager) {
		this.queryManager = queryManager;
	}

}
