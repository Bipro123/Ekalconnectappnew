package com.connectapp.user.chatmodel;

import java.io.Serializable;

/**
 * Created by ritwik on 09/12/17.
 */

public class ChatContact implements Serializable {
    public String name;
    public String emailId;
    public String mobile;
    public String studentFirebaseId;
    public String userID;
    public int unreadMsgCount = 0;
    public String profileImgURL;
    public String firebaseInstanceID;
}
