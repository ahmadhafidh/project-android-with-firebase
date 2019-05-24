package com.ahmad.reminderme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity {

    private TextView tv_activity, tv_close, tv_share;
    private String message, activity;
    private Ringtone ringtone;
    private Vibrator vibrator;
    private SharedPreferences preferences;
    private int selectedTone;
    private boolean vibrated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        preferences = getSharedPreferences("settings", MODE_PRIVATE);
        selectedTone = preferences.getInt("tone", 0);
        vibrated = preferences.getBoolean("vibrate", true);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        tv_activity = findViewById(R.id.tv_activity_notif);
        tv_close = findViewById(R.id.tv_close);
        tv_share = findViewById(R.id.tv_share);

        if(getIntent().hasExtra("activity")){
            activity = getIntent().getStringExtra("activity");
        }

        message = "Saatnya anda " + activity + "!";
        tv_activity.setText(message);

        List<String> tones = new ArrayList<>(getRingtones().keySet());


        Uri toneUri = Uri.parse(getRingtones().get(tones.get(selectedTone)));

        ringtone = RingtoneManager.getRingtone(getApplicationContext(), toneUri);
        ringtone.play();

        long[] pattern = new long[]{0,400,800,600,800,800,800,1000};

        if(vibrated){
            vibrator.vibrate(pattern,0);
        }

        tv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = "Hi, Saya sedang menggunakan aplikasi Reminder Me dan kegiatan saya saat ini " + activity;
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, s);
                startActivity(Intent.createChooser(sharingIntent, "Bagikan melalui"));
                finish();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        ringtone.stop();
        vibrator.cancel();
    }

    private Map<String, String> getRingtones(){
        RingtoneManager manager = new RingtoneManager(this);
        manager.setType(RingtoneManager.TYPE_RINGTONE);
        Cursor cursor = manager.getCursor();

        Map<String, String> ringtones = new HashMap<>();
        while (cursor.moveToNext()){
            String title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            String uri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX) + "/" + cursor.getString(RingtoneManager.ID_COLUMN_INDEX);

            ringtones.put(title, uri);
        }
        return  ringtones;
    }
}
