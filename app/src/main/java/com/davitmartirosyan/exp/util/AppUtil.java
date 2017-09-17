package com.davitmartirosyan.exp.util;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.inputmethod.InputMethodManager;

import com.davitmartirosyan.exp.R;

import static android.content.Context.INPUT_METHOD_SERVICE;


public class AppUtil {

    public static void closeKeyboard(Activity activity) {
        if (activity != null) {
            if (activity.getCurrentFocus() != null) {
                InputMethodManager inputMethodManager =
                        (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        }
    }


    public static void sendNotification(Context context, Class cls,
                                        String title, String description, String data, int type) {

        Intent intent = new Intent(context, cls);
        intent.putExtra(Constant.Extra.EXTRA_NOTIF_DATA, data);
        intent.putExtra(Constant.Extra.EXTRA_NOTIF_TYPE, type);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        stackBuilder.addParentStack(cls);

        stackBuilder.addNextIntent(intent);

        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(type, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setSmallIcon(R.mipmap.cymetry_icn_w_copy_4)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.cymetry_icn_w_copy_4))
                .setColor(Color.WHITE)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(title)
                .setContentText(description)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(description))
                .setAutoCancel(true)
                .setContentIntent(notificationPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(type, builder.build());
    }
}
