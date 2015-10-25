package show.com.tron;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SettingsActivity extends PreferenceActivity {

    private static TimePickerDialog timePickerDialog;
    private static SharedPreferences timePreference;
    public static final String TIME_PREFERENCE = "TIME_PREFERENCE";
    private static final String HOUR = "HOUR";
    private static final String MINUTE = "MINUTE";
    public static final String SWITCH_CHECKED = "SWITCH_CHECKED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        Preference prefNotificationTime;
        Preference prefNumberOfShows;
        Preference prefDelete;
        TronApplication tron;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            tron = (TronApplication) getActivity().getApplicationContext();

            //Retrieve preference where time is saved.
            timePreference = getActivity().getSharedPreferences(TIME_PREFERENCE, Context.MODE_PRIVATE);

            Calendar notifCal = Calendar.getInstance();
            notifCal.set(Calendar.HOUR_OF_DAY, timePreference.getInt(HOUR, 18));
            notifCal.set(Calendar.MINUTE, timePreference.getInt(MINUTE, 0));
            notifCal.set(Calendar.SECOND, 0);

            prefNotificationTime = findPreference("prefNotificationTime");
            prefNotificationTime.setSummary( notifCal.get(Calendar.HOUR_OF_DAY) +
                    ":" + new SimpleDateFormat("mm").format( notifCal.getTime() ) );
            prefNumberOfShows = findPreference("numberOfShows");
            prefNumberOfShows.setSummary( numberOfShows() + "" );
            prefDelete = findPreference("prefDelete");



            //Initialized Time Picker Dialog
            timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    SharedPreferences.Editor editor = timePreference.edit();//Save new time values.
                    editor.putInt(HOUR, hourOfDay);
                    editor.putInt(MINUTE, minute);
                    editor.apply();
                    handleNotification();
                    refresh();
                }
            }, timePreference.getInt(HOUR, 18), timePreference.getInt(MINUTE, 0), false);

            //Set Time Picker Dialog On Click
            prefNotificationTime.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    timePickerDialog.show();
                    return true;
                }
            });

            prefDelete.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new AlertDialog.Builder( getActivity() )
                            .setTitle("Are you sure?")
                            .setMessage("You'll lose all your shows!")
                            .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    tron.deleteAllShows();
                                }
                            })
                            .setNegativeButton("CANCEL", null)
                            .show();
                    return false;
                }
            });

        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        public void refresh() {
            getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        }

        private void handleNotification() {
            Intent alarmIntent = new Intent(getActivity(), NotificationReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            SwitchPreference switchPreference = (SwitchPreference) findPreference("prefNotificationSwitch");
            if ( !switchPreference.isChecked() ) {
                pendingIntent.cancel();
                return;
            }

            SharedPreferences preferences = getActivity().getSharedPreferences(TIME_PREFERENCE, Context.MODE_PRIVATE);
            int hourOfDay = preferences.getInt("HOUR", 18);
            int minute = preferences.getInt("MINUTE", 0);

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
            cal.set(Calendar.MINUTE, minute);
            cal.set(Calendar.SECOND, 0);

            if ( cal.before(Calendar.getInstance()) ) {
                cal.setTimeInMillis( cal.getTimeInMillis() + AlarmManager.INTERVAL_DAY );
            }

            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }

        private int numberOfShows( ) {
            return tron.getNumberOfShow();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Log.d("SettingsActivity", "A Pref changed 2");
            if ( key.equals("prefNotificationSwitch") ) {
                Preference pref = findPreference(key);
                if (pref instanceof SwitchPreference) {
                    SwitchPreference switchP =  (SwitchPreference) pref;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(SWITCH_CHECKED, switchP.isChecked());
                    editor.apply();
                }
            }
        }
    }
}
