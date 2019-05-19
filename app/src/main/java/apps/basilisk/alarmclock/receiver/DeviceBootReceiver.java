package apps.basilisk.alarmclock.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import apps.basilisk.alarmclock.BuildConfig;
import apps.basilisk.alarmclock.model.AlarmClock;

public class DeviceBootReceiver extends BroadcastReceiver {
    private static final String TAG = "DeviceBootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BuildConfig.DEBUG) Log.d(TAG, "onReceive(), action = " + action);
        if (action != null && (action.equals("android.intent.action.BOOT_COMPLETED") ||
                action.equals("android.intent.action.QUICKBOOT_POWERON") ||
                action.equals("com.htc.intent.action.QUICKBOOT_POWERON"))) {
            AlarmClock alarmClock = AlarmClock.load(context);
            if (alarmClock != null && (alarmClock.getState().equals(AlarmClock.AlarmState.ALARMED) ||
                    alarmClock.getState().equals(AlarmClock.AlarmState.ON))) {
                AlarmReceiver.start(context, alarmClock);
            }
        }
    }
}
