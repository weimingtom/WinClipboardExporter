package com.iteye.weimingtom.wce.model;

public class ContactGroupInfo {
	private String groupName;
	private int indexStart = -1;
	private int indexEnd = -1;
	
	public int getSize() {
		return indexEnd - indexStart + 1;
	}
	
	public int getIndex(int position) {
		return indexStart + position;
	}
	
	public ContactGroupInfo() {
		
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public int getIndexStart() {
		return indexStart;
	}

	public void setIndexStart(int indexStart) {
		this.indexStart = indexStart;
	}

	public int getIndexEnd() {
		return indexEnd;
	}

	public void setIndexEnd(int indexEnd) {
		this.indexEnd = indexEnd;
	}
}
