package tr.com.mysatech.locationmonitor;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

/**
 * Created by yavuzm on 06.11.2017.
 */

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String value = "";
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals("my.speed.data")){
                    value = intent.getExtras().getString("current_speed");
                }
            }
        }

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.my_widget);
        if (!value.equals("")){
            value = String.format("%.2f", Double.parseDouble(value));
        }
        remoteViews.setTextViewText(R.id.appwidget_text, value + "km/h");
        ComponentName thisWidget = new ComponentName(context, MyWidget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(thisWidget, remoteViews);

    }
}
