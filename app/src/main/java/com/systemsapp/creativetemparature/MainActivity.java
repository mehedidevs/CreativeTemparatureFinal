package com.systemsapp.creativetemparature;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    public static final int SYSTEM_ALERT_WINDOW_PERMISSION = 7;
    Button button, notific;
    private static RemoteViews contentView;
    private static Notification notification;
    private static NotificationManager notificationManager;
    private static final int NotificationID = 1005;
    private static NotificationCompat.Builder mBuilder;

    TextView tempt_float, status_txt;
    Button call;
    MediaPlayer mediaPlayer;
    String tempt;
    float int_tempt;
    Animation animBlink;
    String store_string;
    Integer last_numb;
    FirebaseAuth mAuth;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        tempt_float = findViewById(R.id.tempt_float);
        DatabaseConnection();
        button = (Button) findViewById(R.id.buttonShow);
        status_txt = findViewById(R.id.status_id);


        mAuth = FirebaseAuth.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

            RuntimePermissionForUser();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

                    startService(new Intent(MainActivity.this, FloatingWidgetShowService.class));

                    finish();

                } else if (Settings.canDrawOverlays(MainActivity.this)) {

                    startService(new Intent(MainActivity.this, FloatingWidgetShowService.class));

                    finish();

                } else {
                    RuntimePermissionForUser();

                    Toast.makeText(MainActivity.this, "System Alert Window Permission Is Required For Floating Widget.", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private void DatabaseConnection() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Temperature");
        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //  tempt_float.setText((dataSnapshot.getValue()).toString() + " \u2109");


                // DataSnapshot   snapshot : dataSnapshot.getChildren();
                tempt = dataSnapshot.getValue().toString();


                //    String temptString = String.valueOf(int_tempt);


                tempt_float.setText((tempt) + "\u00B0" + "C");

                store_string = tempt;

                if (tempt != null) {
                    int_tempt = Float.parseFloat(tempt);
                }


                tempDetect();
             /*   for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String tempt = snapshot.getValue().toString();
                    temtView.setText((tempt) + " \u2109");
                    tempt_float.setText((tempt) + " \u2109");


                }*/

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void RuntimePermissionForUser() {

        Intent PermissionIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));

        startActivityForResult(PermissionIntent, SYSTEM_ALERT_WINDOW_PERMISSION);
    }

    private void warningStart() {
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm_c);
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();

        }
    }

    private void tempDetect() {


        if (int_tempt >= 27.0) {
            warningStart();
            tempt_float.setTextColor(getResources().getColor(R.color.red));

            animBlink = AnimationUtils.loadAnimation(this,
                    R.anim.blink);
            tempt_float.setAnimation(animBlink);
            vibrate_me();
            status_txt.setText("Temperature is very High!");
            status_txt.setTextColor(getResources().getColor(R.color.red));
            status_txt.setAnimation(animBlink);

        } else if (int_tempt < 27.0) {
            animBlink = AnimationUtils.loadAnimation(this,
                    R.anim.blink_blank);

            tempt_float.setTextColor(getResources().getColor(R.color.white));

            animBlink = AnimationUtils.loadAnimation(this,
                    R.anim.blink_blank);
            tempt_float.setAnimation(animBlink);

            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            status_txt.setText("Temperature is ok now!");
            status_txt.setTextColor(getResources().getColor(R.color.green));
            status_txt.setAnimation(animBlink);

        }


    }

    private void vibrate_me() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(5000);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.setLooping(true);
        }
    }

    public void call(View view) {
        String phone = getString(R.string.office_emergency_no);
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
    }

    public void notif(View view) {


    }

    public void sign_out(View view) {

        mAuth.signOut();
        Intent intent = new Intent(getApplicationContext(), Sign_In_Activity.class);
        startActivity(intent);
        finish();
    }
}