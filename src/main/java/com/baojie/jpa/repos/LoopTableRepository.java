package com.baojie.jpa.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.baojie.jpa.entity.LoopTable;


public interface LoopTableRepository extends JpaRepository<LoopTable, Long>, JpaSpecificationExecutor<LoopTable> {
	
	public LoopTable findRecordByUserId(final Long userId);
	
	@Modifying
	@Transactional
    @Query("delete from LoopTable loopt where loopt.userId = ?1")
	public List<LoopTable> deleteLoopTableRecordByUserId(final  Long userId);
	
	@Modifying
	@Transactional
	@Query(value = "update LoopTable loopt set loopt.loopLevel = ?1 where loopt.userId = ?2")
	public int updateLoopLevel(final Integer level, final Long userId);
	
}
