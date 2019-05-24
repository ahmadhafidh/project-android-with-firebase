package com.ahmad.reminderme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, "Alarm Fired", Toast.LENGTH_LONG).show();
        if(intent.hasExtra("activity")){
            //Toast.makeText(context, "Alarm Valid", Toast.LENGTH_LONG).show();
            Intent i = new Intent(context, NotificationActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("activity", intent.getStringExtra("activity"));
            context.startActivity(i);
        }
    }
}
