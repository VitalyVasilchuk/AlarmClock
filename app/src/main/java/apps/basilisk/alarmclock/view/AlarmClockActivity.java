package apps.basilisk.alarmclock.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.HashMap;

import apps.basilisk.alarmclock.R;
import apps.basilisk.alarmclock.presenter.AlarmClockPresenter;
import apps.basilisk.alarmclock.presenter.IAlarmClockPresenter;

public class AlarmClockActivity extends AppCompatActivity implements IAlarmClockView {
    private TimePicker timePicker;
    private TextView textCountdown;
    private Button buttonAction;

    private IAlarmClockPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(/*WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |*/
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        timePicker = findViewById(R.id.alarm_time_picker);
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                presenter.timePickerChange(hourOfDay, minute);
            }
        });

        textCountdown = findViewById(R.id.text_countdown);

        buttonAction = findViewById(R.id.button_action);
        buttonAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> output = new HashMap<>();
                output.put("hour", timePicker.getCurrentHour());
                output.put("minute", timePicker.getCurrentMinute());
                presenter.buttonActionClick(output);
            }
        });

        presenter = new AlarmClockPresenter(this);
        presenter.attach(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.destroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.loadData(getIntent());
    }

    @Override
    public void setActionButtonText(String text) {
        buttonAction.setText(text);
    }

    @Override
    public void setCountDownTimerText(String text) {
        textCountdown.setText(text);
    }

    @Override
    public void setTimePicker(Integer hour, Integer minute) {
        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(minute);
    }
}
