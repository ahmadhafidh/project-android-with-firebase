package com.ahmad.reminderme;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class SyncService extends Service {

    public SyncService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null){
            if(intent.hasExtra("valid")) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    FirebaseDatabase
                            .getInstance()
                            .getReference()
                            .child(user.getUid())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                                        for (DataSnapshot childChild : child.getChildren()) {
                                            Activity activity = childChild.getValue(Activity.class);

                                            if (activity != null && activity.isEnabled()) {
                                                int hour = activity.getTime() / 60;
                                                int minute = activity.getTime() % 60;

                                                Calendar calendar = Calendar.getInstance();

                                                int[] DOW = {2, 3, 4, 5, 6, 7, 1};

                                                calendar.set(Calendar.DAY_OF_WEEK, DOW[activity.getDayOfWeek()]);
                                                calendar.set(Calendar.HOUR_OF_DAY, hour);
                                                calendar.set(Calendar.MINUTE, minute);
                                                calendar.set(Calendar.SECOND, 0);

                                                int[] timingSelections = {3, 5, 10, 15, 30, 45, 60};

                                                long millis = calendar.getTimeInMillis();
                                                millis -= timingSelections[getSharedPreferences("settings", MODE_PRIVATE).getInt("timing", 0)] * 60 * 1000;

                                                long deltaT = millis - System.currentTimeMillis();

                                                if (deltaT <= 0) {
                                                    millis += (AlarmManager.INTERVAL_DAY * 7);
                                                }

                                                Intent i = new Intent(getApplicationContext(), AlarmReceiver.class);
                                                i.putExtra("activity", activity.getActivity());

                                                PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), activity.getId(), i, PendingIntent.FLAG_UPDATE_CURRENT);

                                                AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);

                                                manager.setRepeating(AlarmManager.RTC_WAKEUP, millis, (AlarmManager.INTERVAL_DAY * 7), pi);

                                                deltaT = millis - System.currentTimeMillis();
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }
            }
        }
        return Service.START_STICKY;
    }
}
