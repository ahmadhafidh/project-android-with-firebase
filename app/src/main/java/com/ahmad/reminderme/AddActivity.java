package com.ahmad.reminderme;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class AddActivity extends AppCompatActivity {

    private TimePickerDialog picker;
    private EditText et_waktu, et_aktivitas;
    private Button btn_simpan, btn_kembali;
    private int minutes = -1, dayIdx, count;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        preferences = getSharedPreferences("settings", MODE_PRIVATE);

        et_waktu = findViewById(R.id.et_waktu);
        et_aktivitas = findViewById(R.id.et_aktivitas);

        btn_kembali = findViewById(R.id.btn_kembali);
        btn_simpan = findViewById(R.id.btn_simpan_baru);

        if(getIntent().hasExtra("hari")){
            dayIdx = getIntent().getIntExtra("hari",-1);
        }
        if(getIntent().hasExtra("count")){
            count = getIntent().getIntExtra("count", -1);
        }

        btn_kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(0);
                finish();
            }
        });

        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                if (!et_aktivitas.getText().toString().isEmpty() && minutes != -1) {
                    final ProgressDialog pd = new ProgressDialog(AddActivity.this);
                    pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pd.setTitle("Mohon Tunggu");
                    pd.setMessage("Sedang menyimpan...");
                    pd.setCanceledOnTouchOutside(false);
                    pd.show();

                    Activity activity = new Activity(et_aktivitas.getText().toString(), minutes, dayIdx,count+1, true);

                    FirebaseDatabase
                            .getInstance()
                            .getReference()
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(String.valueOf(dayIdx))
                            .child(String.valueOf(count+1))
                            .setValue(activity)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                    public void onSuccess(Void aVoid) {
                                    pd.dismiss();

                                    preferences.edit().putInt("count", count+1).apply();

                                    startService(new Intent(getApplicationContext(), SyncService.class).putExtra("valid", true));

                                    setResult(1,new Intent().putExtra("count",count+1));
                                    finish();
                                    }
                                })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                } else {
                    Toast.makeText(getApplicationContext(), "Belum Lengkap", Toast.LENGTH_SHORT).show();
                }
            }
        });

        et_waktu.setInputType(InputType.TYPE_NULL);
        et_waktu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                picker = new TimePickerDialog(AddActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                AddActivity.this.minutes = (sHour * 60) + sMinute;
                                et_waktu.setText(String.format("%02d:%02d", sHour, sMinute));
                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });
    }
}
