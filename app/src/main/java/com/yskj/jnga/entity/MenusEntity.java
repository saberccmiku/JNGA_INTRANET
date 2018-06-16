package com.yskj.jnga.entity;

import java.io.Serializable;

import android.content.Intent;

public class MenusEntity {


	private String id;
	private String menuTitle;
	private int drawableId;
	private Intent toIntent;
	private int indexInMenuList;
	private int count;

	public MenusEntity() {
	}

	public MenusEntity(String menuTitle, int drawableId, int count) {
		this.menuTitle = menuTitle;
		this.drawableId = drawableId;
		this.count = count;
	}

	public MenusEntity(String menuTitle, int drawableId, Intent toIntent) {
		this.menuTitle = menuTitle;
		this.drawableId = drawableId;
		this.toIntent = toIntent;
	}
	
	public MenusEntity(String id, String menuTitle, int drawableId, int count) {
		this.menuTitle = menuTitle;
		this.drawableId = drawableId;
		this.count = count;
	}

	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMenuTitle() {
		return menuTitle;
	}

	public void setMenuTitle(String menuTitle) {
		this.menuTitle = menuTitle;
	}

	public int getDrawableId() {
		return drawableId;
	}

	public void setDrawableId(int drawableId) {
		this.drawableId = drawableId;
	}

	public Intent getToIntent() {
		return toIntent;
	}

	public void setToIntent(Intent toIntent) {
		this.toIntent = toIntent;
	}

	public int getIndexInMenuList() {
		return indexInMenuList;
	}

	public void setIndexInMenuList(int indexInMenuList) {
		this.indexInMenuList = indexInMenuList;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "MenusEntity [id=" + id + ", menuTitle=" + menuTitle + ", drawableId=" + drawableId + ", toIntent="
				+ toIntent + ", indexInMenuList=" + indexInMenuList + ", count=" + count + "]";
	}
	

}
