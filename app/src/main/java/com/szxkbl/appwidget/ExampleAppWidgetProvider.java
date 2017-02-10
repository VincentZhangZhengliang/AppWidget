package com.szxkbl.appwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author : Vincent
 * @time : 2017/2/9 17:13.
 * @Discription :
 */

public class ExampleAppWidgetProvider extends AppWidgetProvider {
    private static final String TAG = "ExampleAppWidgetProvide";
    private boolean DEBUG = false;
    private static final int[] ARR_IMAGES = {
        R.drawable.ic_filter_1_black_24dp,
        R.drawable.ic_filter_2_black_24dp,
        R.drawable.ic_filter_3_black_24dp,
        R.drawable.ic_filter_4_black_24dp,
        R.drawable.ic_filter_5_black_24dp,
        R.drawable.ic_filter_6_black_24dp,
    };

    private final Intent EXAMPLE_SERVICE_INTENT =
            new Intent("android.appwidget.action.EXAMPLE_APP_WIDGET_SERVICE");
    private final String ACTION_UPDATE_ALL = "com.skywang.widget.UPDATE_ALL";

    private static Set idsSet = new HashSet();
    private static final int BUTTON_SHOW = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        Log.d(TAG, "OnReceive:Action: " + action);
        if (ACTION_UPDATE_ALL.equals(action)) {
            // “更新”广播
            updateAllAppWidgets(context, AppWidgetManager.getInstance(context), idsSet);
        } else if (intent.hasCategory(Intent.CATEGORY_ALTERNATIVE)) {
            // “按钮点击”广播
            Uri data = intent.getData();
            int buttonId = Integer.parseInt(data.getSchemeSpecificPart());
            if (buttonId == BUTTON_SHOW) {
                Log.d(TAG, "Button wifi clicked");
                Toast.makeText(context, "Button Clicked", Toast.LENGTH_SHORT).show();
                updateAllAppWidgets(context,AppWidgetManager.getInstance(context), idsSet);
            }
        }

        super.onReceive(context, intent);
        Log.e(TAG, "onReceive: ++++++++++++++");
    }

    private void updateAllAppWidgets(Context context, AppWidgetManager appWidgetManager, Set set) {
        Log.d(TAG, "updateAllAppWidgets(): size="+set.size());

        // widget 的id
        int appID;
        // 迭代器，用于遍历所有保存的widget的id
        Iterator it = set.iterator();

        while (it.hasNext()) {
            appID = ((Integer)it.next()).intValue();
            // 随机获取一张图片
            int index = (new java.util.Random().nextInt(ARR_IMAGES.length));

            if (DEBUG) Log.d(TAG, "onUpdate(): index="+index);
            // 获取 example_appwidget.xml 对应的RemoteViews
            RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.example_widget);

            // 设置显示图片
            remoteView.setImageViewResource(R.id.iv_show, ARR_IMAGES[index]);

            // 设置点击按钮对应的PendingIntent：即点击按钮时，发送广播。
            remoteView.setOnClickPendingIntent(R.id.id_next, getPendingIntent(context,
                    BUTTON_SHOW));

            // 更新 widget
            appWidgetManager.updateAppWidget(appID, remoteView);
        }
    }

    private PendingIntent getPendingIntent(Context context, int buttonId) {
        Intent intent = new Intent();
        intent.setClass(context, ExampleAppWidgetProvider.class);
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        intent.setData(Uri.parse("custom:" + buttonId));
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0 );
        return pi;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.e(TAG, "onUpdate: ++++++++++++");

        for (int appWidgetId : appWidgetIds) {
            idsSet.add(Integer.valueOf(appWidgetId));
        }
        prtSet();
    }

    private void prtSet() {
        if (DEBUG) {
            int index = 0;
            int size = idsSet.size();
            Iterator it = idsSet.iterator();
            Log.d(TAG, "total:"+size);
            while (it.hasNext()) {
                Log.d(TAG, index + " -- " + ((Integer)it.next()).intValue());
            }
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        Log.e(TAG, "onAppWidgetOptionsChanged: ++++++++++++");
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.e(TAG, "onDeleted: ++++++++++++");

        for (int appWidgetId : appWidgetIds) {
            idsSet.remove(Integer.valueOf(appWidgetId));
        }
        prtSet();
        super.onDeleted(context, appWidgetIds);

    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Intent intent = new Intent(context,ExampleAppWidgetService.class);
        context.stopService(intent);
        Log.e(TAG, "onDisabled: ++++++++++");
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Intent intent = new Intent(context,ExampleAppWidgetService.class);
        context.startService(intent);
        Log.e(TAG, "onEnabled: +++++++++++");
    }
}
