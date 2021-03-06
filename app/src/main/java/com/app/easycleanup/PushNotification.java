//package com.app.easycleanup;
//
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Intent;
//import android.graphics.Color;
//import android.media.RingtoneManager;
//import android.net.Uri;
//import android.os.Build;
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.RequiresApi;
//import androidx.core.app.NotificationCompat;
//import androidx.core.app.NotificationManagerCompat;
//import androidx.core.content.res.ResourcesCompat;
//
//import com.app.easycleanup.activities.EmployeePlaningActivity;
//import com.app.easycleanup.activities.ProfileActivity;
//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;
//
//import static com.app.easycleanup.utils.AppController.FCM_CHANNEL_ID;
//
//
//public class PushNotification extends FirebaseMessagingService {
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    @Override
//    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
//        Log.d("recTag", "onMessageReceived: called");
//
//        Log.d("recTag", "onMessageReceived: Received From firebase" +remoteMessage.getFrom());
//
//        if(remoteMessage.getNotification() != null){
//            String title =remoteMessage.getNotification().getTitle();
//            String text =remoteMessage.getNotification().getBody();
//
//            Intent i = new Intent(this, ProfileActivity.class);
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i,0);
//            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//            NotificationChannel channel = new NotificationChannel(
//                    FCM_CHANNEL_ID,
//                    "ALERT NOTIFICATION",
//                    NotificationManager.IMPORTANCE_HIGH);
//            getSystemService(NotificationManager.class).createNotificationChannel(channel);
//            Notification.Builder notification = new Notification.Builder(this, FCM_CHANNEL_ID)
//                    .setContentTitle(title)
//                    .setContentText(text)
//                    .setColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null))
//                    .setSound(uri)
//                    .setSmallIcon(R.drawable.ecu_icon)
//                    .setAutoCancel(true)
//                    .setContentIntent(pendingIntent);
//
//            NotificationManagerCompat.from(this).notify(1,notification.build());
//        }
//        if(remoteMessage.getData().size() >0 ){
//            Log.d("TAG", "onMessageReceived: Data: "+remoteMessage.getData().toString());
//        }
//    }
//
//    @Override
//    public void onDeletedMessages() {
//        super.onDeletedMessages();
//        Log.d("delTag", "onDeletedMessages: called");
//    }
//
//    @Override
//    public void onNewToken(@NonNull String s) {
//        super.onNewToken(s);
//        Log.d("tokenTag", "onNewToken: called");
//        //upload token on app server
//    }
//}

package com.app.easycleanup;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.app.easycleanup.activities.LoginActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static com.app.easycleanup.utils.AppController.FCM_CHANNEL_ID;


public class PushNotification extends FirebaseMessagingService {

    private static final String TAG = "MyTag";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG,"onMessage Received Called:");

        Log.d(TAG,"Sender Id:"+remoteMessage.getFrom());
        if(remoteMessage.getNotification()!=null){
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            Intent activityIntent = new Intent(getApplicationContext(), LoginActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(),0,activityIntent,0);

            Notification notification = new NotificationCompat.Builder(this,FCM_CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .setColor(Color.GREEN)
                    .build();

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(1003,notification);
        }
        if(remoteMessage.getData().size()>0){
            Log.d(TAG,"onMessageReceived: Data: "+remoteMessage.getData().toString());
        }

    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
        Log.d(TAG,"onDeletedMessages  Called:");
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d(TAG,"OnNewToken  Called:");

    }
}

