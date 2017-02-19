package com.baojie.jpa.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_loop_table")
public class LoopTable implements Serializable {
	private static final long serialVersionUID = 2017021518254455555L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long userId;

	private Integer loopLevel;

	public Integer getLoopLevel() {
		return loopLevel;
	}

	public void setLoopLevel(Integer loopLevel) {
		this.loopLevel = loopLevel;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "LoopTable [id=" + id + ", loopLevel=" + loopLevel + ", userId=" + userId + "]";
	}

}
