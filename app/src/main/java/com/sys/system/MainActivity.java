package com.sys.system;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class MainActivity extends AppCompatActivity {
    // LottieAnimationView lottie;
    AppCompatButton button1;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //lottie = findViewById(R.id.lottie23);
        button1 = findViewById(R.id.login_btn);
        textView = findViewById(R.id.signup);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // lottie.animate().setDuration(3000).setStartDelay(200);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();


            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), login.class);
                    startActivity(intent);
                    finish();
                }
            });

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(),signup.class);
                    startActivity(intent);
                    finish();
                }
            });


        }
    }
}