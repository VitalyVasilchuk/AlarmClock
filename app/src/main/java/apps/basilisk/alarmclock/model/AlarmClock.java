package apps.basilisk.alarmclock.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Calendar;

public class AlarmClock implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String APP_PREF_ALARM_CLOCK = "ALARM_CLOCK";

    public enum AlarmState {
        ON, OFF, ALARMED, RINGTONE, COMPLETED
    }

    private long time;
    private AlarmState state;
    private boolean repeating;
    private String name;
    private String description;

    public AlarmClock(long time, AlarmState state, boolean repeating, String name, String description) {
        this.time = time;
        this.state = state;
        this.repeating = repeating;
        this.name = name;
        this.description = description;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setTime(int currentHour, int currentMinute) {
        Calendar dateAndTime = Calendar.getInstance();
        dateAndTime.set(Calendar.HOUR_OF_DAY, currentHour);
        dateAndTime.set(Calendar.MINUTE, currentMinute);
        dateAndTime.set(Calendar.SECOND, 0);
        time = dateAndTime.getTimeInMillis();
        if (time < System.currentTimeMillis()) time += 1000 * 60 * 60 * 24;
    }

    public AlarmState getState() {
        return state;
    }

    public void setState(AlarmState state) {
        this.state = state;
    }

    public boolean isRepeating() {
        return repeating;
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "AlarmClock{" +
                "time=" + time +
                ", state=" + state +
                ", repeating=" + repeating +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public byte[] toByteArray() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(this);
            out.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static AlarmClock fromByteArray(byte[] bytes) {
        if (bytes == null) return null;
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        AlarmClock alarm;
        try {
            in = new ObjectInputStream(bis);
            alarm = (AlarmClock) in.readObject();
            return alarm;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public void save(Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        String alarmString = Base64.encodeToString(this.toByteArray(), Base64.NO_WRAP);
        editor.putString(APP_PREF_ALARM_CLOCK, alarmString);
        editor.apply();
    }

    public static AlarmClock load(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String alarmString = preferences.getString(APP_PREF_ALARM_CLOCK, "");
        if (alarmString != null) {
            byte[] alarmByte = alarmString.getBytes();
            return fromByteArray(Base64.decode(alarmByte, Base64.NO_WRAP));
        }
        return null;
    }
}
