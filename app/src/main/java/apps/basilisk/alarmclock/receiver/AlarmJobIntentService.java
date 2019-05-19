package apps.basilisk.alarmclock.receiver;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

import apps.basilisk.alarmclock.view.AlarmClockActivity;
import apps.basilisk.alarmclock.model.AlarmClock;

public class AlarmJobIntentService extends JobIntentService {
    private static final int JOB_ID = 777;

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Intent intentActivity = new Intent(this, AlarmClockActivity.class);
        intentActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        AlarmClock alarmClock = AlarmClock.fromByteArray(intent.getByteArrayExtra("ALARM_CLOCK"));
        if (alarmClock != null) {
            alarmClock.setState(AlarmClock.AlarmState.ALARMED);
            alarmClock.save(getApplicationContext());
            intentActivity.putExtra("ALARM_CLOCK", alarmClock.toByteArray());
        }

        startActivity(intentActivity);
    }

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, AlarmJobIntentService.class, JOB_ID, work);
    }
}
