package com.northstar.minimap;

import android.app.Application;
import android.util.Log;

/**
 * Created by Benjin on 11/9/13.
 */
public class Globals extends Application {
    public Communicator comm = new Communicator();
    public static void log(String message)
    {
        log("Minimap: ", message);
    }

    public static void log(String tag, String message)
    {
        Log.d(tag, message);
    }
}
