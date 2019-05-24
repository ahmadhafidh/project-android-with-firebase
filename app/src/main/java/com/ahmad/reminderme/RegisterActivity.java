package com.ahmad.reminderme;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {

    private Button btn_register;
    private EditText et_username, et_email, et_password, et_confirm_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btn_register = findViewById(R.id.btn_registerNew);
        et_username = findViewById(R.id.et_username_new);
        et_email = findViewById(R.id.et_email_new);
        et_password = findViewById(R.id.et_password_new);
        et_confirm_password = findViewById(R.id.et_ulangi_password_new);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                final String username = et_username.getText().toString();
                final String email = et_email.getText().toString();
                final String password = et_password.getText().toString();
                String confirmPassword = et_confirm_password.getText().toString();

                if(!username.isEmpty() && !email.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty()){
                    if(!password.equals(confirmPassword)){
                        Toast.makeText(getApplicationContext(), "Salah mengulangi password", Toast.LENGTH_SHORT).show();
                    }else{
                        final ProgressDialog pd = new ProgressDialog(RegisterActivity.this);
                        pd.setTitle("Mohon Tunggu");
                        pd.setMessage("Membuat akun baru...");
                        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pd.setCanceledOnTouchOutside(false);
                        pd.show();

                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {

                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(username).build();

                                        authResult.getUser().updateProfile(profileUpdates)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        pd.dismiss();
                                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                        startActivity(intent);
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
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.dismiss();
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }

                }else{
                    Toast.makeText(getApplicationContext(), "Belum lengkap", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
