package tr.com.mysatech.locationmonitor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


/**
 * Implementation of App Widget functionality.
 */
public class MyWidget extends AppWidgetProvider
{
        @Override
        public void onDeleted(Context context, int[] appWidgetIds) {
            super.onDeleted(context, appWidgetIds);
        }

        @Override
        public void onDisabled(Context context) {
            Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
            PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(sender);
            super.onDisabled(context);
        }

        @Override
        public void onEnabled(Context context) {
            super.onEnabled(context);
            AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
            am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, 1000 , pi);
        }

        @Override
        public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                             int[] appWidgetIds) {
        }

        @Override
        public void onAppWidgetOptionsChanged(Context context,
                                              AppWidgetManager appWidgetManager, int appWidgetId,
                                              Bundle newOptions) {
        }
}

