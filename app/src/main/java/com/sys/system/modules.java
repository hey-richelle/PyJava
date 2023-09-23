package com.sys.system;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class modules extends AppCompatActivity {
      TextView java23,python23;
      CardView module_java,python_module;
      FloatingActionButton float23;
      LottieAnimationView lottie_java,lottie_python;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modules);
        getSupportActionBar().setTitle("Modules");
        Drawable homeButton = getResources().getDrawable(R.drawable.ic_home);
        homeButton.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(homeButton);

        SpannableString text = new SpannableString(getSupportActionBar().getTitle());
        text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D6B4FC")));
        lottie_python = findViewById(R.id.python_lottie);
        lottie_java = findViewById(R.id.java_lottie);
        java23 = findViewById(R.id.java23);
        module_java = findViewById(R.id.java_module);
        python_module = findViewById(R.id.python_module);
        python23 = findViewById(R.id.python_23);
        float23 = findViewById(R.id.fab);

        lottie_java.animate().setDuration(1000).setStartDelay(300);
        lottie_python.animate().setDuration(1000).setStartDelay(300);

        python_module.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(modules.this, python_student.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                finish();
            }
        });
        module_java.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(modules.this, student_java.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                finish();
            }
        });



        float23.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(modules.this);
                builder.setTitle("Confirm Action");
                builder.setMessage("Are you sure you want to go back to the main menu?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(modules.this, student_page.class);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        finish();

                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });


        DatabaseReference java = FirebaseDatabase.getInstance().getReference().child("Java");
        final int[] java_count = {0};
        java.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                java_count[0] = (int) dataSnapshot.getChildrenCount();
                java23.setText(String.valueOf(java_count[0]));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors
            }
        });


        DatabaseReference Pyhton = FirebaseDatabase.getInstance().getReference().child("Python");
        final int[] python_count = {0};
        Pyhton.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                python_count[0] = (int) dataSnapshot.getChildrenCount();
                python23.setText(String.valueOf(python_count[0]));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors
            }
        });



    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(modules.this, student_page.class);
        startActivity(intent);
        overridePendingTransition(0,0);
        super.onBackPressed();
    }
}