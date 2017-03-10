package com.baojie.worker.looptable;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

import com.baojie.jpa.entity.LoopTable;

public class LoopRecordRunner implements Runnable {
	private static Logger log = LoggerFactory.getLogger(LoopRecordRunner.class);
	private final Semaphore semaphore;
	private final AtomicBoolean pageDataHasAllGet;
	private final ConcurrentLinkedQueue<Page<LoopTable>> loopTablePages;
	private final LinkedBlockingQueue<LoopTable> loopTablesRecords;

	private LoopRecordRunner(final Semaphore semaphore, final AtomicBoolean pageDataHasAllGet,
			final ConcurrentLinkedQueue<Page<LoopTable>> loopTablePages,
			final LinkedBlockingQueue<LoopTable> loopTablesRecords) {
		this.semaphore = semaphore;
		this.pageDataHasAllGet = pageDataHasAllGet;
		this.loopTablePages = loopTablePages;
		this.loopTablesRecords = loopTablesRecords;
	}

	public static LoopRecordRunner create(final Semaphore semaphore, final AtomicBoolean pageDataHasAllGet,
			final ConcurrentLinkedQueue<Page<LoopTable>> loopTablePages,
			final LinkedBlockingQueue<LoopTable> loopTablesRecords) {
		return new LoopRecordRunner(semaphore, pageDataHasAllGet, loopTablePages, loopTablesRecords);
	}

	@Override
	public void run() {
		Page<LoopTable> page = null;
		retry: for (;;) {
			page = loopTablePages.poll();
			if (null == page) {
				if (pageDataHasAllGet.get()) {
					page = loopTablePages.poll();
					if (null == page) {
						semaphore.release(1);
						break retry;
					} else {
						if (page.hasContent()) {
							dealWithPage(page);// 处理page数据
						} else {
							continue retry;// 可以不用休息100毫秒，直接去队列中去取,因为这时所有page已经取完
						}
					}
				} else {
					waitForPage(100);//不能去掉，有可能造成cpu使用100%
					continue retry;// 可以休息100毫秒，因为这个时候可能真的没有数据了，或者数据查询出现了延迟
				}
			} else {
				if (page.hasContent()) {
					dealWithPage(page);// 处理page数据
				} else {
					continue retry;// 可以不用休息100毫秒，直接去队列中去取,因为这时所有page已经取完
				}
			}
		}
	}

	private void waitForPage(final int milliSeconds) {
		try {
			TimeUnit.MILLISECONDS.sleep(milliSeconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
			log.error(e.toString());
		}
	}

	private void dealWithPage(final Page<LoopTable> page) {
		final Iterator<LoopTable> iterable = getIterator(page);
		if(null==iterable){
			return;
		}
		LoopTable loopTable = null;
		while (iterable.hasNext()) {
			loopTable = iterable.next();
			if (null != loopTable) {
				addLoopTable(loopTable);
			}
		}
	}

	private Iterator<LoopTable> getIterator(final Page<LoopTable> page) {
		final Iterator<LoopTable> iterator = page.iterator();
		if (null == iterator) {
			log.error("iterator must not be null.");
		}
		return iterator;
	}

	private void addLoopTable(final LoopTable loopTable) {
		try {
			loopTablesRecords.put(loopTable);
		} catch (InterruptedException e) {
			e.printStackTrace();
			log.error(e.toString());
		}
	}

}
