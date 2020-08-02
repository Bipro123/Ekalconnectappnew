package com.connectapp.user.data;

import android.content.Context;

import com.connectapp.user.chatmodel.Friend;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ritwik-rai on 23-06-2017.
 */

public class ParseFirebaseData {
    private SettingsAPI set;

    public ParseFirebaseData(Context context) {
        set = new SettingsAPI(context);
    }

    public List<Friend> getUserList(String userData) {
        List<Friend> frnds = new ArrayList<>();
        String name = null, id = null, photo = null;
        for (String oneUser : userData.split("[}][,]")) {
            String[] temp = oneUser.replace("}", "").split("[{]");
            String[] userParts = temp[temp.length - 1].split(",");
            for (String part : userParts) {
                if (part.split("=")[0].trim().equals("displayName"))
                    name = part.split("=")[1].trim();
                if (part.split("=")[0].trim().equals("uid"))
                    id = part.split("=")[1].trim();
                if (part.split("=")[0].trim().equals("photoUrl"))
                    photo = part.split("=")[1].trim();
            }
            if (!set.readSetting("myid").equals(id))
                frnds.add(new Friend(id, name, photo));
        }
        return frnds;
    }

    private String encodeText(String msg) {
        return msg.replace(",", "#comma#").replace("{", "#braceopen#").replace("}", "#braceclose#").replace("=", "#equals#");
    }

    private String decodeText(String msg) {
        return msg.replace("#comma#", ",").replace("#braceopen#", "{").replace("#braceclose#", "}").replace("#equals#", "=");
    }
}
