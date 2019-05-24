package com.ahmad.reminderme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingActivity extends AppCompatActivity {

    private Button btn_simpan;
    private SharedPreferences sharedPreferences;
    private int selectedTiming, selectedTone;
    private Spinner sp_timing, sp_tone;
    private boolean vibrated;
    private Ringtone ringtone;
    private RadioGroup radioGroup;
    private Handler handler;
    private Runnable runnable;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                ringtone.stop();
            }
        };

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);

        selectedTiming = sharedPreferences.getInt("timing", 0);
        selectedTone = sharedPreferences.getInt("tone", 0);
        vibrated = sharedPreferences.getBoolean("vibrate", true);

        sp_timing = findViewById(R.id.sp_timing);
        btn_simpan = findViewById(R.id.btn_simpan_settings);
        sp_tone = findViewById(R.id.sp_bunyi);
        radioGroup = findViewById(R.id.rg_getar);

        if(vibrated) {
            radioGroup.check(R.id.rb_ya);
        }
        else {
            radioGroup.check(R.id.rb_tidak);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rb_ya){
                    vibrated = true;
                    vibrator.vibrate(1000);
                }else if(checkedId == R.id.rb_tidak){
                    vibrated = false;
                }
            }
        });

        List<String> timingList = new ArrayList<>();
        timingList.add(" 3 menit");
        timingList.add(" 5 menit");
        timingList.add("10 menit");
        timingList.add("15 menit");
        timingList.add("30 menit");
        timingList.add("45 menit");
        timingList.add("60 menit");

        CustomArrayAdapter timingAdapter = new CustomArrayAdapter(this, timingList);
        timingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sp_timing.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTiming = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sp_timing.setAdapter(timingAdapter);
        sp_timing.setSelection(selectedTiming);


        final List<String> toneList = new ArrayList<>(getRingtones().keySet());

        CustomArrayAdapter toneAdapter = new CustomArrayAdapter(this, toneList);
        toneAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sp_tone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(selectedTone != position){
                    selectedTone = position;
                    if(ringtone != null && ringtone.isPlaying()){
                        handler.removeCallbacks(runnable);
                        ringtone.stop();
                    }

                    ringtone = RingtoneManager
                            .getRingtone(getApplicationContext(),
                                    Uri.parse(getRingtones().get(toneList.get(position))));

                    ringtone.play();
                    handler.postDelayed(runnable,5000);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                }
        });
        sp_tone.setAdapter(toneAdapter);
        sp_tone.setSelection(selectedTone);

        btn_simpan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("vibrate", vibrated);
                    editor.putInt("timing", selectedTiming);
                    editor.putInt("tone", selectedTone);
                    editor.apply();
                    startService(new Intent(getApplicationContext(), SyncService.class).putExtra("valid", true));
                    finish();
                }
            });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(ringtone != null){
            ringtone.stop();
        }
    }
}
