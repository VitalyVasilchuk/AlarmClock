package apps.basilisk.alarmclock.presenter;

import android.content.Intent;

import java.util.HashMap;

import apps.basilisk.alarmclock.view.IAlarmClockView;

public interface IAlarmClockPresenter {
    void attach(IAlarmClockView view);

    void detach();

    void destroy();

    void loadData(Intent intent);

    void buttonActionClick(HashMap<String, Object> input);

    void timePickerChange(int hour, int minute);
}
