package com.connectapp.user.data;

import java.io.Serializable;

public class ImageClass implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3L;

	private boolean isPosted = false;
	private String base64value = "";
	private String imageType = "";
	private boolean isSelected = false;
	private int imageCount=0;

	public int getImageCount() {
		return imageCount;
	}

	public void setImageCount(int imageCount) {
		this.imageCount = imageCount;
	}

	public void setPosted(boolean isPosted) {
		this.isPosted = isPosted;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public boolean getIsPosted() {
		return isPosted;
	}

	public void setIsPosted(boolean isPosted) {
		this.isPosted = isPosted;
	}

	public String getBase64value() {
		return base64value;
	}

	public void setBase64value(String base64value) {
		this.base64value = base64value;
	}

	public String getImageType() {
		return imageType;
	}

	public void setImageType(String imageType) {
		this.imageType = imageType;
	}

	public boolean getIsSelected() {
		return isSelected;
	}

	public void setIsSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

}
