package com.systemsapp.creativetemparature;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver
{
    public void onReceive(Context context, Intent intent)
    {
        Intent service1 = new Intent(context, FloatingWidgetShowService.class);
        context.startService(service1);
    }
}