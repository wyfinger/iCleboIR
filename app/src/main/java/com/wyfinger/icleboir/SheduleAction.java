package com.wyfinger.icleboir;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.ConsumerIrManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static android.app.AlarmManager.RTC_WAKEUP;
import static android.content.Context.CONSUMER_IR_SERVICE;

class ActionDays implements Parcelable {

    private transient boolean everyday;  // transient - for exclude from serialisation
    private transient boolean workdays;  // by gsone
    private transient boolean weekend;
    boolean mon;
    boolean tue;
    boolean wed;
    boolean thu;
    boolean fri;
    boolean sat;
    boolean sun;

    public ActionDays() {
        setWorkdays(true);
    }

    public ActionDays(boolean mon, boolean tue,  boolean wed, boolean thu, boolean fri,
                      boolean sat, boolean sun) {
        this.mon = mon;
        this.tue = tue;
        this.wed = wed;
        this.thu = thu;
        this.fri = fri;
        this.sat = sat;
        this.sun = sun;
    }

    private ActionDays(Parcel in) {
        boolean[] daysArray = new boolean[7];
        in.readBooleanArray(daysArray);
        mon = daysArray[0];
        tue = daysArray[1];
        wed = daysArray[2];
        thu = daysArray[3];
        fri = daysArray[4];
        sat = daysArray[5];
        sun = daysArray[6];
    }

    @Override
    public String toString() {
        if (isEveryday()) {
            return App.getAppContext().getResources().getString(R.string.scheduler_everyday);
        } else if (isWorkdays() & !sat & !sun) {
            return App.getAppContext().getResources().getString(R.string.scheduler_workdays);
        } else if (isWeekend() & !mon & !tue & !wed & !thu & !fri) {
            return App.getAppContext().getResources().getString(R.string.scheduler_weekend);
        } else if (isNewer()) {
            return App.getAppContext().getResources().getString(R.string.scheduler_never);
        } else {
            String r = "";
            if (mon) r += App.getAppContext().getResources().getString(R.string.scheduler_monday_short) + " ";
            if (tue) r += App.getAppContext().getResources().getString(R.string.scheduler_thursday_short) + " ";
            if (wed) r += App.getAppContext().getResources().getString(R.string.scheduler_wednesday_short) + " ";
            if (thu) r += App.getAppContext().getResources().getString(R.string.scheduler_thursday_short) + " ";
            if (fri) r += App.getAppContext().getResources().getString(R.string.scheduler_friday_short) + " ";
            if (sat) r += App.getAppContext().getResources().getString(R.string.scheduler_saturday_short) + " ";
            if (sun) r += App.getAppContext().getResources().getString(R.string.scheduler_sunday_short) + " ";
            return r.trim();
        }
    }

    boolean checkNowDay() {
        // if now day in selected days return true
        Integer dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);      // 1..7
        switch (dayOfWeek) {
            case Calendar.MONDAY    : return mon;
            case Calendar.TUESDAY   : return tue;
            case Calendar.WEDNESDAY : return wed;
            case Calendar.THURSDAY  : return thu;
            case Calendar.FRIDAY    : return fri;
            case Calendar.SATURDAY  : return sat;
            case Calendar.SUNDAY    : return sun;
            default : return sun;
        }
    }

    boolean isEveryday() {
        return mon & tue & wed & thu & fri & sat & sun;
    }

    void setEveryday(boolean everyday) {
        mon = tue = wed = thu = fri = sat = sun = everyday;
    }

    boolean isWorkdays() {
        return mon & tue & wed & thu & fri;
    }

    void setWorkdays(boolean workdays) {
        mon = tue = wed = thu = fri = workdays;
    }

    boolean isWeekend() {
        return sat & sun;
    }

    void setWeekend(boolean weekend) {
        sat = sun = weekend;
    }

    boolean isNewer() {
        return !mon & !tue & !wed & !thu & !fri & !sat & !sun;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        boolean[] daysArray = { mon, tue, wed, thu, fri, sat, sun };
        dest.writeBooleanArray(daysArray);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public ActionDays createFromParcel(Parcel in) {
            return new ActionDays(in);
        }

        public ActionDays[] newArray(int size) {
            return new ActionDays[size];
        }

    };

}

class ActionTime implements Parcelable {

    private int hours;
    private int minutes;

    ActionTime() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        setHours(calendar.get(Calendar.HOUR_OF_DAY)); // gets hour in 24h format
        setMinutes(calendar.get(Calendar.MINUTE));
    }

    ActionTime(Parcel in) {
        hours = in.readInt(); // set bypass setter specially, for error check
        minutes = in.readInt();
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours % 24;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes % 60;
    }

    @Override
    public String toString() {
        // if EN locale - convert to "AM / PM" format
        if (android.text.format.DateFormat.is24HourFormat(App.getAppContext())) {
            return String.format("%02d:%02d", hours, minutes);
        } else {
            return String.format("%02d:%02d %s", (hours % 12 == 0) ? 12 : hours % 12, minutes, (hours < 12) ? " a.m." : " p.m.");
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(hours);
        dest.writeInt(minutes);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public ActionTime createFromParcel(Parcel in) {
            return new ActionTime(in);
        }

        public ActionTime[] newArray(int size) {
            return new ActionTime[size];
        }

    };
}

class ActionScript implements Parcelable {

    private ArrayList<Integer> commands;
    public transient ConsumerIrManager manager;

    public ActionScript() {
        manager = (ConsumerIrManager) App.getAppContext().getSystemService(CONSUMER_IR_SERVICE);
        commands = new ArrayList<Integer>();
    }

    private ActionScript(Parcel in) {
        manager = (ConsumerIrManager) App.getAppContext().getSystemService(CONSUMER_IR_SERVICE);
        commands = (ArrayList<Integer>) in.readSerializable();
    }

    static final transient int ID_POWER  =  1;
    static final transient int ID_START  =  2;
    static final transient int ID_UP     =  3;
    static final transient int ID_LEFT   =  4;
    static final transient int ID_SELECT =  5;
    static final transient int ID_RIGHT  =  6;
    static final transient int ID_DOWN   =  7;
    static final transient int ID_MODE   =  8;
    static final transient int ID_CLOCK  =  9;
    static final transient int ID_TIMER  = 10;
    static final transient int ID_BASE   = 11;
    static final transient int ID_CLIMB  = 12;
    static final transient int ID_SPOT   = 13;
    static final transient int ID_TURBO  = 14;
    static final transient int ID_MAX    = 15;

    public boolean add(int cmdId) {
        return commands.add(cmdId);
    }

    public void clear() {
        commands.clear();
    }

    private String getName(int cmdId) {
        switch (cmdId) {
            case ID_POWER : return App.getAppContext().getResources().getString(R.string.main_power);
            case ID_START : return App.getAppContext().getResources().getString(R.string.main_start);
            case ID_UP    : return App.getAppContext().getResources().getString(R.string.main_up);
            case ID_LEFT  : return App.getAppContext().getResources().getString(R.string.main_left);
            case ID_SELECT: return App.getAppContext().getResources().getString(R.string.main_select);
            case ID_RIGHT : return App.getAppContext().getResources().getString(R.string.main_right);
            case ID_DOWN  : return App.getAppContext().getResources().getString(R.string.main_down);
            case ID_MODE  : return App.getAppContext().getResources().getString(R.string.main_mode);
            case ID_CLOCK : return App.getAppContext().getResources().getString(R.string.main_clock);
            case ID_TIMER : return App.getAppContext().getResources().getString(R.string.main_timer);
            case ID_BASE  : return App.getAppContext().getResources().getString(R.string.main_base);
            case ID_CLIMB : return App.getAppContext().getResources().getString(R.string.main_climb);
            case ID_SPOT  : return App.getAppContext().getResources().getString(R.string.main_spot);
            case ID_TURBO : return App.getAppContext().getResources().getString(R.string.main_turbo);
            case ID_MAX   : return App.getAppContext().getResources().getString(R.string.main_max);
            default: return "";
        }
    }

    @Override
    public String toString() {
        String ret = "";
        for (int x = 0; x < commands.size(); x = x + 1) {
            ret = ret+ "["+getName(commands.get(x)) + "]";
        }
        return ret;
    }

    public void play(int cmdId) {
        if (manager.hasIrEmitter() == false) return;
        switch (cmdId) {
            case ID_POWER:
                manager.transmit(38000,
                    new int[]{8950, 4450, 550, 1700, 500, 550, 600, 550, 550, 550, 550, 550, 550, 550,
                            600, 550, 550, 550, 550, 550, 550, 1700, 550, 1650, 550, 1700, 550, 1650, 550,
                            1700, 550, 1650, 550, 1700, 500, 1700, 550, 1700, 500, 1700, 550, 550, 550, 550,
                            550, 550, 600, 500, 600, 550, 550, 550, 550, 550, 600, 500, 600, 1650, 550, 1700,
                            550, 1650, 600, 1650, 550, 1650, 550});
                break;
            case ID_START:
                manager.transmit(38000,
                    new int[]{8900, 4450, 550, 1650, 550, 550, 550, 600, 550, 550, 550, 550, 550, 550,
                            550, 600, 550, 550, 550, 550, 550, 1700, 550, 1650, 550, 1700, 500, 1700, 550,
                            1700, 500, 1700, 550, 1650, 550, 550, 550, 600, 550, 1650, 550, 550, 550, 550,
                            600, 550, 550, 550, 550, 550, 550, 1700, 550, 1700, 500, 550, 600, 1650, 550,
                            1700, 500, 1700, 550, 1700, 500, 1700, 550});
                break;
            case ID_UP:
                manager.transmit(38000,
                    new int[]{8900, 4500, 550, 1650, 550, 550, 550, 550, 600, 550, 550, 550, 550, 600,
                            500, 550, 600, 550, 550, 550, 550, 1700, 550, 1650, 550, 1700, 500, 1700, 550,
                            1700, 500, 1700, 550, 1650, 550, 550, 600, 550, 550, 550, 550, 1700, 500, 600,
                            550, 550, 550, 550, 550, 600, 550, 1650, 550, 1700, 500, 1700, 550, 550, 550,
                            1700, 550, 1650, 550, 1700, 500, 1700, 550});
                break;
            case ID_LEFT:
                manager.transmit(38000,
                    new int[]{8900, 4450, 550, 1700, 500, 600, 550, 550, 550, 550, 550, 550, 550, 600,
                        550, 550, 550, 550, 550, 550, 550, 1700, 550, 1700, 500, 1700, 550, 1650, 550,
                        1700, 550, 1650, 550, 1700, 500, 1700, 550, 1700, 500, 600, 550, 1650, 550, 550,
                        550, 550, 600, 550, 550, 550, 550, 550, 550, 600, 550, 1650, 550, 550, 550, 1700,
                        550, 1650, 550, 1700, 550, 1650, 550});
                break;
            case ID_SELECT:
                manager.transmit(38000,
                    new int[]{8900, 4500, 500, 1700, 550, 550, 550, 600, 500, 600, 550, 550, 550, 550,
                        550, 600, 550, 550, 550, 550, 550, 1700, 500, 1700, 550, 1700, 500, 1700, 550,
                        1650, 550, 1700, 550, 1650, 550, 600, 500, 600, 550, 1650, 550, 1700, 500, 600,
                        550, 550, 550, 550, 550, 600, 500, 1700, 550, 1700, 500, 600, 550, 550, 550, 1700,
                        500, 1700, 550, 1650, 550, 1700, 550});
                break;
            case ID_RIGHT:
                manager.transmit(38000,
                    new int[]{8900, 4450, 550, 1650, 550, 550, 550, 550, 600, 550, 550, 550, 550, 550,
                        550, 600, 550, 550, 550, 550, 550, 1700, 550, 1650, 550, 1700, 550, 1650, 550,
                        1700, 500, 1700, 550, 1700, 500, 550, 600, 1650, 550, 550, 550, 1700, 550, 550,
                        550, 550, 550, 550, 600, 550, 550, 1650, 550, 550, 550, 1700, 550, 550, 550, 1700,
                        550, 1650, 550, 1700, 500, 1700, 550});
                break;
            case ID_DOWN:
                manager.transmit(38000,
                    new int[]{8950, 4450, 550, 1700, 500, 600, 550, 550, 550, 550, 550, 550, 550, 600,
                        550, 550, 550, 550, 550, 550, 550, 1700, 550, 1700, 500, 1700, 550, 1700, 500,
                        1700, 550, 1650, 550, 1700, 550, 1650, 550, 550, 550, 550, 600, 1650, 550, 550,
                        550, 550, 600, 550, 550, 550, 550, 550, 550, 1700, 550, 1700, 500, 550, 600, 1650,
                        550, 1700, 500, 1700, 550, 1700, 500});
                break;
            case ID_MODE:
                manager.transmit(38000,
                    new int[]{8900, 4500, 550, 1650, 550, 550, 550, 600, 500, 600, 550, 550, 550, 550,
                        550, 600, 500, 600, 550, 550, 550, 1700, 500, 1700, 550, 1700, 500, 1700, 550,
                        1650, 550, 1700, 550, 1650, 550, 600, 500, 1700, 550, 1650, 550, 600, 500, 600,
                        550, 550, 550, 550, 550, 550, 550, 1700, 550, 550, 550, 550, 550, 1700, 550, 1650,
                        550, 1700, 550, 1650, 550, 1700, 550});
                break;
            case ID_CLOCK:
                manager.transmit(38000,
                    new int[]{8950, 4450, 550, 1650, 550, 600, 500, 600, 550, 550, 550, 600, 500, 600,
                        500, 600, 550, 550, 550, 550, 550, 1700, 550, 1650, 550, 1700, 550, 1650, 550,
                        1700, 500, 1700, 550, 1700, 500, 1700, 550, 550, 550, 1700, 500, 600, 550, 550,
                        550, 550, 550, 600, 500, 600, 550, 550, 550, 1700, 500, 600, 550, 1650, 550, 1700,
                        550, 1650, 550, 1700, 500, 1700, 550});
                break;
            case ID_TIMER:
                manager.transmit(38000,
                    new int[]{8950, 4450, 550, 1650, 550, 550, 550, 600, 550, 550, 550, 600, 500, 550,
                        550, 600, 550, 550, 550, 600, 500, 1700, 550, 1650, 550, 1700, 550, 1650, 550,
                        1700, 500, 1700, 550, 1700, 500, 1700, 550, 550, 550, 600, 500, 600, 550, 550,
                        550, 550, 550, 600, 500, 600, 550, 550, 550, 1700, 500, 1700, 550, 1650, 550,
                        1700, 550, 1650, 550, 1700, 550, 1650, 550});
                break;
            case ID_BASE:
                manager.transmit(38000,
                    new int[]{8900, 4450, 550, 1700, 500, 600, 550, 550, 550, 550, 550, 550, 600, 550,
                        550, 550, 550, 550, 550, 550, 600, 1650, 550, 1700, 500, 1700, 550, 1650, 550,
                        1700, 550, 1650, 550, 1700, 550, 550, 550, 1700, 500, 550, 600, 550, 550, 550,
                        550, 550, 550, 550, 600, 550, 550, 1650, 550, 550, 550, 1700, 550, 1700, 500,
                        1700, 550, 1650, 550, 1700, 550, 1650, 550});
                break;
            case ID_CLIMB:
                manager.transmit(38000,
                    new int[]{8950, 4450, 550, 1700, 550, 550, 550, 550, 550, 550, 600, 550, 550, 550,
                        550, 550, 550, 550, 600, 550, 550, 1700, 500, 1700, 550, 1650, 550, 1700, 550,
                        1650, 550, 1700, 500, 1700, 550, 1700, 500, 1700, 550, 550, 550, 550, 550, 550,
                        600, 550, 550, 550, 550, 550, 550, 550, 600, 550, 550, 1700, 500, 1700, 550, 1650,
                        550, 1700, 550, 1650, 550, 1700, 500});
                break;
            case ID_SPOT:
                manager.transmit(38000,
                    new int[]{8950, 4450, 550, 1700, 550, 550, 550, 550, 550, 550, 550, 550, 600, 550,
                        550, 550, 550, 550, 550, 550, 600, 1650, 550, 1700, 500, 1700, 550, 1700, 500,
                        1700, 550, 1700, 500, 1700, 550, 1650, 550, 550, 550, 1700, 550, 1700, 500, 550,
                        550, 600, 550, 550, 550, 550, 550, 550, 600, 1650, 550, 550, 550, 550, 550, 1700,
                        550, 1700, 500, 1700, 550, 1650, 550});
                break;
            case ID_TURBO:
                manager.transmit(38000,
                    new int[]{8850, 4550, 450, 1750, 450, 650, 450, 650, 450, 650, 450, 650, 450, 650,
                        450, 650, 450, 650, 450, 650, 450, 1750, 450, 1750, 450, 1750, 450, 1750, 450,
                        1750, 450, 1750, 450, 1750, 450, 1750, 450, 1750, 450, 1750, 450, 1750, 450, 650,
                        450, 650, 450, 650, 450, 650, 450, 650, 450, 650, 450, 650, 450, 650, 450, 1750,
                        450, 1750, 450, 1750, 450, 1750, 450});
                break;
            case ID_MAX:
                manager.transmit(38000,
                    new int[]{8950, 4450, 550, 1700, 550, 550, 550, 550, 550, 550, 550, 600, 550, 550,
                        550, 550, 550, 550, 550, 600, 550, 1650, 550, 1700, 550, 1650, 550, 1700, 500,
                        1700, 550, 1700, 500, 1700, 550, 550, 550, 1700, 550, 1650, 550, 1700, 500, 550,
                        600, 550, 550, 550, 550, 550, 550, 1700, 550, 550, 550, 550, 550, 550, 600, 1650,
                        550, 1700, 500, 1700, 550, 1700, 500});
                break;
        }
    }

    public void playAll() {
        Thread t = new Thread(new Runnable() {
            public void run() {
                for (int x = 0; x < commands.size(); x = x + 1) {
                    play(commands.get(x));
                    try {
                        TimeUnit.MILLISECONDS.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(commands);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public ActionScript createFromParcel(Parcel in) {
            return new ActionScript(in);
        }

        public ActionScript[] newArray(int size) {
            return new ActionScript[size];
        }

    };

}

public class SheduleAction implements Parcelable {

    ActionDays days;
    ActionTime time;
    ActionScript script;
    boolean enable;

    public SheduleAction() {
        days = new ActionDays();
        time = new ActionTime();
        script = new ActionScript();
        enable = true;
    }

    private SheduleAction(Parcel in) {
        days = in.readParcelable(ActionDays.class.getClassLoader());
        time = in.readParcelable(ActionTime.class.getClassLoader());
        script = in.readParcelable(ActionScript.class.getClassLoader());
        enable = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(days, flags);
        dest.writeParcelable(time, flags);
        dest.writeParcelable(script, flags);
        dest.writeByte((byte) (enable ? 1 : 0));
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public SheduleAction createFromParcel(Parcel in) {
            return new SheduleAction(in);
        }

        public SheduleAction[] newArray(int size) {
            return new SheduleAction[size];
        }

     };

}

class ScheduleActions {

    private ArrayList<SheduleAction> data;
    private transient SharedPreferences sharedPrefs;
    private transient Gson gson;
    private AlarmManager alarmManager;
    boolean needInfoTip;

    private String PREF_ACTIONS = "actions";

    ScheduleActions() {

        data = new ArrayList<SheduleAction>();

        sharedPrefs = App.getAppContext().getSharedPreferences("settings", Context.MODE_PRIVATE);

        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        gson = builder.create();

        alarmManager = (AlarmManager) App.getAppContext().getSystemService(Context.ALARM_SERVICE);
    }

    boolean add(SheduleAction element) {
        clearAlarms();
        boolean ret = data.add(element);
        setAlarms();
        return ret;
    }

    SheduleAction get(int index) {
        return data.get(index);
    }

    SheduleAction set(int index, SheduleAction element) {
        clearAlarms();
        SheduleAction ret = data.set(index, element);
        setAlarms();
        return ret;
    }

    SheduleAction remove(int index) {
        clearAlarms();
        SheduleAction ret = data.remove(index);
        setAlarms();
        return ret;
    }

    int size() {
        return data.size();
    }

    void load() {
        if (sharedPrefs.contains(this.PREF_ACTIONS)) {
            String pref = sharedPrefs.getString(this.PREF_ACTIONS, "");
            data = gson.fromJson(pref, new TypeToken<ArrayList<SheduleAction>>() {}.getType());
            needInfoTip = false;
        } else {
            needInfoTip = true;
        }
        clearAlarms();
        setAlarms();
    }

    void clearAlarms() {
        for (int x = 0; x < data.size(); x = x + 1) {
            Intent intent = new Intent(App.getAppContext(), ActionReceiver.class);
            intent.setAction(App.getAppContext().getPackageName()+".action_"+x);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(App.getAppContext(),
                    x, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            alarmManager.set(RTC_WAKEUP, System.currentTimeMillis() + 1000,
                    pendingIntent);
        }
    }

    void setAlarms() {
        for (int x = 0; x < data.size(); x = x + 1) {
            SheduleAction action = data.get(x);
            if (action.enable) {
                Intent intent = new Intent(App.getAppContext(), ActionReceiver.class);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setAction(App.getAppContext().getPackageName() + ".action_" + x);
                // send action data to receiver
                intent.putExtra(App.getAppContext().getPackageName() + ".action", data.get(x));

                PendingIntent pendingIntent = PendingIntent.getBroadcast(App.getAppContext(),
                        x, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                // calculate time of action in Millis
                Calendar calendar = Calendar.getInstance();
                Calendar cal = Calendar.getInstance();

                calendar.set(Calendar.YEAR, cal.get(Calendar.YEAR));
                calendar.set(Calendar.MONTH, cal.get(Calendar.MONTH));
                calendar.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH));
                calendar.set(Calendar.HOUR_OF_DAY, action.time.getHours());
                calendar.set(Calendar.MINUTE, action.time.getMinutes());
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                // convert to second day time if needed
                if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                    calendar.add(Calendar.HOUR_OF_DAY, 24);
                }

                Log.i("wyalarm", "alarm after " + (calendar.getTimeInMillis() - System.currentTimeMillis()) / 1000 + " sec");

                alarmManager.setRepeating(RTC_WAKEUP, calendar.getTimeInMillis(),
                        24 * 60 * 60 * 1000, pendingIntent);
            }
        }
    }

    void save() {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        String json = gson.toJson(data);
        editor.putString(this.PREF_ACTIONS, json);
        editor.apply();
    }

}
