package com.baojie.worker.looptable;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baojie.jpa.entity.LoopTable;

public class LoopBusinessRunner implements Runnable {
	
	private static Logger log = LoggerFactory.getLogger(LoopBusinessRunner.class);
	private final AtomicBoolean turnOnBusinessRunner;
	private final LinkedBlockingQueue<LoopTable> loopTableRecords;

	private LoopBusinessRunner(final LinkedBlockingQueue<LoopTable> loopTableRecords,
			final AtomicBoolean turnOnBusinessRunner) {
		this.loopTableRecords = loopTableRecords;
		this.turnOnBusinessRunner = turnOnBusinessRunner;
	}

	public static LoopBusinessRunner create(final LinkedBlockingQueue<LoopTable> loopTableRecords, final AtomicBoolean turnOnBusinessRunner) {
		return new LoopBusinessRunner(loopTableRecords, turnOnBusinessRunner);
	}

	@Override
	public void run() {
		LoopTable loopTable = null;
		retry: while (turnOnBusinessRunner.get()) {
			loopTable = getFromQueue();
			if (null != loopTable) {
				//业务逻辑
				//System.out.println(loopTable);
			} else {
				continue retry;
			}
		}
	}

	private LoopTable getFromQueue() {
		LoopTable loopTable = null;
		try {
			loopTable = loopTableRecords.take();
		} catch (InterruptedException e) {
			loopTable = null;
			e.printStackTrace();
			log.error(e.toString());
		} catch (Exception e) {
			loopTable = null;
			e.printStackTrace();
			log.error(e.toString());
		} catch (Throwable e) {
			loopTable = null;
			e.printStackTrace();
			log.error(e.toString());
		}
		return loopTable;
	}

}
