package com.example.newzapp;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Map<String,Object> tokenData = new HashMap<>();
        tokenData.put("token",s);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("DeviceTokens").document().set(tokenData);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        String title = message.getNotification().getTitle();
        String body = message.getNotification().getBody();
        final String Chanel_id = "HEADS_UP_NOTIFICATIONS";
        NotificationChannel channel = new NotificationChannel(
                Chanel_id,
                "MyNotification",
                NotificationManager.IMPORTANCE_HIGH);
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this, Chanel_id).setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_news)
                .setAutoCancel(true);
        if (ActivityCompat.checkSelfPermission(this, "android.permission.POST_NOTIFICATIONS") != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        NotificationManagerCompat.from(this).notify(1, notification.build());
    }
}

