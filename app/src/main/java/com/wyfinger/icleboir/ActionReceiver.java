package com.wyfinger.icleboir;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;

public class ActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SheduleAction action = intent.getParcelableExtra(App.getAppContext().getPackageName() + ".action");
        if (action == null) return;
        if (action.days.checkNowDay()) {
            action.script.playAll();
            Log.i("wyalarm", "alarm");
            ((Vibrator) App.getAppContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(200);
        }
    }
}
