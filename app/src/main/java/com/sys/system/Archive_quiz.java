package com.sys.system;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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
public class Archive_quiz extends AppCompatActivity {
    private FloatingActionButton float23;
    private RecyclerView archiveRecyclerView;
    private ArchiveQuizAdapter archiveQuizAdapter;
    private DatabaseReference archiveDatabaseReference;
    private DatabaseReference mainDatabaseReference; // Reference to the main database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive_quiz);
        getSupportActionBar().setTitle("Archive quiz");

        Drawable homeButton = getResources().getDrawable(R.drawable.ic_home);

// Set its color to black
        homeButton.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);

// Set the modified drawable as the home button icon
        getSupportActionBar().setHomeAsUpIndicator(homeButton);

// Enable the home button on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

// Set the color of the title to black
        SpannableString text = new SpannableString(getSupportActionBar().getTitle());
        text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D6B4FC")));
        float23 = findViewById(R.id.bottom_navigation);
        archiveRecyclerView = findViewById(R.id.quiz);
        archiveRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get references to your Firebase archive and main database nodes
        archiveDatabaseReference = FirebaseDatabase.getInstance().getReference("archive");
        mainDatabaseReference = FirebaseDatabase.getInstance().getReference();

        // Initialize your adapter for archived quizzes
        archiveQuizAdapter = new ArchiveQuizAdapter(new ArrayList<>(), mainDatabaseReference); // Pass the main database reference
        archiveRecyclerView.setAdapter(archiveQuizAdapter);

        float23.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Admin_page.class);
                startActivity(intent);
                overridePendingTransition(0, 0); // Disable animation
                finish();
            }
        });


        // Retrieve archived quiz names from Firebase and update the adapter
        retrieveArchivedQuizNames();
    }

    private void retrieveArchivedQuizNames() {
        archiveDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> archivedQuizNames = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String archivedQuizName = snapshot.getKey();
                    archivedQuizNames.add(archivedQuizName);
                }

                archiveQuizAdapter.setArchivedQuizNames(archivedQuizNames);
                archiveQuizAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled if needed
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Archive_quiz.this, Admin_page.class);
        startActivity(intent);
        overridePendingTransition(0, 0); // Disable animation
        super.onBackPressed();
    }

}
