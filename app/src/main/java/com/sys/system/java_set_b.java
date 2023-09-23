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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class java_set_b extends AppCompatActivity {
    private RecyclerView quizRecycler;
    private QuizAdapter11 quizAdapter;
    private List<DataSnapshot> quizDataSnapshots;
    FloatingActionButton float23;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.side_logo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; show dialog to confirm going back to menu
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Confirmation");
                builder.setMessage("Are you sure you want to go back to the menu?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(),Java_sets.class);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        finish();
                    }
                });
                builder.setNegativeButton("No", null);
                builder.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java_set_b);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Java Normal");
        Drawable homeButton = getResources().getDrawable(R.drawable.ic_home);
        homeButton.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(homeButton);

        SpannableString text = new SpannableString(getSupportActionBar().getTitle());
        text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D6B4FC")));
        float23 = findViewById(R.id.fab23);
        quizRecycler = findViewById(R.id.quiz);
        quizRecycler.setHasFixedSize(true);
        quizRecycler.setLayoutManager(new LinearLayoutManager(this));

        quizDataSnapshots = new ArrayList<>();
        quizAdapter = new QuizAdapter11(this, quizDataSnapshots);
        quizRecycler.setAdapter(quizAdapter);

        float23.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),Java_setb_setUp.class);
                overridePendingTransition(0,0);
                startActivity(i);
                finish();
            }
        });


        // Initialize Firebase Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Java_normal");

        // Add a ValueEventListener to fetch data
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                quizDataSnapshots.clear(); // Clear the list before adding data
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    quizDataSnapshots.add(snapshot);
                }
                quizAdapter.notifyDataSetChanged(); // Notify the adapter of data changes
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database errors if any
            }
        });
    }

    public void onBackPressed(){
        Intent i = new Intent(getApplicationContext(),Java_sets.class);
        overridePendingTransition(0,0);
        startActivity(i);
        finish();
        super.onBackPressed();
    }
}
