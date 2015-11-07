package show.com.tron;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Boolean aBoolean = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SettingsActivity.SWITCH_CHECKED, false);
        if ( !aBoolean ) {
            PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(sender);
            return;
        }

        DBHelper db = DBHelper.getInstance(context);
        String day = new SimpleDateFormat("EEEE").format(Calendar.getInstance().getTime()).toUpperCase();
        List<Show> list = db.getTodayShows( day );

        if ( !list.isEmpty() ) {
            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            Intent notificationIntent = new Intent(context, MainActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 1,
                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);// PendingIntent.FLAG_CANCEL_CURRENT

            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            Notification notif = new Notification.Builder(context)
                    .setContentTitle( context.getResources().getString(R.string.notificaton_title) )
                    .setContentText( getTitle(list) )
                    .setSmallIcon( R.drawable.ic_stat_action_theaters )
                    .setSound(alarmSound)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .build();
            notificationManager.notify(0, notif);
        }
    }

    private String getTitle(List<Show> list) {
        String result = "";
        Iterator iterator = list.iterator();
        int i = 3;
        while ( iterator.hasNext() && i > 0 ) {
            Show show = (Show) iterator.next();
            i--;
            if ( iterator.hasNext() && i > 0 ) {
                result += show.getName() + ", ";
            } else {
                result += show.getName() + ".";
            }
        }
        return result;
    }
}
