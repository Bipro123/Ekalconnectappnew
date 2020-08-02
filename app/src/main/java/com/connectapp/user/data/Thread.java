package com.connectapp.user.data;

import com.connectapp.user.R;

import java.io.Serializable;
import java.util.ArrayList;

public class Thread implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 4L;

    private ArrayList<Keyword> keywords = new ArrayList<Keyword>();

    private String threadID;

    private String threadName;

    private int threadImage = R.drawable.ic_school;

    public ArrayList<Keyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(ArrayList<Keyword> keywords) {
        this.keywords = keywords;
    }

    public String getThreadID() {
        return threadID;
    }

    public void setThreadID(String threadID) {
        this.threadID = threadID;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public int getThreadImage() {
        return threadImage;
    }

    public void setThreadImage(int threadImage) {
        this.threadImage = threadImage;
    }
}
