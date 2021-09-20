package com.app.easycleanup.utils;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;


public class AppController extends Application implements Application.ActivityLifecycleCallbacks{
    public static AppController mInstance;
    public static final String FCM_CHANNEL_ID="ALERT_NOTIFICATIONS";
    @Override
    public void onCreate() {
        super.onCreate();

        registerActivityLifecycleCallbacks(this);
        mInstance = this;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel fcmChannel = new NotificationChannel(FCM_CHANNEL_ID,"FCM_Channel", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(fcmChannel);
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.d("activity created","true");
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.d("activity started","truei");
        //CrickSignUpLoginManager.getCrickManager().RefreshToken();
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.d("activity resume","true");
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.d("activty pause","trueeeeeee");
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.d("activity stopped","true");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Log.d("activity savedIns","true");
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
       /* AlarmBroadCast alarm = new AlarmBroadCast();
        alarm.cancelAlarm(getApplicationContext());*/
        Log.d("activity destroy","true");
    }



    public static synchronized AppController getInstance() {
        return mInstance;
    }

    /* For Saving secret token and refresh token according to mechanism
    * @param token
    * @ refreshtoken*/

}