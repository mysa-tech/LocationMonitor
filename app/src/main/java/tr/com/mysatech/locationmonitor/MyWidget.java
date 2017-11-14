package tr.com.mysatech.locationmonitor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class MyWidget extends AppWidgetProvider
{
    public static final String TAG = "MyWidget";
    public static final String START_SERVICE = "service_start";
    public static final String PAUSE_SERVICE = "service_pause";
    public static final String SETTINGS_ACTIVITY = "activity_settings";

    //private static boolean serviceRunning = false;

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
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.my_widget);
        Intent intent = new Intent(context, MyWidget.class);
        intent.setAction(SETTINGS_ACTIVITY);
        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.settings_button, actionPendingIntent);

        intent = new Intent(context, MyWidget.class);
        intent.setAction(START_SERVICE);
        actionPendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.startButton, actionPendingIntent);

        intent = new Intent(context, MyWidget.class);
        intent.setAction(PAUSE_SERVICE);
        actionPendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.pauseButton, actionPendingIntent);

        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);

    }

    @Override
    public void onAppWidgetOptionsChanged(Context context,
                                          AppWidgetManager appWidgetManager, int appWidgetId,
                                          Bundle newOptions) {
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals(START_SERVICE) && !MyService.serviceRunning){
            Intent startIntent = new Intent(context, MyService.class);
            context.startService(startIntent);
        }
        else if (intent.getAction().equals(PAUSE_SERVICE) && MyService.serviceRunning) {
            Intent pauseIntent = new Intent(context, MyService.class);
            context.stopService(pauseIntent);
        }
        else if (intent.getAction().equals(SETTINGS_ACTIVITY)){
            Intent settingsIntent = new Intent(context, MainActivity.class);
            context.startActivity(settingsIntent);
        }
        else {
            super.onReceive(context, intent);
        }
    }
}