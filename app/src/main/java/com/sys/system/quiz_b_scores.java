package com.sys.system;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class quiz_b_scores extends AppCompatActivity {
    Button exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();

            setContentView(R.layout.activity_quiz_bscores);
            TextView user_id, score;
            user_id = findViewById(R.id.user_id);
            score = findViewById(R.id.score);
            exit = findViewById(R.id.exits2);
            // Set the title of the action bar


            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(quiz_b_scores.this, "Back to QUIZ", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), easy2.class);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    finish();
                }
            });

            // Retrieve user ID from FirebaseAuth
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();
                user_id.setText(userId);
            }

            // Retrieve score and total questions from Firebase Realtime Database
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
            databaseRef.child("Java_normal_score").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        int quizScore = dataSnapshot.getValue(Integer.class);
                        score.setText("Score: " + quizScore);

                        // Center the score vertically
                        score.setGravity(Gravity.CENTER_VERTICAL);


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("Easy mode", "Failed to read value.", databaseError.toException());
                }
            });

        }
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(quiz_b_scores.this, easy2.class);
        startActivity(intent);
        overridePendingTransition(0,0);
        super.onBackPressed();
    }
}
