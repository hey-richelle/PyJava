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
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Update_set_b extends AppCompatActivity {

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
                        Intent intent = new Intent(getApplicationContext(), java_set_b.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
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

    private EditText mQuestionTextEditText;
    private EditText mAnswerTextEditText;
    private Button mSaveQuestionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_set_b);

        // Get the home button drawable
        getSupportActionBar().setTitle("Java Normal");
        Drawable homeButton = getResources().getDrawable(R.drawable.ic_home);
        homeButton.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(homeButton);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SpannableString text = new SpannableString(getSupportActionBar().getTitle());
        text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D6B4FC")));

        mQuestionTextEditText = findViewById(R.id.et_question);
        mAnswerTextEditText = findViewById(R.id.et_correct_answer);
        mSaveQuestionButton = findViewById(R.id.btn_save_question);

        String question = getIntent().getStringExtra("question");
        String answer = getIntent().getStringExtra("answer");

        // Set the retrieved data to the respective EditText fields
        mQuestionTextEditText.setText(question);
        mAnswerTextEditText.setText(answer);

        mSaveQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get entered data
                String question = mQuestionTextEditText.getText().toString();
                String answer = mAnswerTextEditText.getText().toString();

                if (TextUtils.isEmpty(question) || TextUtils.isEmpty(answer)) {
                    // Show error message to user
                    Toast.makeText(Update_set_b.this, "Please fill in the question and the correct answer.", Toast.LENGTH_SHORT).show();
                } else {
                    // Retrieve the question ID passed from the previous activity
                    String questionId = getIntent().getStringExtra("questionId");

                    // Store updated data in Firebase Realtime Database
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference questionsRef = database.child("Java_normal").child(questionId);

                    // Update the question and answer
                    questionsRef.child("question").setValue(question);
                    questionsRef.child("answer").setValue(answer);

                    // Show success message to user
                    Toast.makeText(Update_set_b.this, "Question updated successfully!", Toast.LENGTH_SHORT).show();

                    // Navigate back to the quiz_content activity
                    Intent intent = new Intent(Update_set_b.this, java_set_b.class);
                    startActivity(intent);
                    finish(); // Close the current activity to avoid going back to it on back press
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), java_set_b.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        super.onBackPressed();
    }
}
