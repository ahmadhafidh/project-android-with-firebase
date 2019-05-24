package com.ahmad.reminderme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomizeActivity extends AppCompatActivity {

    private static final int REQUEST_COUNT = 1111;
    private int dayIdx, count;
    private RecyclerView rv;
    private List<Activity> activities = new ArrayList<>();
    private ActivityAdapter activityAdapter;
    private Button btn_simpan, btn_tambah;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize);

        if(getIntent().hasExtra("hari")){
            dayIdx = getIntent().getIntExtra("hari",-1);
        }
        if(getIntent().hasExtra("count")){
            count = getIntent().getIntExtra("count", -1);
        }

        rv = findViewById(R.id.rv);
        btn_simpan = findViewById(R.id.btn_simpan);
        btn_tambah = findViewById(R.id.btn_tambah);

        btn_tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomizeActivity.this, AddActivity.class);
                intent.putExtra("hari", dayIdx);
                intent.putExtra("count", count);
                startActivityForResult(intent, REQUEST_COUNT);
            }
        });

        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Object> mapUpdates = new HashMap<>();

                for (Activity activity : activities) {
                    mapUpdates.put(String.valueOf(activity.getId()), activity);
                }

                final ProgressDialog pd = new ProgressDialog(CustomizeActivity.this);
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setTitle("Mohon Tunggu");
                pd.setMessage("Sedang menyimpan...");
                pd.setCanceledOnTouchOutside(false);
                pd.show();

                FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(String.valueOf(dayIdx))
                        .updateChildren(mapUpdates, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                if(databaseError == null){
                                    pd.dismiss();
                                    startService(new Intent(getApplicationContext(), SyncService.class).putExtra("valid", true));

                                    finish();
                                }
                                else{
                                    Toast.makeText(CustomizeActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


        activityAdapter = new ActivityAdapter(activities, CustomizeActivity.this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(CustomizeActivity.this);
        rv.setLayoutManager(mLayoutManager);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(activityAdapter);

        final ProgressDialog pd = new ProgressDialog(CustomizeActivity.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setTitle("Mohon Tunggu");
        pd.setMessage("Sedang memuat...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        FirebaseDatabase.getInstance()
                .getReference()
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(String.valueOf(dayIdx))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        activities.clear();
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            Activity activity = child.getValue(Activity.class);
                            activities.add(activity);
                        }
                        activityAdapter.notifyDataSetChanged();
                        pd.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 1){
            count = data.getIntExtra("count", count);
        }
    }
}
