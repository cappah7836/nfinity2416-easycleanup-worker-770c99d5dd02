package com.app.easycleanup.network;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.app.easycleanup.R;
import com.app.easycleanup.activities.ProfileActivity;


public class ReminderBroadcast extends BroadcastReceiver {

    private static final String CHANNEL_ID = "CHECKOUT ALERTER";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, ProfileActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,i,0);
        NotificationCompat.Builder builder = new  NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ecu_icon)
                .setContentTitle("ALERT!")
                .setContentText("PLEASE FOLLOW UP")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Don't forget to checkout when you leave otherwise you will be consider as an absent."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(200, builder.build());

    }
}
