package apps.basilisk.alarmclock.view;

public interface IAlarmClockView {
    void setActionButtonText(String text);

    void setCountDownTimerText(String text);

    void setTimePicker(Integer hour, Integer minute);
}
