package com.chat.befiler.push;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.chat.befiler.activities.LoginActivity;
import com.chat.befiler.activities.MainActivity;
import com.chat.befiler.commons.Common;
import com.chat.befiler.fragments.ConversationsDetailFragment;
import com.chat.befiler.fragments.ConversationsFragment;
import com.example.signalrtestandroid.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.Random;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessagingService";
    Bitmap bitmap_image = null;
    Common commons = null;

    @Override
    public void onCreate() {
        super.onCreate();
        commons = new Common();
        //bitmap_image = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.notification_ic);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM:", "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    // Get new FCM registration token
                    String token = task.getResult();
                    //Freshchat.getInstance(getApplicationContext()).setPushRegistrationToken(token);
                    if(commons!=null){
                        commons.saveDeviceToken(getApplicationContext(),token);
                    }
                    // Log and toast
                    //Log.d("FCM:", token);
                });
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

            // TODO(developer): Handle FCM messages here.
            // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
            Log.d(TAG, "From:  Fire Base" + remoteMessage.getFrom());

            // Check if message contains a data payload.
            if (remoteMessage.getData().size() > 0) {
                Log.d(TAG, "Message data payload: " + remoteMessage.getData());

                // Handle message within 10 seconds
                sendNotification(remoteMessage,false);
            }

            // Check if message contains a notification payload.
            if (remoteMessage.getNotification() != null) {
                Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
                sendNotification(remoteMessage,true);
            }
    }

    @SuppressLint("WrongConstant")
    private void sendNotification(RemoteMessage remoteMessage, boolean isContainNotificationPayload) {
        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        Intent intent;
        String notificationId = "";
        String conversationByUID = "";
        String recordId = "";
        String imageUrl = "";

        if (remoteMessage.getData().containsKey("notificationId") && remoteMessage.getData().get("notificationId") !=null){
            notificationId = remoteMessage.getData().get("notificationId");
        }
        if (remoteMessage.getData().containsKey("conversationuid") && remoteMessage.getData().get("conversationuid") !=null){
            conversationByUID = remoteMessage.getData().get("conversationuid");
        }
        if (remoteMessage.getData().containsKey("recordId") && remoteMessage.getData().get("recordId") !=null){
            recordId = remoteMessage.getData().get("recordId");
        }

        if (remoteMessage.getData().containsKey("imageUrl") && remoteMessage.getData().get("imageUrl") !=null){
            imageUrl = remoteMessage.getData().get("imageUrl");
        }

        if(!commons.getIsLoggedIn(this)) {
            intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("conversationByUID",conversationByUID);
        } else {
            intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("conversationByUID",conversationByUID);
        }
        PendingIntent pendingIntent;
        int requestCode = new Random().nextInt();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        }else {
            pendingIntent = PendingIntent.getActivity(this, m, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        }

        String channelId = "channelId1";
        NotificationCompat.Builder notificationBuilder;
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if(isContainNotificationPayload){
            notificationBuilder = new NotificationCompat.Builder(this, channelId)
                    .setContentTitle(Objects.requireNonNull(remoteMessage.getNotification()).getTitle())
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setLargeIcon(bitmap_image)
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .setContentIntent(pendingIntent)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(remoteMessage.getNotification().getBody()));
            notificationBuilder.setSmallIcon(getNotificationIcon(notificationBuilder));

        }else{
            if (imageUrl!=null && !imageUrl.isEmpty()){
                notificationBuilder = generateImageNotification(bitmap_image,defaultSoundUri,imageUrl,pendingIntent,remoteMessage,channelId);
            }else{
                notificationBuilder = new NotificationCompat.Builder(this, channelId)
                        .setContentTitle(remoteMessage.getData().get("title"))
                        .setContentText(remoteMessage.getData().get("body"))
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setLargeIcon(bitmap_image)
                        .setPriority(NotificationManager.IMPORTANCE_HIGH)
                        .setContentIntent(pendingIntent)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(remoteMessage.getData().get("body")));
                notificationBuilder.setSmallIcon(getNotificationIcon(notificationBuilder));
            }
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(m, notificationBuilder.build());
    }

    public NotificationCompat.Builder generateImageNotification(Bitmap bitmap_image,Uri defaultSoundUri,String imageUrl,PendingIntent intent,RemoteMessage remoteMessage,String channelId){

        NotificationCompat.Builder notificationBuilderObj = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("body"))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setLargeIcon(bitmap_image)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setContentIntent(intent)
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(getBitmapFromURL(imageUrl)));
        notificationBuilderObj.setSmallIcon(getNotificationIcon(notificationBuilderObj));

        return notificationBuilderObj;
    }

    /* Downloading push notification image before displaying it in
     * the notification tray
     */
    public Bitmap getBitmapFromURL(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = null;
            try {
                input = connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private int getNotificationIcon(NotificationCompat.Builder notificationBuilder) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            notificationBuilder.setColor(getResources().getColor(com.google.android.material.R.color.design_default_color_on_primary));
            return R.mipmap.ic_launcher;

        }
        return R.mipmap.ic_launcher;
    }
    @Override
    public void onNewToken(String token) {
        Log.d("FCM", "Refreshed token: " + token);
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        // sendRegistrationToServer(token);
        if(commons!=null){
            commons.saveDeviceToken(getApplicationContext(),token);
        }
    }

}
