package com.connectapp.user.data;

import java.io.Serializable;
import java.util.ArrayList;

public class Threads implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Thread> threads = new ArrayList<Thread>();

	public ArrayList<Thread> getThreads() {
		return threads;
	}

	public void setThreads(ArrayList<Thread> threads) {
		this.threads = threads;
	}

}
