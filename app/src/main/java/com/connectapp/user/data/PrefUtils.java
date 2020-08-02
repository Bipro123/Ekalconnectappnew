package com.connectapp.user.data;

import java.io.Serializable;
import java.util.ArrayList;

public class PrefUtils implements Serializable {

	private ArrayList<RathClass> rathClasses = new ArrayList<RathClass>();
	private boolean isRathDownloaded = false;


	public ArrayList<RathClass> getRathClasses() {
		return rathClasses;
	}

	public void setRathClasses(ArrayList<RathClass> rathClasses) {
		this.rathClasses = rathClasses;
	}

	public boolean getIsRathDownloaded() {
		return isRathDownloaded;
	}

	public void setIsRathDownloaded(boolean isRathDownloaded) {
		this.isRathDownloaded = isRathDownloaded;
	}

}
