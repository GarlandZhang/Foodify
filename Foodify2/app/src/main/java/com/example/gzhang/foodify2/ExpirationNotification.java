package com.example.gzhang.foodify2;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.Toast;

/**
 * Created by GZhang on 2017-12-28.
 */

public class ExpirationNotification extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extra = intent.getBundleExtra("extra");
        String foodName = extra.getString("FoodName");
        System.out.println("In ExpirationNotification: " + foodName);
        Toast.makeText(context, "YOUR " + foodName.toUpperCase() + " EXPIRES IN 1 DAY!", Toast.LENGTH_LONG).show();
        Vibrator v = (Vibrator)context.getSystemService(context.VIBRATOR_SERVICE);
        v.vibrate(1000);
    }
}
