package apps.basilisk.alarmclock.presenter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import apps.basilisk.alarmclock.R;
import apps.basilisk.alarmclock.model.AlarmClock;
import apps.basilisk.alarmclock.receiver.AlarmReceiver;
import apps.basilisk.alarmclock.view.IAlarmClockView;

public class AlarmClockPresenter implements IAlarmClockPresenter {
    private IAlarmClockView view;

    private Context context;
    private AlarmClock alarmClock;
    private CountDownTimer countDownTimer;
    private MediaPlayer mediaPlayer;

    public AlarmClockPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void attach(IAlarmClockView view) {
        this.view = view;
    }

    @Override
    public void detach() {
        this.view = null;
    }

    @Override
    public void destroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (alarmClock.getState().equals(AlarmClock.AlarmState.RINGTONE)) {
            alarmClock.setState(AlarmClock.AlarmState.COMPLETED);
            alarmClock.save(context);
        }
    }

    @Override
    public void loadData(Intent intent) {
        //alarmClock = (AlarmClock) getIntent().getSerializableExtra("ALARM_CLOCK");
        alarmClock = AlarmClock.fromByteArray(intent.getByteArrayExtra("ALARM_CLOCK"));

        if (alarmClock == null) alarmClock = AlarmClock.load(context);

        if (alarmClock == null)
            alarmClock = new AlarmClock(System.currentTimeMillis(), AlarmClock.AlarmState.OFF,
                    false, "", "");
        else {
            Calendar c = Calendar.getInstance();
            c.setTime(new Date(alarmClock.getTime()));
            view.setTimePicker(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));

            if (alarmClock.getState().equals(AlarmClock.AlarmState.ON))
                countdownStart(alarmClock.getTime());
        }

        updateUI();
    }

    @Override
    public void buttonActionClick(HashMap<String, Object> input) {
        int hour = (int) input.get("hour");
        int minute = (int) input.get("minute");

        switch (alarmClock.getState()) {
            case ON:
                alarmClock.setState(AlarmClock.AlarmState.OFF);
                AlarmReceiver.stop(context);
                countdownStop();
                break;

            case OFF:
            case COMPLETED:
                alarmClock.setTime(hour, minute);
                alarmClock.setState(AlarmClock.AlarmState.ON);
                AlarmReceiver.start(context, alarmClock);
                countdownStart(alarmClock.getTime());
                break;

            case ALARMED:
            case RINGTONE:
                alarmClock.setState(AlarmClock.AlarmState.COMPLETED);
                if (mediaPlayer != null) {
                    mediaPlayerStop();
                }
                countdownStop();
                break;
        }
        alarmClock.save(context);

        updateUI();
    }

    @Override
    public void timePickerChange(int hour, int minute) {
        if (alarmClock.getState() != AlarmClock.AlarmState.ON && alarmClock.getState() != AlarmClock.AlarmState.ALARMED) {
            alarmClock.setTime(hour, minute);
            view.setCountDownTimerText(String.format(context.getString(R.string.alarm_time_remaining),
                    millisToString(alarmClock.getTime() - System.currentTimeMillis())));
        }
    }

    private void countdownStart(long time) {
        countDownTimer = new CountDownTimer(time - System.currentTimeMillis(), 1000) {

            public void onTick(long millisUntilFinished) {
                view.setCountDownTimerText(String.format(context.getString(R.string.alarm_time_remaining), millisToString(millisUntilFinished)));
            }

            public void onFinish() {
                view.setCountDownTimerText(context.getString(R.string.alarm_done));
            }
        };
        countDownTimer.start();
    }

    private String millisToString(long millisUntilFinished) {
        long seconds = millisUntilFinished / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        return (days > 0) ? days + ":" : "" + hours % 24 + "h " + minutes % 60 + "' " + seconds % 60 + "\"";
    }

    private void countdownStop() {
        if (countDownTimer != null) countDownTimer.cancel();
        view.setCountDownTimerText("");
    }

    private void updateUI() {
        switch (alarmClock.getState()) {
            case ON:
                view.setActionButtonText(context.getString(R.string.alarm_label_stop));
                break;

            case COMPLETED:
                view.setCountDownTimerText(context.getString(R.string.alarm_done));
            case OFF:
                view.setActionButtonText(context.getString(R.string.alarm_label_start));
                break;

            case ALARMED:
                view.setActionButtonText(context.getString(R.string.alarm_label_disable));
                view.setCountDownTimerText(context.getString(R.string.alarm_done));
                mediaPlayerStart();
                break;

            case RINGTONE:
                view.setActionButtonText(context.getString(R.string.alarm_label_disable));
                view.setCountDownTimerText(context.getString(R.string.alarm_done));
                break;
        }
    }

    private void mediaPlayerStart() {
        if (mediaPlayer == null) {
            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmUri == null) {
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }

            mediaPlayer = MediaPlayer.create(context, alarmUri);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    alarmClock.setState(AlarmClock.AlarmState.COMPLETED);
                    alarmClock.save(context);
                    view.setActionButtonText(context.getString(R.string.alarm_label_start));
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
            });
            alarmClock.setState(AlarmClock.AlarmState.RINGTONE);
            alarmClock.save(context);
            mediaPlayer.start();
        }
    }

    private void mediaPlayerStop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
