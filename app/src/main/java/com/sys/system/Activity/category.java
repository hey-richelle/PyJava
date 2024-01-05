package com.sys.system.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sys.system.R;

public class category extends AppCompatActivity {
    CardView java,pythons;
    FloatingActionButton fab;
    LottieAnimationView lottie_java,lottie_python;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        lottie_python = findViewById(R.id.python_lottie);
        lottie_java = findViewById(R.id.java_lottie);
        getSupportActionBar().setTitle("Quiz");
        Drawable homeButton = getResources().getDrawable(R.drawable.ic_home);
        homeButton.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(homeButton);

        SpannableString text = new SpannableString(getSupportActionBar().getTitle());
        text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D6B4FC")));
        fab = findViewById(R.id.fab);
        java = findViewById(R.id.java_quiz);
        pythons = findViewById(R.id.pythons23);
        lottie_java.animate().setDuration(1000).setStartDelay(300);
        lottie_python.animate().setDuration(1000).setStartDelay(300);
        java.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), easy2.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                finish();
            }
        });

        pythons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), easy3.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                finish();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), student_page.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                finish();
            }
        });


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(category.this, student_page.class);
        startActivity(intent);
        overridePendingTransition(0,0);
        super.onBackPressed();
    }
}