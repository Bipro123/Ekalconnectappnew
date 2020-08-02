package com.connectapp.user.data;

import java.io.Serializable;
import java.util.ArrayList;

public class KeywordsClass implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4023467015460275937L;
	private ArrayList<Keyword> keywords = new ArrayList<Keyword>();

	public ArrayList<Keyword> getKeywords() {
		return keywords;
	}

	public void setKeywords(ArrayList<Keyword> keywords) {
		this.keywords = keywords;
	}

}
