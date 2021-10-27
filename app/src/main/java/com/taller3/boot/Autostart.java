package com.taller3.boot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class Autostart extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent arg)
    {
        Log.i("Autostart", "started");
        if(arg.getAction() == Intent.ACTION_BOOT_COMPLETED)
        {
            Intent intent= new Intent(context, BackgroundBootService.class);
            if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O)
            {
                context.startForegroundService(intent);
            }
            else
            {
                context.startService(intent);
            }
            Log.i("Autostart", "started");
        }
    }

}
