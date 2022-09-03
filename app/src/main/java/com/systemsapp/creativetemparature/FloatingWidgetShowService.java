package com.systemsapp.creativetemparature;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FloatingWidgetShowService extends Service {

    TextView temtView, tempt_float, tempt_store;
    Button call;
    WindowManager windowManager;
    View floatingView, collapsedView, expandedView;
    WindowManager.LayoutParams params;
    MediaPlayer mediaPlayer;
    String tempt;
    float int_tempt;
    Animation animBlink;
    String store_string;
    Integer last_numb;

    Context context;

    public FloatingWidgetShowService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Temperature");
        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //  tempt_float.setText((dataSnapshot.getValue()).toString() + " \u2109");


                // DataSnapshot   snapshot : dataSnapshot.getChildren();
                tempt = dataSnapshot.getValue().toString();


                //    String temptString = String.valueOf(int_tempt);

                temtView.setText((tempt) + "\u00B0" + "C");
                tempt_float.setText((tempt) + "\u00B0" + "C");
                tempt_store.setText(tempt);
                store_string = tempt;

                if (tempt != null) {
                    int_tempt = Float.parseFloat(tempt);
                }


                Toast.makeText(FloatingWidgetShowService.this, tempt, Toast.LENGTH_SHORT).show();

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

        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_widget_layout, null);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    //for oreo
                              //oreo

                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);




        }else {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    //for oreo
                    WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,

                    //oreo

                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);

        }


        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        windowManager.addView(floatingView, params);

        expandedView = floatingView.findViewById(R.id.Layout_Expended);
        temtView = floatingView.findViewById(R.id.tempt);
        tempt_float = floatingView.findViewById(R.id.tempt_float);


        tempt_store = floatingView.findViewById(R.id.tempt_store);
        call = floatingView.findViewById(R.id.call);

        call = floatingView.findViewById(R.id.call);
        tempt_float = floatingView.findViewById(R.id.tempt_float);
        collapsedView = floatingView.findViewById(R.id.Layout_Collapsed);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);

                String phone = getString(R.string.office_emergency_no);
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }


            }
        });

        floatingView.findViewById(R.id.Widget_Close_Icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stopSelf();

            }
        });

        expandedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);

            }
        });

        floatingView.findViewById(R.id.MainParentRelativeLayout).setOnTouchListener(new View.OnTouchListener() {
            int X_Axis, Y_Axis;
            float TouchX, TouchY;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        X_Axis = params.x;
                        Y_Axis = params.y;
                        TouchX = event.getRawX();
                        TouchY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_UP:

                        collapsedView.setVisibility(View.GONE);
                        expandedView.setVisibility(View.VISIBLE);
                        return true;

                    case MotionEvent.ACTION_MOVE:

                        params.x = X_Axis + (int) (event.getRawX() - TouchX);
                        params.y = Y_Axis + (int) (event.getRawY() - TouchY);
                        windowManager.updateViewLayout(floatingView, params);
                        return true;
                }
                return false;
            }
        });

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
            temtView.setTextColor(getResources().getColor(R.color.red));
            animBlink = AnimationUtils.loadAnimation(this,
                    R.anim.blink);
            tempt_float.setAnimation(animBlink);
            temtView.setAnimation(animBlink);

        } else if (int_tempt < 27.0) {

            tempt_float.setTextColor(getResources().getColor(R.color.black));
            temtView.setTextColor(getResources().getColor(R.color.black));

            animBlink = AnimationUtils.loadAnimation(this,
                    R.anim.blink_blank);
            tempt_float.setAnimation(animBlink);
            temtView.setAnimation(animBlink);
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
        }

    /*    if (store_string!=null){
            if (store_string.equals("27.0")||store_string.equals("28.0")||store_string.equals("29.0")||store_string.equals("30.0")) {
      *//*     *//*
                warningStart();

            }
        }*/


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingView != null) windowManager.removeView(floatingView);

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }

    }


}