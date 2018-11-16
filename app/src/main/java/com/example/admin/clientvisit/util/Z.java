package com.example.admin.clientvisit.util;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Z {

    public static final String TAG="TAG";
    public static final int DISTANCCE_TO_COMPARE=2000;   // 2 km

    public static boolean isNotNullNotBlank(String input) {
        if (input == null)
            return false;
        else if (input.equals("")) {
            return false;
        } else
            return true;
    }

    public static boolean isEmailValid(String emailStr) {
        Pattern emailPattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher matcher = emailPattern.matcher(emailStr);
        boolean result = matcher.find();
        Log.d(TAG, "isEmailValid: "+result);
        return result;

    }
}
