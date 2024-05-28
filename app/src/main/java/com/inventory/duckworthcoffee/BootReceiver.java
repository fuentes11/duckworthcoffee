package com.inventory.duckworthcoffee;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("BOOT_CHECK", "Receiver Arrive_1");
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.e("BOOT_CHECK", "Receiver Arrive_2");

            // Agregar un retraso antes de iniciar MainActivity
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(context, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                    Log.e("BOOT_CHECK", "MainActivity started directly");
                }
            }, 5000); // Retraso de 5 segundos (puedes ajustar este valor según sea necesario)
        }
        Log.e("BOOT_CHECK", "Receiver Arrive_3");
    }
}
