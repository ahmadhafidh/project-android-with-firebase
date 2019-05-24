package com.ahmad.reminderme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(final Context context, Intent intent) {
        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent i = new Intent(context, SyncService.class).putExtra("valid", true);
            context.startService(i);
        }
    }
}
