package com.example.sociallogin.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class MyUtils {

    public static void showMessage(Context context, String message){
        if( context !=null && message!=null){
            Toast toast = Toast.makeText(context,message, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    public static void logCatPrinter(String tag, String message){
        Log.i(tag, message);
    }
}
