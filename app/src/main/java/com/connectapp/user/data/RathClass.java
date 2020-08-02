package com.connectapp.user.data;

import java.io.Serializable;

public class RathClass implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3393879155758982135L;

	private String rathName;

	private String rathCode;

	private String bhaag;


	public String getRathName() {
		return rathName;
	}

	public void setRathName(String rathName) {
		this.rathName = rathName;
	}

	public String getRathCode() {
		return rathCode;
	}

	public void setRathCode(String rathCode) {
		this.rathCode = rathCode;
	}

	public String getBhaag() {
		return bhaag;
	}

	public void setBhaag(String bhaag) {
		this.bhaag = bhaag;
	}

}
