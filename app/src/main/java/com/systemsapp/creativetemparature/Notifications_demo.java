package com.systemsapp.creativetemparature;

import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import static com.systemsapp.creativetemparature.App.CHANNEL_1_ID;
import static com.systemsapp.creativetemparature.App.CHANNEL_2_ID;

public class Notifications_demo extends AppCompatActivity {
    EditText title, description;

    NotificationManagerCompat notificationManagerCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications_demo);
        notificationManagerCompat = NotificationManagerCompat.from(this);

        title = findViewById(R.id.title);
        description = findViewById(R.id.desc);
    }

    public void Channel1(View view) {

        String myTitle = title.getText().toString();
        String mydesc = description.getText().toString();


        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(myTitle)
                .setContentInfo(mydesc)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManagerCompat.notify(1, notification);

    }

    public void Channel2(View view) {


        String myTitle = title.getText().toString();
        String mydesc = description.getText().toString();


        Notification notification = new NotificationCompat.Builder(this, CHANNEL_2_ID)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(myTitle)
                .setContentInfo(mydesc)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManagerCompat.notify(1, notification);
    }
}
