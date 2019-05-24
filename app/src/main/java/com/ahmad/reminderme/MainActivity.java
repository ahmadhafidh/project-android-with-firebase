package com.ahmad.reminderme;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private ImageButton ib_setting;

    private Button[] btn_hari = new Button[7];
    private static final int[] btn_hari_id = {
            R.id.btn_senin,
            R.id.btn_selasa,
            R.id.btn_rabu,
            R.id.btn_kamis,
            R.id.btn_jumat,
            R.id.btn_sabtu,
            R.id.btn_minggu,
    };
    private SharedPreferences preferences;
    private int count;
    private TextView tv_welcoming;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            setContentView(R.layout.activity_main);

            preferences = getSharedPreferences("settings", MODE_PRIVATE);

            ib_setting = findViewById(R.id.ib_setting);
            tv_welcoming = findViewById(R.id.tv_welcoming);

            String text = "Hi, " + user.getDisplayName();

            tv_welcoming.setText(text);

            tv_welcoming.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                    String message = "";
                    message += "Username: ";
                    message += user.getDisplayName();
                    message += "\nEmail   : ";
                    message += user.getEmail();
                    adb.setIcon(getResources().getDrawable(R.drawable.person));
                    adb.setPositiveButton("Kembali", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    adb.setNegativeButton("Logout", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            finish();
                        }
                    });
                    adb.show();
                }
            });

            ib_setting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                    startActivity(intent);
                }
            });

            for (int i = 0; i <7 ; i++) {
                final int hari_idx = i;
                btn_hari[i] = findViewById(btn_hari_id[i]);
                btn_hari[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        count = preferences.getInt("count", 0);

                        Intent intent = new Intent(MainActivity.this, CustomizeActivity.class);
                        intent.putExtra("hari", hari_idx);
                        intent.putExtra("count", count);
                        startActivity(intent);
                    }
                });
            }

            startService(new Intent(getApplicationContext(), SyncService.class));

            if(count == 0){
                final ProgressDialog pd = new ProgressDialog(MainActivity.this);
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setTitle("Mohon Tunggu");
                pd.setMessage("Sedang memuat...");
                pd.setCanceledOnTouchOutside(false);
                pd.show();

                FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    count += child.getChildrenCount();
                                }
                                preferences.edit().putInt("count", count).apply();
                                pd.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
        }else{
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    }
}
