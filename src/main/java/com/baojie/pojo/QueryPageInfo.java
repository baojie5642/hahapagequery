package com.baojie.pojo;

import java.util.concurrent.atomic.AtomicInteger;

public class QueryPageInfo {

	private final AtomicInteger pageNum;
	private final Integer pageSize;
	private final Integer fitColnumValue; 
	private final String fitColnumName; 
	
	private QueryPageInfo (final AtomicInteger pageNum,final Integer pageSize,final Integer fitColnumValue,
	final String fitColnumName){
		this.pageNum=pageNum;
		this.pageSize=pageSize;
		this.fitColnumValue=fitColnumValue;
		this.fitColnumName=fitColnumName;
	}
	
	public static QueryPageInfo create(final AtomicInteger pageNum,final Integer pageSize,final Integer fitColnumValue,
			final String fitColnumName){
		return new QueryPageInfo(pageNum, pageSize,fitColnumValue,fitColnumName);
	}

	public AtomicInteger getPageNum() {
		return pageNum;
	}

	public int getPageSize() {
		return pageSize;
	}

	public Integer getFitColnumValue() {
		return fitColnumValue;
	}

	public String getFitColnumName() {
		return fitColnumName;
	}

}
