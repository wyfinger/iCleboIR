package com.wyfinger.icleboir;

import android.app.Application;
import android.content.Context;

/**
 *  This is a Application class to get Context
 *  from everyone. This class marked at Android Manifest.
 */

public class App extends Application {

    private static Context context;
    public static ScheduleActions actionsData;

    public void onCreate() {
        super.onCreate();
        App.context = getApplicationContext();
        // load actions and set alarms
        actionsData = new ScheduleActions();
        actionsData.load();
        actionsData.clearAlarms();
        actionsData.setAlarms();
    }

    public static Context getAppContext() {
        return App.context;
    }

}
