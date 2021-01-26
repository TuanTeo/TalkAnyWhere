package com.developer.myapplication.util;

import android.util.Log;

public class LogUtils {
    private static final boolean DEBUG = true;
    public static void showLog(String string){
        if(DEBUG){
            Log.d("TalkAnyWhere", string);
        }
    }
}
