package com.baojie.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.baojie.jpa.entity.LoopTable;
import com.baojie.jpa.repos.LoopTableRepository;



@Service
public class LoopTableService {

	@Autowired
	private LoopTableRepository loopTableRepository;

	public LoopTableService() {

	}

	public Page<LoopTable> findAllByLevel(final Specification<LoopTable> spec, final Pageable pageable) {
		return loopTableRepository.findAll(spec, pageable);
	}

	public LoopTable findRecordByUserId(final Long userId){
		return loopTableRepository.findRecordByUserId(userId);
	}
	
	public void saveRecord(final LoopTable loopTable){
		loopTableRepository.save(loopTable);
	}
	
	public void updateRecordLevel(final Integer level, final Long userId){
		loopTableRepository.updateLoopLevel(level, userId);
	}
	
	public List<LoopTable> deleteLoopTableRecordByUserId(final  Long userId){
		return loopTableRepository.deleteLoopTableRecordByUserId(userId);
	}
	
	public LoopTableRepository getLoopTableRepository() {
		return loopTableRepository;
	}

	public void setLoopTableRepository(LoopTableRepository loopTableRepository) {
		this.loopTableRepository = loopTableRepository;
	}

}
