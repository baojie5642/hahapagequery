package com.baojie.worker.looptable;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import com.baojie.jpa.entity.LoopTable;
import com.baojie.pojo.QueryPageInfo;
import com.baojie.service.manager.DataServiceManager;


public class LoopPageRunner implements Runnable {
	private static Logger log = LoggerFactory.getLogger(LoopPageRunner.class);
	private final DataServiceManager dataServiceManager;
	private final ConcurrentLinkedQueue<Page<LoopTable>> loopTablePages;
	private final QueryPageInfo pageInfo;
	private final Semaphore semaphore;

	private LoopPageRunner(final DataServiceManager dataServiceManager, final ConcurrentLinkedQueue<Page<LoopTable>> loopTablePages,
			final QueryPageInfo pageInfo, final Semaphore semaphore) {
		this.dataServiceManager = dataServiceManager;
		this.loopTablePages = loopTablePages;
		this.pageInfo = pageInfo;
		this.semaphore = semaphore;
	}

	public static LoopPageRunner create(final DataServiceManager bizManager,
			final ConcurrentLinkedQueue<Page<LoopTable>> loopTablePages, final QueryPageInfo pageInfo,
			final Semaphore semaphore) {
		return new LoopPageRunner(bizManager, loopTablePages, pageInfo, semaphore);
	}

	@Override
	public void run() {
		final QueryPageInfo pageInfoInner = pageInfo;
		final int pageSize = pageInfoInner.getPageSize();
		final SpecificationImpl specificationImpl = buildSpecification(pageInfoInner);
		int pageNum = 0;
		PageRequest pageRequest = null;
		Page<LoopTable> page = null;
		retry: for (;;) {
			pageNum = getPageNum(pageInfoInner);
			pageRequest = createPageRequest(pageNum, pageSize);
			if (null == pageRequest) {
				semaphore.release(1);
				break retry;
			} else {
				page = queryPage(specificationImpl, pageRequest);
				if (null == page) {
					semaphore.release(1);
					break retry;
				} else {
					if (pageHasContent(page)) {
						loopTablePages.offer(page);
					} else {
						if (pageHasNext(page)) {
							continue retry;
						} else {
							semaphore.release(1);
							break retry;
						}
					}
				}
			}
		}
	}

	private SpecificationImpl buildSpecification(final QueryPageInfo pageInfo) {
		final Integer colnumValue = pageInfo.getFitColnumValue();
		final String colnumName = pageInfo.getFitColnumName();
		final SpecificationImpl specificationImpl = new SpecificationImpl(colnumValue, colnumName);
		return specificationImpl;
	}

	private int getPageNum(final QueryPageInfo loopTablePageInfo) {
		final AtomicInteger atomicInteger = loopTablePageInfo.getPageNum();
		final int pageNum = atomicInteger.getAndIncrement();
		final int realNum = pageNum - 1;
		return realNum;
	}

	private PageRequest createPageRequest(final int pageNum, final int pageSize) {
		PageRequest pageRequest = null;
		try {
			pageRequest = new PageRequest(pageNum, pageSize);
		} catch (Throwable throwable) {
			pageRequest = null;
			throwable.printStackTrace();
			log.error(throwable.toString());
		}
		return pageRequest;
	}

	private Page<LoopTable> queryPage(final SpecificationImpl specificationImpl, final PageRequest pageRequest) {
		Page<LoopTable> page = null;
		try {
			page = dataServiceManager.findLoopPageByLevel(specificationImpl, pageRequest);
		} catch (Throwable throwable) {
			page = null;
			throwable.printStackTrace();
			log.error(throwable.toString());
		}
		return page;
	}

	private boolean pageHasContent(final Page<LoopTable> page) {
		if (page.hasContent()) {
			return true;
		} else {
			return false;
		}
	}

	private boolean pageHasNext(final Page<LoopTable> page) {
		if (page.hasNext()) {
			return true;
		} else {
			return false;
		}
	}

	private final class SpecificationImpl implements Specification<LoopTable> {
		private final Integer fitColnumValue;
		private final String fitColnumName;

		public SpecificationImpl(final Integer fitColnumValue, final String fitColnumName) {
			this.fitColnumValue = fitColnumValue;
			this.fitColnumName = fitColnumName;
		}

		@Override
		public Predicate toPredicate(Root<LoopTable> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
			// root=query.from(LoopTable.class);
			final Path<Long> $id = root.get(fitColnumName);
			final Predicate _id = criteriaBuilder.equal($id, fitColnumValue);
			return criteriaBuilder.and(_id);
		}
	}
}
