package com.sys.system;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class User_progress extends AppCompatActivity {
    CardView pyton,java;
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_progress);
        getSupportActionBar().setTitle("User progress");
        Drawable homeButton = getResources().getDrawable(R.drawable.ic_home);
        homeButton.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(homeButton);

        SpannableString text = new SpannableString(getSupportActionBar().getTitle());
        text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D6B4FC")));
        java = findViewById(R.id.java_quiz);
        pyton = findViewById(R.id.pythons23);
        fab = findViewById(R.id.fab);

        pyton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Python",Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(),"Python user score",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(),score_view2.class);
                overridePendingTransition(0,0);
                startActivity(i);
                finish();
            }
        });

        java.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Java user score",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(),score_view.class);
                overridePendingTransition(0,0);
                startActivity(i);
                finish();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Closed",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(),Admin_page.class);
                overridePendingTransition(0,0);
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(),Admin_page.class);
        startActivity(i);
        overridePendingTransition(0,0);
        finish();
        super.onBackPressed();
    }
}