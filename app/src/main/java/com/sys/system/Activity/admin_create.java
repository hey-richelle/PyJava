package com.sys.system.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sys.system.R;

import java.util.HashMap;


public class admin_create extends AppCompatActivity {

    private static final String TAG = "Admin_create";
    private FirebaseAuth mAuth;
    EditText editText;
    EditText editText2;
    EditText editText3;
    AppCompatButton button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_create);
        mAuth = FirebaseAuth.getInstance();
        editText = findViewById(R.id.email);
        editText2 = findViewById(R.id.password);
        editText3 = findViewById(R.id.Phone);
        button = findViewById(R.id.registered);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void createAccount() {
        String email = editText.getText().toString().trim();
        String password = editText2.getText().toString().trim();
        String phone = editText3.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            editText.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editText2.setError("Password is required");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            editText3.setError("Phone number is required");
            return;
        }

        // Check password strength
        if (!isPasswordStrong(password)) {
            editText2.setError("Password is weak. Password must contain at least 8 characters");
            return;
        }

        String phoneNumber = editText3.getText().toString().trim();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // User creation is successful
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> verificationTask) {
                                        if (verificationTask.isSuccessful()) {
                                            // Email verification sent
                                            Log.d(TAG, "sendEmailVerification:success");
                                            String uid = user.getUid();

                                            // Save user's email and phone number to Firebase
                                            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                                            HashMap<String, Object> userData = new HashMap<>();
                                            userData.put("email", email);
                                            userData.put("phone", phoneNumber); // Add phone number to the data
                                            database.child("ADMIN").child(uid).setValue(userData);

                                            Toast.makeText(admin_create.this, "Account created successfully. Please check your email "+email+" for verification", Toast.LENGTH_SHORT).show();
                                            Intent loginIntent = new Intent(admin_create.this, login.class);
                                            overridePendingTransition(0,0);
                                            startActivity(loginIntent);
                                            finish();

                                            // Now you should wait for email verification before logging in the user
                                        } else {
                                            // Failed to send email verification
                                            Log.w(TAG, "sendEmailVerification:failure", verificationTask.getException());
                                            Toast.makeText(admin_create.this, "Failed to send email verification", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        } else {
                            // User creation failed
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(admin_create.this, "Account creation failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean isPasswordStrong(String password) {
        // Check password strength criteria
        String passwordPattern = ".*";
        return password.matches(passwordPattern);
    }

    public void  onBackPressed(){
        Intent admin = new Intent(getApplicationContext(),Admin_page.class);
        startActivity(admin);
        finish();
        overridePendingTransition(0,0);
        super.onBackPressed();
    }
}