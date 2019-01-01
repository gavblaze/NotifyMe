package com.gavblaze.android.notifyme;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import static android.support.v4.app.NotificationCompat.DEFAULT_ALL;
import static android.support.v4.app.NotificationCompat.PRIORITY_HIGH;

public class MainActivity extends AppCompatActivity {
    private Button button_notify;
    private Button button_cancel;
    private Button button_update;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String CHANNEL_ID = "primary_notification_channel";
    private static final int NOTIFICATION_ID = 12;
    private BroadcastReceiver mBroadcastReceiver;
    private static final String ACTION_UPDATE_NOTIFICATION = BuildConfig.APPLICATION_ID + ".ACTION_UPDATE_NOTIFICATION";
    private static final String ACTION_CANCEL_NOTIFICATION = BuildConfig.APPLICATION_ID + ".ACTION_CANCEL_NOTIFICATION";
    private static final String ACTION_USER_DELETE_NOTIFICATION = BuildConfig.APPLICATION_ID + ".ACTION_USER_DELETE_NOTIFICATION";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();

        mBroadcastReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_UPDATE_NOTIFICATION);
        filter.addAction(ACTION_CANCEL_NOTIFICATION);
        filter.addAction(ACTION_USER_DELETE_NOTIFICATION);
        this.registerReceiver(mBroadcastReceiver, filter);


        button_notify = findViewById(R.id.notify);

        button_notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendNotification();
            }
        });

        button_update = findViewById(R.id.update);
        button_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Update the notification
                updateNotification();
            }
        });

        button_cancel = findViewById(R.id.cancel);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Cancel the notification
                cancelNotification();
            }
        });

        setNotificationButtonState(true, false, false);
    }

    public void sendNotification() {

        NotificationCompat.Builder mBuilder = getNotificationBuilder();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        setNotificationButtonState(false, true, true);
    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "This is my channel";
            String description = "Channel description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLightColor(Color.RED);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public void updateNotification() {
        // Load the drawable resource into the a bitmap image.
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mascot_1);
        // Build the notification with all of the parameters using helper
        // method.
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        // Update the notification style to BigPictureStyle.
        notifyBuilder.setStyle(new NotificationCompat.BigPictureStyle()
        .bigPicture(bitmap)
        .setBigContentTitle("Notification Updated!"));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // Deliver the notification.
        notificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());

        setNotificationButtonState(false, false, true);

    }

    public void cancelNotification() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(NOTIFICATION_ID);

        setNotificationButtonState(true, false, false);
    }

    private NotificationCompat.Builder getNotificationBuilder() {

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID , intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent cancelIntent = new Intent(ACTION_CANCEL_NOTIFICATION);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, cancelIntent, PendingIntent.FLAG_ONE_SHOT);

        Intent updateIntent = new Intent(ACTION_UPDATE_NOTIFICATION);
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, updateIntent, 0);

        Intent userDeleteIntent = new Intent(ACTION_USER_DELETE_NOTIFICATION);
        PendingIntent userDeletePendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, userDeleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_android)
                .setContentTitle("You've been notified!")
                .setContentText("This is your notification text.")
                .setContentIntent(pendingIntent)
                .setPriority(PRIORITY_HIGH)
                .setDefaults(DEFAULT_ALL)
                .addAction(R.drawable.ic_cancel, "Cancel", cancelPendingIntent)
                .addAction(R.drawable.ic_update, "Update", updatePendingIntent)
                .setDeleteIntent(userDeletePendingIntent)
                .setAutoCancel(true);
    }


    public void setNotificationButtonState (Boolean isNotifyEnabled, Boolean isUpdateEnabled, Boolean isCancelEnabled) {
        button_notify.setEnabled(isNotifyEnabled);
        button_update.setEnabled(isUpdateEnabled);
        button_cancel.setEnabled(isCancelEnabled);

    }

    @Override
    protected void onDestroy() {
        this.unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    private class NotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(LOG_TAG, "TEST.........onReceive() called");

            if (intent.getAction() != null) {
                switch (intent.getAction()) {
                    case ACTION_CANCEL_NOTIFICATION:
                        cancelNotification();
                        break;
                    case ACTION_UPDATE_NOTIFICATION:
                        updateNotification();
                        break;
                    case ACTION_USER_DELETE_NOTIFICATION:
                        setNotificationButtonState(true, false , false);
                    default:
                        break;
                }
            }
        }
    }
}
