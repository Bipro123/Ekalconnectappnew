package com.connectapp.user.chatmodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ritwik on 24/12/17.
 */

public class ChatContactList implements Serializable {

    /***
     * Students Array with Students Object
     * */
    public ArrayList<ChatContact> studentsArrayList= new ArrayList<>();

    /**
     * Students Map saved according to its key--> email ID
     * */
    public HashMap<String,ChatContact> studentArrayMap= new HashMap<>();
}
