package com.zizohanto.android.tobuy.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.widget.RemoteViews;

import com.zizohanto.android.tobuy.ui.tobuylistdetail.TobuyListDetailActivity;
import com.zizohanto.android.tobuy.ui.tobuylistdetail.TobuyListDetailFragment;
import com.zizohanto.android.tobuyList.R;

import static com.zizohanto.android.tobuy.ui.tobuylistdetail.TobuyListDetailActivity.EXTRA_TOBUYLIST_ID;

/**
 * Implementation of App Widget functionality.
 */
public class TobuyListWidgetProvider extends AppWidgetProvider {

    private PendingIntent pendingIntent;
    private AlarmManager manager;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(TobuyListDetailFragment.WIDGET_PREF, Context.MODE_PRIVATE);
        String defaultValue = context.getString(R.string.no_data);

        String tobuyListId = sharedPreferences.getString(TobuyListDetailFragment.WIDGET_PREF_TOBUYLIST_ID, defaultValue);
        String name = sharedPreferences.getString(TobuyListDetailFragment.WIDGET_NAME, defaultValue);
        String price = sharedPreferences.getString(TobuyListDetailFragment.WIDGET_PRICE, defaultValue);
        String budget = sharedPreferences.getString(TobuyListDetailFragment.WIDGET_BUDGET, defaultValue);
        String store = sharedPreferences.getString(TobuyListDetailFragment.WIDGET_STORE, defaultValue);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_tobuy_list);

        views.setTextViewText(R.id.name, name);
        views.setTextViewText(R.id.price, price);
        views.setTextViewText(R.id.budget, budget);
        views.setTextViewText(R.id.store, store);

        Intent clickIntent = new Intent(context, TobuyListDetailActivity.class);
        clickIntent.putExtra(EXTRA_TOBUYLIST_ID, tobuyListId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, clickIntent, 0);
        views.setOnClickPendingIntent(R.id.appwidget_root, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void updateWidget(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, TobuyListWidgetProvider.class));
        for (int appWidgetId : appWidgetIds) {
            TobuyListWidgetProvider.updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
            startWidgetUpdateService(context);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private void startWidgetUpdateService(Context context) {
        manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final Intent updateIntent = new Intent(context, WidgetUpdateService.class);

        if (pendingIntent == null) {
            pendingIntent = PendingIntent.getService(context, 0, updateIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        }
        manager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), 60000, pendingIntent);
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
        if (manager != null) {
            manager.cancel(pendingIntent);
        }
    }
}

