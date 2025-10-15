package com.smat.ins.model.entity;

import org.hibernate.envers.Audited;

/**
 * JobPositionHierarchy entity. @author MyEclipse Persistence Tools
 */

@Audited
public class JobPositionHierarchy implements java.io.Serializable {

	// Fields

	private Long id;
	private HierarchyType hierarchyType;
	private JobPosition jobPosition;
	private Integer ranking;

	// Constructors

	/** default constructor */
	public JobPositionHierarchy() {
	}

	/** full constructor */
	public JobPositionHierarchy(HierarchyType hierarchyType, JobPosition jobPosition, Integer ranking) {
		this.hierarchyType = hierarchyType;
		this.jobPosition = jobPosition;
		this.ranking = ranking;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public HierarchyType getHierarchyType() {
		return this.hierarchyType;
	}

	public void setHierarchyType(HierarchyType hierarchyType) {
		this.hierarchyType = hierarchyType;
	}

	public JobPosition getJobPosition() {
		return this.jobPosition;
	}

	public void setJobPosition(JobPosition jobPosition) {
		this.jobPosition = jobPosition;
	}

	public Integer getRanking() {
		return this.ranking;
	}

	public void setRanking(Integer ranking) {
		this.ranking = ranking;
	}

}