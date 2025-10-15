package com.smat.ins.util.model;

public class Language {
	private String languaneCode;
	 private String languageName;

	 public Language(String languaneCode, String languageName) {
	  super();
	  this.languaneCode = languaneCode;
	  this.languageName = languageName;
	 }

	 public String getLanguaneCode() {
	  return languaneCode;
	 }

	 public void setLanguaneCode(String languaneCode) {
	  this.languaneCode = languaneCode;
	 }

	 public String getLanguageName() {
	  return languageName;
	 }

	 public void setLanguageName(String languageName) {
	  this.languageName = languageName;
	 }

	 @Override
	 public String toString() {
	  StringBuilder builder = new StringBuilder();
	  builder.append("Language [languaneCode=").append(languaneCode)
	    .append(", languageName=").append(languageName).append("]");
	  return builder.toString();
	 }
}
