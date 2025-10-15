package com.smat.ins.model.entity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;

import com.smat.ins.util.UtilityHelper;

/**
 * JobPosition entity. @author MyEclipse Persistence Tools
 */

@Audited
public class JobPosition implements java.io.Serializable {

	// Fields

	private Short id;
	private EffectType effectType;
	private String arabicName;
	private String englishName;
	private String code;
	private Short definedLevel;
	private Short lowSendLevel;
	private Short highSendLevel;
	private Set jobPositionHierarchies = new HashSet(0);
	private Set accessByJobPositions = new HashSet(0);
	private Set userAliases = new HashSet(0);

	// Constructors

	/** default constructor */
	public JobPosition() {
	}

	/** full constructor */
	public JobPosition(EffectType effectType, String arabicName, String englishName, String code, Short definedLevel,
			Short lowSendLevel, Short highSendLevel, Set jobPositionHierarchies, Set accessByJobPositions,
			Set userAliases) {
		this.effectType = effectType;
		this.arabicName = arabicName;
		this.englishName = englishName;
		this.code = code;
		this.definedLevel = definedLevel;
		this.lowSendLevel = lowSendLevel;
		this.highSendLevel = highSendLevel;
		this.jobPositionHierarchies = jobPositionHierarchies;
		this.accessByJobPositions = accessByJobPositions;
		this.userAliases = userAliases;
	}

	// Property accessors

	public Short getId() {
		return this.id;
	}

	public void setId(Short id) {
		this.id = id;
	}

	public EffectType getEffectType() {
		return this.effectType;
	}

	public void setEffectType(EffectType effectType) {
		this.effectType = effectType;
	}

	public String getArabicName() {
		return this.arabicName;
	}

	public void setArabicName(String arabicName) {
		this.arabicName = arabicName;
	}

	public String getEnglishName() {
		return this.englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Short getDefinedLevel() {
		return this.definedLevel;
	}

	public void setDefinedLevel(Short definedLevel) {
		this.definedLevel = definedLevel;
	}

	public Short getLowSendLevel() {
		return this.lowSendLevel;
	}

	public void setLowSendLevel(Short lowSendLevel) {
		this.lowSendLevel = lowSendLevel;
	}

	public Short getHighSendLevel() {
		return this.highSendLevel;
	}

	public void setHighSendLevel(Short highSendLevel) {
		this.highSendLevel = highSendLevel;
	}

	public Set getJobPositionHierarchies() {
		return this.jobPositionHierarchies;
	}

	public void setJobPositionHierarchies(Set jobPositionHierarchies) {
		this.jobPositionHierarchies = jobPositionHierarchies;
	}

	public Set getAccessByJobPositions() {
		return this.accessByJobPositions;
	}

	public void setAccessByJobPositions(Set accessByJobPositions) {
		this.accessByJobPositions = accessByJobPositions;
	}

	public Set getUserAliases() {
		return this.userAliases;
	}

	public void setUserAliases(Set userAliases) {
		this.userAliases = userAliases;
	}

	@Override
	public boolean equals(Object other) {

		return (other instanceof JobPosition) && (id != null) ? id.equals(((JobPosition) other).id) : (other == this);
	}

	@Override
	public int hashCode() {
		return (id != null) ? (this.getClass().hashCode() + id.hashCode()) : super.hashCode();
	}

	@Override
	public String toString() {
		return getName();
	}

	public String getName() {
		if (UtilityHelper.getLocale().getLanguage().contains("en"))
			return getEnglishName();
		else
			return getArabicName();
	}

}