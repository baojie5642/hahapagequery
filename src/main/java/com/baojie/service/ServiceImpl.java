package com.baojie.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.baojie.jpa.entity.LoopTable;

@Service
public class ServiceImpl {
	
	@Autowired
	private LoopTableService loopTableService;

	public ServiceImpl(){
		
	}
	
	public Page<LoopTable> findPageByLevel(final Specification<LoopTable> spec, final Pageable pageable) {
		return loopTableService.findAllByLevel(spec, pageable);
	}
	
	
	public LoopTable findLoopTableRecordByUserId(final Long userId){
		return loopTableService.findRecordByUserId(userId);
	}
	
	public void saveLoopTableRecord(final LoopTable loopTable){
		loopTableService.saveRecord(loopTable);
	}
	
	public void deleteLoopTableRecordByUserId(final Long userId){
		loopTableService.deleteLoopTableRecordByUserId(userId);
	}
	
	public void updateLoopTableRecordLevel(final Integer level,final Long userId){
		loopTableService.updateRecordLevel(level, userId);
	}
	
	public LoopTableService getLoopTableService() {
		return loopTableService;
	}

	public void setLoopTableService(LoopTableService loopTableService) {
		this.loopTableService = loopTableService;
	}

}
