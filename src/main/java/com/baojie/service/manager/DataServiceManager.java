package com.baojie.service.manager;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.baojie.jpa.entity.LoopTable;
import com.baojie.service.ServiceImpl;

@Service
public class DataServiceManager {

	@Autowired
	public ServiceImpl service;

	public Page<LoopTable> findLoopPageByLevel(final Specification<LoopTable> spec, final Pageable pageable) {
		return service.findPageByLevel(spec, pageable);
	}
	
	public void saveLoopTableRecord(final LoopTable loopTable){
		service.saveLoopTableRecord(loopTable);
	}
	
	public void deleteLoopRecordByUserId(final Long userId){
		service.deleteLoopTableRecordByUserId(userId);
	}
	
	public void updateLoopRecordByUserId(final Integer level,final Long userId){
		service.updateLoopTableRecordLevel(level, userId);
	}
	
	public LoopTable findLoopRecordByUserId(final Long userId){
		return service.findLoopTableRecordByUserId(userId);
	}

	public ServiceImpl getService() {
		return service;
	}

	public void setService(ServiceImpl service) {
		this.service = service;
	}
	
}
